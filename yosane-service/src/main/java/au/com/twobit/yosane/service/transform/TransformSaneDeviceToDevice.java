package au.com.twobit.yosane.service.transform;

import au.com.southsky.jfreesane.SaneDevice;
import au.com.twobit.yosane.api.Device;
import au.com.twobit.yosane.service.utils.EncodeDecode;

import com.google.common.base.Function;
import com.google.inject.Inject;

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
        Device d = new Device();
        d.setVendor(device.getVendor());
        d.setModel(device.getModel());
        d.setType(device.getType());
        d.setName(device.getName());
        d.setId(coder.encodeString(device.getName()));
        return d;
    }

}
