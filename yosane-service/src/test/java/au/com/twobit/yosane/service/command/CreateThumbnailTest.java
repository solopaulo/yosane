package au.com.twobit.yosane.service.command;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

import au.com.twobit.yosane.service.image.ImageFormat;
import au.com.twobit.yosane.service.op.command.CreateThumbnail;

public class CreateThumbnailTest {

    @Test
    public void testResize() throws Exception {
        File result = new File("/tmp/yosane/test.thumbnail.png");
        if ( result.exists() && result.canWrite() ) {
            result.delete();
        }
        BufferedImage input = ImageIO.read( new File("/tmp/yosane/test.png"));
        BufferedImage output = new CreateThumbnail(input).call();
        ImageIO.write(output, ImageFormat.png.name(), result);
        Assert.assertTrue( result.exists() && result.canRead() && result.length() > 0);
    }

}
