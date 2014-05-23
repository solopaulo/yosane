package au.com.twobit.yosane.service.utils;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.junit.Assert;
import org.junit.Test;

import au.com.twobit.yosane.service.resource.ImagesResource;

public class MiscTest {

    @Test
    public void testUriBuilder() throws Exception {
        URI uri = UriBuilder.fromResource(ImagesResource.class).path( ImagesResource.class.getMethod("getImageFile",String.class)).build("abcd1234");
        Assert.assertNotNull(uri);
        System.out.println(uri.toString());
    }

}
