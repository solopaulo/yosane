package au.com.twobit.yosane.service.op.command;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

import au.com.twobit.yosane.service.image.ImageFormat;
import au.com.twobit.yosane.service.op.command.CreateThumbnail;

public class CreateThumbnailTest {

    @Test
    public void testResize() throws Exception {
        File fileInput = null;
        try {
            Path path = Paths.get( getClass().getResource("/").toURI());
            fileInput = path.resolve("../../src/main/resources/mockimages/DSC05828.JPG").toFile();
        } catch (Exception x) {
            Assert.fail(x.getMessage());
        }        
        BufferedImage input = ImageIO.read( fileInput );
        CreateThumbnail creator = new CreateThumbnail(input);
        CreateThumbnail.setScaleWidth(350);
        BufferedImage output = creator.call();
        File result = File.createTempFile("prefix","suffix");
        ImageIO.write(output, ImageFormat.png.name(), result);
        Assert.assertTrue( result.exists() && result.canRead() && result.length() > 0);
        Assert.assertTrue( result.delete() );
    }

    @Test
    public void testSomePathStuff() throws Exception {
        Path path = Paths.get( getClass().getResource("/").toURI());
        File dir = path.resolve("../../src/main/resources/mockimages").toFile();
        System.out.println( dir.isDirectory() && dir.exists());
    }
}
