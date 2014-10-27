package au.com.twobit.yosane.service.op.command;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.twobit.yosane.service.resource.dto.EmailMessage;
import au.com.twobit.yosane.service.send.SendFiles;
import au.com.twobit.yosane.service.send.provider.SendFilesEmail;
import au.com.twobit.yosane.service.storage.Storage;

import com.google.common.collect.Lists;

public class EmailPdf implements Runnable {

    private Storage storage;
    private String imageFormat;
    private EmailMessage emailMessage;
    private SendFiles sendEmail;
    private static final Logger log = LoggerFactory.getLogger(EmailPdf.class);

    @Inject
    public EmailPdf(Storage storage, @Named("imageOutputFormat") String imageFormat, @Named("sendEmail") SendFiles sendEmail) {
        this.storage = storage;
        this.imageFormat = imageFormat;
        this.sendEmail = sendEmail;
    }

    public EmailPdf send(EmailMessage emailMessage) {
        this.emailMessage = emailMessage;
        return this;
    }

    @Override
    public void run() {
        try {
            List<File>tempFiles = Lists.newArrayList();
            for (String imageIdentifier : emailMessage.getImageIdentifiers() ) {
                BufferedImage image = storage.loadImage(imageIdentifier);
                File tempFile = File.createTempFile(imageIdentifier, String.format(".%s",imageFormat));
                if (!tempFile.canWrite()) {
                    log.error("Unable to write the image to a temporary file for emailing: {}",tempFile.getName());
                    continue;
                }    
                ImageIO.write(image, imageFormat, tempFile);
                tempFiles.add( tempFile );
            }
            
            // create PDF document 
            CreatePDFDocument pdfCreator = new CreatePDFDocument(imageFormat, tempFiles.toArray(new File[] {}));
            File pdfFile = File.createTempFile(UUID.randomUUID().toString(), ".pdf");
            // add the image to the page
            FileOutputStream fos = new FileOutputStream(pdfFile);
            fos.write( pdfCreator.call() );
            fos.close();
            
            Map<String,String> settings = new LinkedHashMap<String,String>();
            settings.put(SendFilesEmail.RECIPIENT, emailMessage.getRecipient()); 
            // send files by email to recipient
            sendEmail.sendFilesTo(settings, new File [] { pdfFile } );
            for ( File tempFile : tempFiles ) {
                tempFile.delete();
            }
            pdfFile.delete();
        } catch (Exception x) {
            x.printStackTrace();
            log.error("Failed to send email with image attached: {}",x.getMessage());
        }
    }

}
