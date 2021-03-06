package au.com.twobit.yosane.service.op.command;

import java.awt.image.BufferedImage;
import java.util.List;

import au.com.twobit.yosane.api.DeviceOption;
import au.com.twobit.yosane.api.ImageStatus;
import au.com.twobit.yosane.service.device.ScanHardware;
import au.com.twobit.yosane.service.storage.Storage;

public class ScanImage implements Runnable {

    final private ScanHardware hardware;
    final private Storage storage;
    final private String scannerName;
    final private String imageIdentifier;
    final private List<DeviceOption> options;
    
    public ScanImage(ScanHardware hardware, Storage storage, String scannerName, String imageIdentifier,List<DeviceOption> options) {
        this.hardware = hardware;
        this.storage = storage;
        this.scannerName = scannerName;
        this.imageIdentifier = imageIdentifier;
        this.options = options;
    }

    @Override
    public void run() {
        try {
            // write the status to file
            storage.updateImageStatus(ImageStatus.SCANNING, imageIdentifier);
            // acquire the image
            DeviceOption [] devopts = new DeviceOption[]{};
            BufferedImage bi = hardware.acquireImage(scannerName, imageIdentifier, options == null ? devopts : options.toArray(devopts));
            // update the status and write to file
            storage.updateImageStatus(ImageStatus.PROCESSING, imageIdentifier);
            // store the image
            storage.saveImage(bi, imageIdentifier);
            // create thumbnail
            if ( bi != null ) {
                BufferedImage thumbnail = new CreateThumbnail(bi).call();
                // save thumbnail
                storage.saveImageThumbnail(thumbnail, imageIdentifier);
            }
            // update the status and write to file
            storage.updateImageStatus(ImageStatus.READY, imageIdentifier);
        } catch (Exception x) {
            x.printStackTrace();
            try {
                storage.updateImageStatus(ImageStatus.FAILED, imageIdentifier);
            } catch (Exception x1) {
                x.printStackTrace();
            }
            // log errors and stuff
        }

    }

}
