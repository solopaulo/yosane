package au.com.twobit.yosane.service.guice;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Inject;

import au.com.twobit.yosane.api.Device;
import au.com.twobit.yosane.api.DeviceOption;
import au.com.twobit.yosane.service.device.ScanHardware;
import au.com.twobit.yosane.service.utils.EncodeDecode;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.google.inject.AbstractModule;

public class MockSaneDependencyModule extends AbstractModule {
    final List<Device> devices = Lists.newArrayList();
    private boolean done = false;
    private int imageIndex = 0;

    @Inject
    private EncodeDecode coder;

    public MockSaneDependencyModule() {
    }

    @PostConstruct
    public void init() {
        if (done) {
            return;
        }
        done = true;
        String name = "mustek:0xs41s92";
        devices.add(new Device("Mustek", "McMuster", "Flatbed A3 Scanner", name, coder.encodeString(name)));
        name = "fujitsu:00sd9x8";
        devices.add(new Device("Fujitsu", "Firewire Flatbed Scanner", "Slimline Flatbed", name, coder.encodeString(name)));
    }

    @Override
    protected void configure() {
        ScanHardware mockHardware = new ScanHardware() {

            @Override
            public List<Device> getListOfScanDevices() {
                init();
                return devices;
            }

            @Override
            public Device getScanDeviceDetails(String scanDeviceIdentifier) throws IllegalArgumentException {
                for (Device d : getListOfScanDevices()) {
                    if (d.getName().equals(scanDeviceIdentifier)) {
                        return d;
                    }
                }
                throw new IllegalArgumentException("No device found");
            }

            @Override
            public List<DeviceOption> getScanDeviceOptions(String scanDeviceIdentifier) throws IllegalArgumentException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public BufferedImage acquireImage(String scanDeviceIdentifier, String ticket, DeviceOption... options) throws IllegalArgumentException, Exception {
                BufferedImage img = null;
                try {
                    Thread.sleep(1500);
                } catch (Exception x) {
                }
                try {
                    // open the image resource list
                    InputStream is = getClass().getClassLoader().getResourceAsStream("mockimages/resourcelist.txt");
                    // read in the file listing
                    String[] files = CharStreams.toString(new InputStreamReader(is)).split("\n");
                    Closeables.close(is, true);

                    String file = files[imageIndex++ % files.length];
                    img = ImageIO.read(getClass().getClassLoader().getResourceAsStream(String.format("mockimages/%s", file)));
                } catch (Exception x) {
                    x.printStackTrace();
                }
                return img;
            }

        };
        bind(ScanHardware.class).toInstance(mockHardware);
    }

}
