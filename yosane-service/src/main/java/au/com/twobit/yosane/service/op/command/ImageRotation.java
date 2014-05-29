package au.com.twobit.yosane.service.op.command;

import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Rotation;

import au.com.twobit.yosane.api.ImageStatus;
import au.com.twobit.yosane.service.image.RotateDirection;
import au.com.twobit.yosane.service.storage.Storage;
import au.com.twobit.yosane.service.storage.StorageException;

public class ImageRotation implements Runnable {

    final private Storage storage;
    final private String imageIdentifier;
    final private RotateDirection direction;
    
    public ImageRotation(Storage storage, String imageIdentifier,RotateDirection direction) {
        this.storage = storage;
        this.imageIdentifier = imageIdentifier;
        this.direction = direction;
    }
    
    @Override
    public void run() {
        BufferedImage image = null;
        BufferedImage thumb = null;
        
        try {
            image = storage.loadImage(imageIdentifier);
            thumb = storage.loadImageThumbnail(imageIdentifier);
        } catch (StorageException x) {
            x.printStackTrace();
            // log an error and return;
            return;
        }
        
        // update status to processing
        try {
            storage.updateImageStatus(ImageStatus.PROCESSING, imageIdentifier);
        } catch (StorageException x) {
            x.printStackTrace();
            // log an error and return;
            return;
        }
        
        Rotation rotation = direction == RotateDirection.CW ? Rotation.CW_90 : Rotation.CW_270;
        // rotate image
        BufferedImage rotatedImage = Scalr.rotate(image, rotation);
        // rotate thumbnail
        BufferedImage rotatedThumb = Scalr.rotate(thumb,  rotation);
        
        try {
            storage.saveImage(rotatedImage, imageIdentifier);
            storage.saveImageThumbnail(rotatedThumb, imageIdentifier);
        } catch (StorageException x) {
            x.printStackTrace();
            // log an error
        }
    }

}
