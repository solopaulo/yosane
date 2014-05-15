package au.com.twobit.yosane.service.resource;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Optional;

public class ImagesResourceTest {

    @Test
    public void testOptional() {
        String nullString = null;
        String defaultString = "nonNull";
        Optional<String> optional = Optional.fromNullable(nullString);
        Assert.assertEquals(defaultString, optional.or(defaultString));
    }

    @Test
    public void testOptionalSomeMore() {
        String nullString = null;
        String defaultString = "nonNull";
        Optional<String> optional = Optional.fromNullable(defaultString);
        Assert.assertEquals(defaultString, optional.or("somethingElse"));
    }

}
