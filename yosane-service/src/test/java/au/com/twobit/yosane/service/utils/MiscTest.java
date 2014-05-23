package au.com.twobit.yosane.service.utils;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.junit.Assert;
import org.junit.Test;

import au.com.twobit.yosane.service.resource.ImagesResource;

import com.theoryinpractise.halbuilder.api.Link;

public class MiscTest {

    @Test
    public void testUriBuilder() throws Exception {
        URI uri = UriBuilder.fromResource(ImagesResource.class).path( ImagesResource.class.getMethod("getImageFile",String.class)).build("abcd1234");
        Assert.assertNotNull(uri);
        System.out.println(uri.toString());
    }
    
    @Test
    public void testHalBuilderLink() {
        Link link = new Link(null,"relation","url");
        Assert.assertNotNull(link);
        Assert.assertEquals("relation", link.getRel());
        Assert.assertEquals("url", link.getHref());
    }

}
