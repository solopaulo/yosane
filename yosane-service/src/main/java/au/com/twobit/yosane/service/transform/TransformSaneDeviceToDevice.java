package au.com.twobit.yosane.service.transform;

import org.junit.Before;

import au.com.southsky.jfreesane.SaneDevice;
import au.com.twobit.yosane.api.Device;
import au.com.twobit.yosane.service.utils.EncodeDecode;
import au.com.twobit.yosane.service.utils.URLEncodeDecode;

import com.google.common.base.Function;

public class TransformSaneDeviceToDevice implements Function<SaneDevice, Device> {
    private EncodeDecode coder;
    
    @Before
    public void onSetup() {
        coder = new URLEncodeDecode();
    }
    
    @Override
    public Device apply(SaneDevice device) {
        if ( device == null ) {
            return null;
        }
        Device d = new Device();
        d.setVendor(device.getVendor());
        d.setModel(device.getModel());
        d.setType(device.getType());
        d.setName(device.getName());
        d.setId(coder.encodeString(device.getName()));
        return d;
    }

}
