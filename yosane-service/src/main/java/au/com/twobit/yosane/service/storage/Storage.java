package au.com.twobit.yosane.service.storage;

import java.awt.image.BufferedImage;
import java.util.Date;

import au.com.twobit.yosane.api.DocumentOfImages;
import au.com.twobit.yosane.api.ImageStatus;

/**
 * An interface to load and save images
 * 
 * @author paul
 * 
 */
public interface Storage {

    /**
     * Saves an image to storage and returns a reference by which to load it
     * 
     * @param image
     *            BufferedImage data to store
     * @param imageIdentifier
     *            The unique identifier for the image
     * @return Returns the identifier to be used to locate the image
     * @throws StorageException
     *             If an error occurs storing the image
     */
    public void saveImage(BufferedImage image, String imageIdentifier) throws StorageException;

    /**
     * Saves an image thumbnail to storage and returns using the reference
     * provided
     * 
     * @param image
     *            BufferedImage data to store
     * @param imageIdentifier
     *            The unique identifier for the image
     * @return Returns the identifier to be used to locate the image
     * @throws StorageException
     *             If an error occurs storing the image
     */
    public void saveImageThumbnail(BufferedImage image, String imageIdentifier) throws StorageException;

    /**
     * Loads an image from storage using the reference provided
     * 
     * @param imageIdentifier
     *            The unique identifier for the image
     * @return Returns the buffered image data
     * @throws StorageException
     *             If an error occurs loading the image
     */
    public BufferedImage loadImage(String imageIdentifier) throws StorageException;

    /**
     * Loads an image thumbnail using the reference provided
     * 
     * @param imageIdentifier
     *            The unique identifier for the image
     * @return Returns the buffered image data
     * @throws StorageException
     *             If an error occurs loading the image
     */
    public BufferedImage loadImageThumbnail(String imageIdentifier) throws StorageException;

    /**
     * Updates the status of the image identifier provided
     * 
     * @param status
     *            the new status of the images
     * @param imageIdentifier
     *            the unique identifier for the image
     * @throws StorageException
     *             If some error occurs when updating the status
     */
    public void updateImageStatus(ImageStatus status, String imageIdentifier) throws StorageException;

    /**
     * Gets the status of the image identifier provided
     * 
     * @param imageIdentifier
     *            the unique identifier for the image
     * @return - returns the ImageStatus for the image as discovered
     */
    public ImageStatus getImageStatus(String imageIdentifier) throws StorageException;

    /**
     * Gets the last known modified date of the image
     * 
     * @param imageIdentifier
     * @return - returns the Date of last modification of the image
     */
    public Date getImageLastModifiedDate(String imageIdentifier) throws StorageException;
    
    /** Verifies the status is as requested
     * 
     * @param imageIdentifier the unique identifier for the image
     * @param status the status we want to assert is correct
     * @throws StorageException - must throw a storage exception if the assertion fails
     */
    public void assertImageStatus(String imageIdentifier, ImageStatus status) throws StorageException;
    
    
    /** Saves the document of images
     * 
     * @param document A document of images referencing some images that are still available
     * @param documentIdentifier An identifier used to uniquely identify the document
     * @throws StorageException - describes an error that caused the operation to fail
     */
    public void saveDocument(DocumentOfImages document, String documentIdentifier) throws StorageException;
    
    
    /** Gets a document of images
     * 
     * @param documentIdentifier An identifier used to uniquely identify the document
     * @return - Returns the DocumentOfImages referenced by the unique identifier
     * @throws StorageException - describes an error that caused the operation to fail
     */
    public DocumentOfImages loadDocument(String documentIdentifier) throws StorageException;
   
}
