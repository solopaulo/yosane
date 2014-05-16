package au.com.twobit.yosane.service.storage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;

import au.com.twobit.yosane.api.ImageStatus;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class FileStorage implements Storage {
    private String holdingArea;
    private String imageOutputFormat;
    private final static String IMAGE_FILE_NAME = "image";
    private final static String THUMB_FILE_NAME = "thumb";
    private final static String STATUS_FILE_NAME = "status";

    @Inject
    public FileStorage(@Named("holdingArea") String holdingArea, @Named("imageOutputFormat") String imageOutputFormat) {
        this.holdingArea = holdingArea;
        this.imageOutputFormat = imageOutputFormat;
    }

    @Override
    public void saveImage(BufferedImage image, String imageIdentifier) throws StorageException {
        save(image, imageIdentifier, IMAGE_FILE_NAME);
    }

    @Override
    public void saveImageThumbnail(BufferedImage image, String imageIdentifier) throws StorageException {
        save(image, imageIdentifier, THUMB_FILE_NAME);
    }

    protected void save(BufferedImage image, String imageIdentifier, String filename) throws StorageException {
        File imagedir = getImageAreaDirectory(imageIdentifier);
        // determine the path of the file
        String imageFilePath = Joiner.on(File.separator).join(imagedir.getPath(), filename);
        // write the image data to disk
        try {
            if (image != null)
                ImageIO.write(image, imageOutputFormat, new File(imageFilePath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageException("Failed to write file to disk: " + e.getMessage());
        }
    }

    @Override
    public BufferedImage loadImage(String imageIdentifier) throws StorageException {
        return load(imageIdentifier, IMAGE_FILE_NAME);
    }

    @Override
    public BufferedImage loadImageThumbnail(String imageIdentifier) throws StorageException {
        return load(imageIdentifier, THUMB_FILE_NAME);
    }

    protected BufferedImage load(String imageIdentifier, String filename) throws StorageException {
        File imagedir = getImageAreaDirectory(imageIdentifier);
        String filepath = Joiner.on(File.separator).join(imagedir.getPath(), filename);
        File file = new File(filepath);
        try {
            return ImageIO.read(file);
        } catch (Exception x) {
            x.printStackTrace();
        }
        throw new StorageException("Unable to read from file with identifier " + imageIdentifier);
    }

    @Override
    public void updateStatus(ImageStatus status, String imageIdentifier) throws StorageException {
        File imagedir = getImageAreaDirectory(imageIdentifier);
        String filepath = Joiner.on(File.separator).join(imagedir.getPath(), STATUS_FILE_NAME);
        try {
            Files.write(status.name().getBytes(), new File(filepath));
        } catch (Exception x) {
            // log an error
            throw new StorageException(x.getMessage());
        }
    }

    @Override
    public ImageStatus getStatus(String imageIdentifier) throws StorageException {
        String filepath = Joiner.on(File.separator).join(holdingArea, imageIdentifier, STATUS_FILE_NAME);
        try {
            return ImageStatus.valueOf(Files.toString(new File(filepath), Charset.defaultCharset()));
        } catch (Exception x) {
        }
        return ImageStatus.FAILED;
    }

    @Override
    public void assertStatus(String imageIdentifier, ImageStatus status) throws StorageException {
        ImageStatus currentStatus = getStatus(imageIdentifier) ; 
        if ( status != currentStatus ) {
            throw new StorageException(String.format("Asserted status %s but instead it was %s",status.name(),currentStatus.name()));
        }
        
    }


    protected File getImageAreaDirectory(String imageIdentifier) throws StorageException {
        // check storage is available first as that is probably cheaper option
        File imagedir = new File(Joiner.on(File.separator).join(holdingArea, imageIdentifier));
        // create dir if not exists
        if (!(imagedir.exists() || imagedir.mkdirs())) {
            throw new StorageException("Path cannot be created: " + imagedir.getPath());
        } else if (!(imagedir.isDirectory() && imagedir.canWrite())) {
            throw new StorageException("Path is not writable: " + imagedir.getPath());
        }
        return imagedir;
    }

}
