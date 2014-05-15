package au.com.twobit.yosane.service.storage;

import java.awt.image.BufferedImage;

/** An interface to load and save images
 * 
 * @author paul
 *
 */
public interface Storage {
	
	/** Saves an image to storage and returns a reference by which to load it
	 * 
	 * @param image BufferedImage data to store
	 * @return Returns the identifier to be used to locate the image
	 * @throws StorageException If an error occurs storing the image
	 */
	public String saveImage(BufferedImage image) throws StorageException;
	
	/** Saves an image thumbnail to storage and returns using the reference provided
	 * 
	 * @param image BufferedImage data to store
	 * @return Returns the identifier to be used to locate the image
	 * @throws StorageException If an error occurs storing the image
	 */
	public void saveImageThumbnail(BufferedImage image, String imageIdentifier) throws StorageException;
	
	/** Loads an image from storage using the reference provided
	 * 
	 * @param imageIdentifier The unique identifier for the image
	 * @return Returns the buffered image data
	 * @throws StorageException If an error occurs loading the image
	 */
	public BufferedImage loadImage(String imageIdentifier) throws StorageException;
	
	/** Loads an image thumbnail using the reference provided
	 * @param imageIdentifier The unique identifier for the image
	 * @return Returns the buffered image data
	 * @throws StorageException If an error occurs loading the image
	 */
	public BufferedImage loadImageThumbnail(String imageIdentifier) throws StorageException;
}
