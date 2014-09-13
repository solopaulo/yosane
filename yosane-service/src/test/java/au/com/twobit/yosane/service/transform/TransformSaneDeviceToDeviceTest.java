package au.com.twobit.yosane.service.transform;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.southsky.jfreesane.SaneDevice;
import au.com.twobit.yosane.api.Device;
import au.com.twobit.yosane.service.utils.EncodeDecode;
import au.com.twobit.yosane.service.utils.URLEncodeDecode;

public class TransformSaneDeviceToDeviceTest {

    final String DEV_MODEL      = "model name";
    final String DEV_VENDOR     = "vendor name";
    final String DEV_NAME       = "device name";
    final String DEV_TYPE       = "device type";
    
    private TransformSaneDeviceToDevice transform;
    private EncodeDecode coder;
    
    @Before
    public void onSetup() {
        transform = new TransformSaneDeviceToDevice();
        coder = new URLEncodeDecode();
    }
    
    @Test
    public void testNullDevice() {
        Assert.assertNull(transform.apply(null));
    }
    
    @Test
    public void testNormalTransformation() {
        transform.setCoder( coder );
        SaneDevice device = mock(SaneDevice.class);
        when(device.getModel()).thenReturn(DEV_MODEL);
        when(device.getName()).thenReturn(DEV_NAME);
        when(device.getVendor()).thenReturn(DEV_VENDOR);
        when(device.getType()).thenReturn(DEV_TYPE);
        
        Device d = transform.apply(device);
        Assert.assertEquals( coder.encodeString(device.getName()),d.getId());
               
    }

}
