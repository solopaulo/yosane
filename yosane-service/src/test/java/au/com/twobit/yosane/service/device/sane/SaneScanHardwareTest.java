package au.com.twobit.yosane.service.device.sane;

import org.junit.Assert;
import org.junit.Test;


public class SaneScanHardwareTest {

    @Test
    public void testRegex() {
        Assert.assertEquals("pixma", "pixma:net:123241_12312".replaceAll("^(\\w+).*", "$1"));
    }

}
