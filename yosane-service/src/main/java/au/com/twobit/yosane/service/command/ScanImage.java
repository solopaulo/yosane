package au.com.twobit.yosane.service.command;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;

import au.com.twobit.yosane.api.ImageStatus;
import au.com.twobit.yosane.service.device.ScanHardware;
import au.com.twobit.yosane.service.storage.Storage;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ScanImage implements Runnable {

	final private ScanHardware hardware;
	final private Storage storage;
	final private String scannerName;
	final private String imageIdentifier;

	public ScanImage(ScanHardware hardware, Storage storage, String scannerName, String imageIdentifier) {
		this.hardware = hardware;
		this.storage = storage;
		this.scannerName = scannerName;
		this.imageIdentifier = imageIdentifier;
	}

	@Override
	public void run() {
		try {
			// write the status to file
			storage.updateStatus(ImageStatus.SCANNING, imageIdentifier);
			// acquire the image
			BufferedImage bi = hardware.acquireImage(scannerName, imageIdentifier);
			// update the status and write to file
			storage.updateStatus(ImageStatus.PROCESSING, imageIdentifier);
			// store the image
			storage.saveImage(bi, imageIdentifier);
			// create thumbnail
			BufferedImage thumbnail = new CreateThumbnail(storage, imageIdentifier).call();
			// save thumbnail
			storage.saveImageThumbnail(thumbnail, imageIdentifier);
			// update the status and write to file
			storage.updateStatus(ImageStatus.READY, imageIdentifier);
		} catch (Exception x) {
			x.printStackTrace();
			try {
				storage.updateStatus(ImageStatus.FAILED, imageIdentifier);
			} catch (Exception x1) {
			}
			// log errors and stuff
		}

	}

}
