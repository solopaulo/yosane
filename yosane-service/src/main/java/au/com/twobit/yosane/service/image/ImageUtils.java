package au.com.twobit.yosane.service.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class ImageUtils {
    public static byte[] createByteArrayFromImage(BufferedImage image, String imageFormat) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, imageFormat, stream);
        return stream.toByteArray();
    }
}
