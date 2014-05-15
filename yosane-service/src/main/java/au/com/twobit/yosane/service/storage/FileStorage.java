package au.com.twobit.yosane.service.storage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.google.common.base.Joiner;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class FileStorage implements Storage {
	private String holdingArea;
	private String imageOutputFormat;
	private final static String IMAGE_FILE_NAME = "image";
	
	@Inject
	public FileStorage(@Named("holdingArea") String holdingArea, 
					   @Named("imageOutputFormat") String imageOutputFormat) {
		this.holdingArea = holdingArea;
		this.imageOutputFormat = imageOutputFormat;
	}
	
	
	@Override
	public String saveImage(BufferedImage image) throws StorageException {
		// create image identifier
		String identifier = Hashing.md5().hashString(UUID.randomUUID().toString(),Charset.defaultCharset()).toString();
		// check storage is available first as that is probably cheaper option
		File imagedir = new File( Joiner.on(File.separator).join(holdingArea,identifier));
		if ( (imagedir.exists() || ! imagedir.mkdir() ) || !( imagedir.isDirectory() && imagedir.canWrite() )) {
			throw new StorageException(String.format("Unable to write to new image area: %s",imagedir.getPath()));
		}
		// determine the path of the file
		String imageFilePath = Joiner.on(File.separator).join(imagedir.getPath(),IMAGE_FILE_NAME);
		// write the image data to disk
		try {
			ImageIO.write(image, imageOutputFormat, new File(imageFilePath));
		} catch (IOException e) {
			e.printStackTrace();
			throw new StorageException("Failed to write file to disk: "+e.getMessage());
		}
		return identifier;
	}

	@Override
	public BufferedImage loadImage(String imageIdentifier) throws StorageException {
		String filepath = Joiner.on(File.separator).join(holdingArea,imageIdentifier,IMAGE_FILE_NAME);
		File file = new File(filepath);
		try {
			return ImageIO.read(file);	
		} catch (Exception x) {
			x.printStackTrace();
		}
		throw new StorageException("Unable to read from file with identifier "+imageIdentifier);
	}


	@Override
	public void saveImageThumbnail(BufferedImage image, String imageIdentifier)
			throws StorageException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public BufferedImage loadImageThumbnail(String imageIdentifier)
			throws StorageException {
		// TODO Auto-generated method stub
		return null;
	}

}
