package au.com.twobit.yosane.service.transform;

import javax.inject.Inject;

import au.com.southsky.jfreesane.SaneDevice;
import au.com.twobit.yosane.api.Device;
import au.com.twobit.yosane.service.utils.EncodeDecode;

import com.google.common.base.Function;

public class TransformSaneDeviceToDevice implements Function<SaneDevice, Device> {
    private EncodeDecode coder;
    
    @Inject
    public void setCoder(EncodeDecode coder) {
        this.coder = coder;
    }
    
    @Override
    public Device apply(SaneDevice device) {
        if ( device == null ) {
            return null;
        }
        String name = device.getName();
        Device d = new Device(device.getVendor(),device.getModel(),device.getType(),name,coder.encodeString(name));
        return d;
    }

}
