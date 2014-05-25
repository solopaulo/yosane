package au.com.twobit.yosane.service.utils;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatterBuilder;
import org.junit.Assert;
import org.junit.Test;

import au.com.twobit.yosane.service.resource.ImagesResource;

import com.theoryinpractise.halbuilder.api.Link;

public class MiscTest {

    @Test
    public void testUriBuilder() throws Exception {
        URI uri = UriBuilder.fromResource(ImagesResource.class).path( ImagesResource.class.getMethod("getImageFile",String.class)).build("abcd1234");
        Assert.assertNotNull(uri.toString(),uri);
    }
    
    @Test
    public void testHalBuilderLink() {
        Link link = new Link(null,"relation","url");
        Assert.assertNotNull(link);
        Assert.assertEquals("relation", link.getRel());
        Assert.assertEquals("url", link.getHref());
    }
    
    @Test
    public void testPeriodsSubtract() {
        DateTime fifteenDaysAgo = DateTime.now().minusDays(15);
        DateTime now = DateTime.now();
        Assert.assertTrue( InformalPeriod.subtractPeriodFromDate("1w4d", now).isAfter(fifteenDaysAgo));
        Assert.assertTrue( InformalPeriod.subtractPeriodFromDate("2w3d",now).isBefore(fifteenDaysAgo));
    }

    @Test
    public void testPeriodsAdd() {
        DateTime fifteenDaysFromNow = DateTime.now().plusDays(15);
        DateTime now = DateTime.now();
        Assert.assertTrue( InformalPeriod.addPeriodToDate("1w4d", now).isBefore(fifteenDaysFromNow));
        Assert.assertTrue( InformalPeriod.addPeriodToDate("2w3d",now).isAfter(fifteenDaysFromNow));

    }
}
