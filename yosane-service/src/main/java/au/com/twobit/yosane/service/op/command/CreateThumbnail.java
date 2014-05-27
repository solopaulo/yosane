package au.com.twobit.yosane.service.op.command;

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

public class CreateThumbnail implements Callable<BufferedImage> {
    private final BufferedImage input;
    private static int scaleWidth;
    
    public CreateThumbnail(BufferedImage input) {
        this.input = input;
    }
    
    @Inject
    static void setScaleWidth( @Named("scaleWidth") int scaleWidth) {
        CreateThumbnail.scaleWidth = scaleWidth;
    }

    @Override
    public BufferedImage call() throws Exception {
        // make a thumbnail of the image
        if ( scaleWidth <= 0 ) {
            throw new Exception("Scale width is not set");
        }
        return Scalr.resize(input, Method.ULTRA_QUALITY, Mode.FIT_TO_WIDTH, scaleWidth);        
    }
}
