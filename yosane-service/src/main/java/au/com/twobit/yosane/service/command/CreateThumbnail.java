package au.com.twobit.yosane.service.command;

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import au.com.twobit.yosane.service.storage.Storage;

public class CreateThumbnail implements Callable<BufferedImage> {
	private final String imageIdentifier;
	private final Storage storage;
	
	public CreateThumbnail(Storage storage, String imageIdentifier) {
		this.imageIdentifier = imageIdentifier;
		this.storage = storage;
	}
	@Override
	public BufferedImage call() throws Exception {
		return null;
	}

}
