package au.com.twobit.yosane.service.storage;

import static au.com.twobit.yosane.service.storage.FileStorage.PathFor.DOCUMENTS;
import static au.com.twobit.yosane.service.storage.FileStorage.PathFor.IMAGES;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

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

public class FileStorage implements Storage {
    private String holdingArea;
    private String imageOutputFormat;
    private Logger log = LoggerFactory.getLogger(getClass());
    private final static String IMAGE_FILE_NAME     =   "image";
    private final static String THUMB_FILE_NAME     =   "thumb";
    private final static String STATUS_FILE_NAME    =   "status";
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
        File imageFile = getFileOf(IMAGES,imageIdentifier,filename);
        try {
            if (image != null) {
                ImageIO.write(image, imageOutputFormat, imageFile);
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

    
    /** Loads an image from disk using ImageIO method
     * 
     * @param imageIdentifier The unique image identifier used to source the file
     * @param filename The name of the file (e.g. the main image or the thumbnail)
     * @return - Returns the BufferedImage data loaded from the file
     * @throws StorageException if an error occcurs attempting to load the file
     */
    protected BufferedImage load(String imageIdentifier, String filename) throws StorageException {
        File file = getFileOf(IMAGES,imageIdentifier,filename);
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
        File imageStatusFile = getFileOf(IMAGES,imageIdentifier,STATUS_FILE_NAME);
        try {
            Files.write(status.name().getBytes(), imageStatusFile);
        } catch (Exception x) {
            log.error("Failed to update status for image: {}",x.getMessage());
            throw new StorageException(x.getMessage());
        }
    }

    @Override
    public ImageStatus getImageStatus(String imageIdentifier) throws StorageException {
        File file = getFileOf(IMAGES,imageIdentifier,STATUS_FILE_NAME);
        try {
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
    
    /* Gets a file handle to the file identified for the type of path provided, and additional parameters provided
     * 
     * @param pathFor A PathFor enum indicating whether the path is for images or documents
     * @param identifier The required unique identifier used to identify an individual document
     * @param extras Var args String parameters used to further build the file path
     * @return - 
     */
    protected File getFileOf(PathFor pathFor, String identifier, String ... extras) throws StorageException {
        File f = getPathFor(pathFor,identifier,false);
        String [] params = Lists.asList(f.getPath(), extras != null ? extras : new String [] { }).toArray(new String[]{});
        String filepath = Joiner.on(File.separator).join(params);
        return new File(filepath);
    }
    
    
    /* Gets a file handle to the directory indicated by the PathFor enum
     * 
     * @param pathFor A PathFor enum indicating whether the path is for images or documents
     * @param identifier A unique identifier for an individual image or document
     * @return - Returns a file handle to the directory. If the directory did not exist, an attempt
     *           will be made to create it
     */
    protected File getPathFor(PathFor pathFor, String identifier,boolean noCreateDir) throws StorageException {
        // check storage is available first as that is probably cheaper option
        File location = new File(Joiner.on(File.separator).join(holdingArea,pathFor.path(), identifier));
        // maybe create dir if not exists
        if ( noCreateDir && ! location.exists() ) {
            throw new StorageException("Path will not be created: " + location.getPath());
        } else if ( ! (location.exists() || location.mkdirs())) {
            throw new StorageException("Path cannot be created: " + location.getPath());
        } else if (!(location.isDirectory() && location.canWrite())) {
            throw new StorageException("Path is not writable: " + location.getPath());
        }
        return location;
    }
    
    
    /** Enumerates the different things this storage supports, e.g. Images, Documents and where they will be stored
     * 
     * @author paul
     *
     */
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


    @Override
    public Date getImageLastModifiedDate(String imageIdentifier) throws StorageException {
        File file = getFileOf(IMAGES,imageIdentifier,IMAGE_FILE_NAME);
        return new Date(file == null ? 0L :  file.lastModified());
    }

}
