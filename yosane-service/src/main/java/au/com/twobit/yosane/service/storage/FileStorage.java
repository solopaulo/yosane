package au.com.twobit.yosane.service.storage;

import static au.com.twobit.yosane.service.storage.FileStorage.PathFor.DOCUMENTS;
import static au.com.twobit.yosane.service.storage.FileStorage.PathFor.IMAGES;
import static com.theoryinpractise.halbuilder.api.RepresentationFactory.HAL_JSON;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.twobit.yosane.api.DocumentOfImages;
import au.com.twobit.yosane.api.ImageStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.json.JsonRepresentationFactory;

public class FileStorage implements Storage {
    private String holdingArea;
    private String imageOutputFormat;
    private Logger log = LoggerFactory.getLogger(getClass());
    private final static String IMAGE_FILE_NAME     =   "image";
    private final static String THUMB_FILE_NAME     =   "thumb";
    private final static String STATUS_FILE_NAME    =   "status";
    private final static String DOCUMENT_BEAN       =   "document";
    
    private ObjectMapper mapper;
    
    @Inject
    public FileStorage(@Named("holdingArea") String holdingArea, @Named("imageOutputFormat") String imageOutputFormat) {
        this.holdingArea = holdingArea;
        this.imageOutputFormat = imageOutputFormat;
        mapper = new ObjectMapper();
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
        File imagedir = getPathFor(IMAGES,imageIdentifier);
        // determine the path of the file
        String imageFilePath = Joiner.on(File.separator).join(imagedir.getPath(), filename);
        // write the image data to disk
        try {
            if (image != null) {
                ImageIO.write(image, imageOutputFormat, new File(imageFilePath));
            }
        } catch (IOException e) {
            log.error("Failed to save image to storage: {}",e.getMessage());
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
        File imagedir = getPathFor(IMAGES,imageIdentifier);
        String filepath = Joiner.on(File.separator).join(imagedir.getPath(), filename);
        File file = new File(filepath);
        try {
            return ImageIO.read(file);
        } catch (Exception x) {
            log.error("Failed to load image from storage: {}",x.getMessage());
            x.printStackTrace();
        }
        throw new StorageException("Unable to read from file with identifier " + imageIdentifier);
    }

    @Override
    public void updateImageStatus(ImageStatus status, String imageIdentifier) throws StorageException {
        File imagedir = getPathFor(IMAGES,imageIdentifier);
        String filepath = Joiner.on(File.separator).join(imagedir.getPath(), STATUS_FILE_NAME);
        try {
            Files.write(status.name().getBytes(), new File(filepath));
        } catch (Exception x) {
            log.error("Failed to update status for image: {}",x.getMessage());
            throw new StorageException(x.getMessage());
        }
    }

    @Override
    public ImageStatus getImageStatus(String imageIdentifier) throws StorageException {
        String filepath = Joiner.on(File.separator).join(getPathFor(IMAGES,imageIdentifier), STATUS_FILE_NAME);
        try {
            File file = new File(filepath);
            if ( ! file.exists() ) {
                return ImageStatus.MISSING;
            }
            return ImageStatus.valueOf(Files.toString(file, Charset.defaultCharset()));
        } catch (Exception x) {
        }
        return ImageStatus.FAILED;
    }

    @Override
    public void assertImageStatus(String imageIdentifier, ImageStatus status) throws StorageException {
        ImageStatus currentStatus = getImageStatus(imageIdentifier) ; 
        if ( status != currentStatus ) {
            throw new StorageException(String.format("Asserted status %s but instead it was %s",status.name(),currentStatus.name()));
        }
        
    }


    @Override
    public void saveDocument(DocumentOfImages document, String documentIdentifier) throws StorageException {
        // create json from documentImages
        if ( document == null ) {
            throw new StorageException("No document provided for saving");
        } else if ( Strings.isNullOrEmpty(documentIdentifier) ) {
            throw new StorageException("Unable to save document - no identifier provided");
        }
        // convert the document to json
        try {
            mapper.writeValue(getFileOf(DOCUMENTS,documentIdentifier,"json"),document);
        } catch (Exception x) {
            String message = String.format("An error occurred saving document to disk: %s",x.getMessage());
            log.error(message);
            throw new StorageException(message);            
        }
    }

    @Override
    public DocumentOfImages loadDocument(String documentIdentifier) throws StorageException {
        if ( Strings.isNullOrEmpty(documentIdentifier) ) {
            throw new StorageException("Unable to save document - no identifier provided");
        }
        // load file into json string
        DocumentOfImages document = null;
        try {
            document = mapper.readValue(getFileOf(DOCUMENTS,documentIdentifier,"json"), DocumentOfImages.class);
        } catch (Exception x) {
            String message = String.format("An error occurred loading json document from disk %s", x.getMessage());
            log.error(message);
            throw new StorageException(message);
        }
        return document;
    }
    

    protected File getFileOf(PathFor pathFor, String identifier, String ... extras) throws StorageException {
        File f = getPathFor(pathFor,identifier);
        String [] params = Lists.asList(f.getPath(), extras != null ? extras : new String [] { }).toArray(new String[]{});
        String filepath = Joiner.on(File.separator).join(params);
        return new File(filepath);
    }
    protected File getPathFor(PathFor pathFor, String identifier) throws StorageException {
        // check storage is available first as that is probably cheaper option
        File location = new File(Joiner.on(File.separator).join(holdingArea,pathFor.path(), identifier));
        // create dir if not exists
        if (!(location.exists() || location.mkdirs())) {
            throw new StorageException("Path cannot be created: " + location.getPath());
        } else if (!(location.isDirectory() && location.canWrite())) {
            throw new StorageException("Path is not writable: " + location.getPath());
        }
        return location;
    }
    
    protected enum PathFor {
        IMAGES("images"),
        DOCUMENTS("documents");
        
        private String folderName;
        private PathFor(String folderName) {
            this.folderName = folderName;
        }
        
        public String path() {
            return folderName;
        }
    }

}
