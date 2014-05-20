package au.com.twobit.yosane.service.transform;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import au.com.southsky.jfreesane.OptionValueType;
import au.com.southsky.jfreesane.SaneOption;
import au.com.twobit.yosane.api.DeviceOption;


public class TransformSaneOptionToDeviceOptionTest {
    final static String OPT_NAME    = "option name";
    final static String OPT_DESC    = "option description";
    final static String OPT_TITLE   = "option title";
    final static String OPT_TYPE    = "option type";
    
    private TransformSaneOptionToDeviceOption transform;
    
    @Before
    public void onSetup() {
        transform = new TransformSaneOptionToDeviceOption();
    }
    
    @Test
    public void testTransformOnNull() {
        Assert.assertNull(transform.apply(null));
    }
    
    @Test
    public void testNormalTransformation() {
        SaneOption so = Mockito.mock(SaneOption.class);
        Mockito.when(so.getName()).thenReturn(OPT_NAME);
        Mockito.when(so.getDescription()).thenReturn(OPT_DESC);
        Mockito.when(so.getTitle()).thenReturn(OPT_TITLE);
        Mockito.when(so.getType()).thenReturn(OptionValueType.STRING);
        
        DeviceOption dopt = transform.apply(so);
        Assert.assertNotNull(dopt);
        Assert.assertEquals(OPT_NAME, dopt.getName());
        Assert.assertEquals(OPT_DESC, dopt.getDescription());
        Assert.assertEquals(OPT_TITLE, dopt.getTitle());
        Assert.assertEquals(OptionValueType.STRING.name(), dopt.getType());
    }

}
