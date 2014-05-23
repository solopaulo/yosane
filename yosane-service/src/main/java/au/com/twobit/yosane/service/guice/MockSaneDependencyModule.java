package au.com.twobit.yosane.service.guice;

import java.awt.image.BufferedImage;
import java.util.List;

import javax.annotation.PostConstruct;

import au.com.twobit.yosane.api.Device;
import au.com.twobit.yosane.api.DeviceOption;
import au.com.twobit.yosane.service.device.ScanHardware;
import au.com.twobit.yosane.service.utils.EncodeDecode;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;

public class MockSaneDependencyModule extends AbstractModule {
    final List<Device>devices = Lists.newArrayList();
    private boolean done = false;
    
    @Inject
    private EncodeDecode coder;
    
    public MockSaneDependencyModule() {
    }

    @PostConstruct public void init() {
        if ( done ) {
            return;
        }
        done = true;
        Device device = new Device();
        device.setName("mustek:0xs41s92");
        device.setModel("McMuster");
        device.setType("Flatbed A3 Scanner");
        device.setVendor("Mustek");
        device.setId( coder.encodeString(device.getName()));
        devices.add(device);
        
        device = new Device();
        device.setName("fujitsu:00sd9x8");
        device.setModel("Firewire Flatbed Scanner");
        device.setType("Slimline Flatbed");
        device.setVendor("Fujitsu");
        device.setId(coder.encodeString(device.getName()));
        devices.add(device);
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
                    if ( d.getName().equals( scanDeviceIdentifier ) ) {
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
                // TODO Auto-generated method stub
                return null;
            }
            
        };
        bind(ScanHardware.class).toInstance( mockHardware );
    }

}
