package au.com.twobit.yosane.service.op.command;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.twobit.yosane.service.resource.dto.LocalFileMessage;
import au.com.twobit.yosane.service.send.SendFiles;
import au.com.twobit.yosane.service.send.provider.SendFilesLocalDir;
import au.com.twobit.yosane.service.storage.Storage;

import com.google.common.collect.Lists;

public class LocalFileImage implements Runnable {
    private Storage storage;
    private String imageFormat;
    private LocalFileMessage localFileMessage;
    private SendFiles sendFiles;
    private static final Logger log = LoggerFactory.getLogger(LocalFileImage.class);

    @Inject
    public LocalFileImage(Storage storage, @Named("imageOutputFormat") String imageFormat, @Named("sendLocalFile") SendFiles sendFiles) {
        this.storage = storage;
        this.imageFormat = imageFormat;
        this.sendFiles = sendFiles;
    }
    

    public LocalFileImage send(LocalFileMessage localFileMessage) {
        this.localFileMessage = localFileMessage;
        return this;
    }
    
    @Override
    public void run() {
        try {
            List<File>tempFiles = Lists.newArrayList();
            for (String imageIdentifier : localFileMessage.getImageIdentifiers() ) {
                BufferedImage image = storage.loadImage(imageIdentifier);
                File tempFile = File.createTempFile(imageIdentifier, String.format(".%s",imageFormat));
                if (!tempFile.canWrite()) {
                    log.error("Unable to write the image to a temporary file for sending local file: {}",tempFile.getName());
                    continue;
                }    
                ImageIO.write(image, imageFormat, tempFile);
                tempFiles.add( tempFile );
            }
            
            Map<String,String> settings = new LinkedHashMap<String,String>();
            settings.put(SendFilesLocalDir.LOCAL_DIR, localFileMessage.getLocalPath()); 
            // send files by email to recipient
            sendFiles.sendFilesTo(settings, tempFiles.toArray(new File[]{}));
            for ( File tempFile : tempFiles ) {
                tempFile.delete();
            }
        } catch (Exception x) {
            x.printStackTrace();
            log.error("Failed to send email with image attached: {}",x.getMessage());
        }
    }

}
