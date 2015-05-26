package au.com.twobit.yosane.service.device.sane;

import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.southsky.jfreesane.OptionValueType;
import au.com.southsky.jfreesane.SaneDevice;
import au.com.southsky.jfreesane.SaneOption;
import au.com.southsky.jfreesane.SanePasswordProvider;
import au.com.southsky.jfreesane.SaneSession;
import au.com.twobit.yosane.api.Device;
import au.com.twobit.yosane.api.DeviceOption;
import au.com.twobit.yosane.service.device.AcquisitionException;
import au.com.twobit.yosane.service.device.ScanHardware;
import au.com.twobit.yosane.service.transform.TransformSaneDeviceToDevice;
import au.com.twobit.yosane.service.transform.TransformSaneOptionToDeviceOption;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class SaneScanHardware implements ScanHardware {
    private Logger log = LoggerFactory.getLogger(getClass());
    final public static int DEFAULT_SANE_PORT = 6566;
    final private String CACHE_KEY_DEVICES = "cache_devices";
    final private String CACHE_KEY_DEVICE_OPTIONS = "cache_options";
    final private int sanePort;
    final private InetAddress saneAddress;
    private Cache<String, Object> cache;
    private TransformSaneDeviceToDevice transformSaneDevice;
    
    @Inject
    public SaneScanHardware(@Named("saneHost") String saneHost, @Named("sanePort") int sanePort) throws UnknownHostException {
        saneAddress = InetAddress.getByName(saneHost);
        this.sanePort = sanePort;
        initializeCache();
    }
    
    @Inject
    public void setTransformSaneDevice(TransformSaneDeviceToDevice transformSaneDevice) {
        this.transformSaneDevice = transformSaneDevice;
    }
    

    private void initializeCache() {
        cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
    }

    /**
     * Gets a list of available scan devices
     * 
     * @throws ExecutionException
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Device> getListOfScanDevices() {
        List<Device> deviceList = null;
        try {
            deviceList = (List<Device>) cache.get(CACHE_KEY_DEVICES, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    List<Device> devices = Lists.newArrayList();
                    SaneSession session = null;
                    try {
                        session = createSaneSession();
                        devices.addAll(Collections2.transform(session.listDevices(), transformSaneDevice));
                    } catch (Exception x) {
                        x.printStackTrace();
                        // log error
                    } finally {
                        closeSaneSession(session);
                    }
                    return devices;
                }
            });
        } catch (ExecutionException x) {
            // log an error
            x.printStackTrace();
        }
        return deviceList;
    }

    /**
     * Gets the details of a sane scan device
     * 
     * @param scanDeviceIdentifier
     *            The name of the scanner as provided in the list of scan
     *            devices
     * @return {@link Device} The scan device
     */
    @Override
    public Device getScanDeviceDetails(final String scanDeviceIdentifier) throws IllegalArgumentException {
        try {
            return Collections2.filter(getListOfScanDevices(), new Predicate<Device>() {
                @Override
                public boolean apply(Device device) {
                    return device.getName().equals(scanDeviceIdentifier);
                }
            }).iterator().next();
        } catch (Exception x) {
            throw new IllegalArgumentException(x.getMessage());
        }
    }

    @Override
    public BufferedImage acquireImage(String scanDeviceIdentifier, String ticket, DeviceOption... options) throws AcquisitionException {
        // check if scanner is available
        if (!scannerIsAvailable(scanDeviceIdentifier)) {
            throw new AcquisitionException(String.format("Device is busy: %s", scanDeviceIdentifier));
        }
        return acquireImageFromScanner(scanDeviceIdentifier, options);
    }

    /**
     * Gets a list of compatible options the scanner allows users to set (CACHE)
     * 
     * @param scanDeviceIdentifier
     *            The scanner device identifier to identify the appropriate
     *            device
     * @return Returns a list of device options for the identified scanner
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<DeviceOption> getScanDeviceOptions(final String scanDeviceIdentifier) throws IllegalArgumentException {
        String key = Joiner.on("_").join(CACHE_KEY_DEVICE_OPTIONS, scanDeviceIdentifier);
        List<DeviceOption> options = null;
        try {
            options = (List<DeviceOption>) cache.get(key, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return getScanDeviceOptionsFromSane(scanDeviceIdentifier);
                }
            });
        } catch (ExecutionException x) {
            x.printStackTrace();
            throw new IllegalArgumentException(x.getCause().getMessage());
        }
        return options;
    }

    /**
     * Gets the options for a device from Sane
     * 
     * @param scanDeviceIdentifier
     * @return
     */
    List<DeviceOption> getScanDeviceOptionsFromSane(final String scanDeviceIdentifier) throws Exception{
        List<DeviceOption> options = Lists.newArrayList();
        SaneSession session = null;
        SaneDevice device = null;
        String capturedError = null;
        try {
            // get a list of devices from sane
            session = createSaneSession();
            session.setPasswordProvider(SanePasswordProvider.usingDotSanePassFile());
            device = session.getDevice(scanDeviceIdentifier);
            device.open();
            options.addAll(Collections2.transform(device.listOptions(), new TransformSaneOptionToDeviceOption()));
        } catch (Exception x) {
            log.error(x.getMessage());
            capturedError = String.format("Unable to get device options: %s",x.getMessage());
        } finally {
            try {
                device.close();
            } catch (Exception x) {
            }
            closeSaneSession(session);
        }
        // if there was an error prior to closing the device, throw it
        if ( ! Strings.isNullOrEmpty(capturedError) ) {
            throw new Exception(capturedError);
        }
        return options;
    }

    /* verify if the scanner is really available and not in use */
    private boolean scannerIsAvailable(String scanDeviceIdentifier) {
        boolean available = false;
        SaneSession session = null;
        try {
            session = createSaneSession();
            SaneDevice device = session.getDevice(scanDeviceIdentifier);
            available = !device.isOpen();
        } catch (Exception x) {
            // error
        } finally {
            closeSaneSession(session);
        }
        return available;
    }

    /* acquire an image and write it to disk */
    private BufferedImage acquireImageFromScanner(String scanDeviceIdentifier, DeviceOption[] options) throws AcquisitionException {
        SaneSession session = null;
        BufferedImage image = null;
        try {
            // get the resource name from the full name listed by device list
            String name = scanDeviceIdentifier.replaceAll("^(\\w+).*", "$1");
            // create a new sane session
            session = createSaneSession();
            // set password provider
            session.setPasswordProvider(SanePasswordProvider.usingDotSanePassFile());
            // get the device handle for the resource
            SaneDevice device = session.getDevice(name);
            // open the device
            device.open();
            if (options != null && options.length > 0) {
                // get the option list and set options as appropriate
                for (DeviceOption option : options) {
                    SaneOption so  = device.getOption(option.getName());
                    if ( so == null ) {
                        continue;
                    }
                    setSaneOptionValue(so,option.getValue());
                }
            }
            // acquire the image
            image = device.acquireImage();
        } catch (Exception x) {
            x.printStackTrace();
            // error
            log.error("Unable to open device with identifier: {}", scanDeviceIdentifier);
            throw new AcquisitionException(String.format("Scan failed: %s", x.getMessage()), x);
        } finally {
            closeSaneSession(session);
        }
        return image;
    }

    private void setSaneOptionValue(SaneOption so, String value) {
        if ( so == null ) {
            return;
        } else  if ( Strings.isNullOrEmpty(value) && so.getType() != OptionValueType.STRING ) {
            return;
        }
        value = Optional.fromNullable(value).or("");
        try {
            switch(so.getType()) {
                case BOOLEAN:
                    so.setBooleanValue( Boolean.valueOf(value) );
                    break;
                case FIXED:
                    so.setFixedValue( Double.parseDouble( value ) );
                    break;
                case INT:
                    so.setIntegerValue( Integer.parseInt(value) );
                    break;
                case STRING:
                    so.setStringValue(value);
                    break;
                default:
            }
        } catch (Exception x) {
            x.printStackTrace();
            return;
        }
    }

    /* Create a new sane session */
    private SaneSession createSaneSession() throws Exception {
        SaneSession session = null;
        try {
            session = SaneSession.withRemoteSane(saneAddress, sanePort);
        } catch (Exception x) {
            x.printStackTrace();
            // error
        }
        return session;
    }

    /* Clean up the sane session */
    private void closeSaneSession(SaneSession session) {
        try {
            session.close();
        } catch (Throwable x) {
            x.printStackTrace();
        }
    }
}
