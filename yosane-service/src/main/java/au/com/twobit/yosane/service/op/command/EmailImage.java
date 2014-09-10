package au.com.twobit.yosane.service.op.command;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.twobit.yosane.service.email.SendEmail;
import au.com.twobit.yosane.service.resource.dto.EmailMessage;
import au.com.twobit.yosane.service.storage.Storage;

public class EmailImage implements Runnable {

    private Storage storage;
    private String imageFormat;
    private EmailMessage emailMessage;
    private SendEmail sendEmail;
    private static final Logger log = LoggerFactory.getLogger(EmailImage.class);

    @Inject
    public EmailImage(Storage storage, @Named("imageOutputFormat") String imageFormat, SendEmail sendEmail) {
        this.storage = storage;
        this.imageFormat = imageFormat;
        this.sendEmail = sendEmail;
    }

    public EmailImage send(EmailMessage emailMessage) {
        this.emailMessage = emailMessage;
        return this;
    }

    @Override
    public void run() {
        try {
            BufferedImage image = storage.loadImage(emailMessage.getImageIdentifier());
            File tempFile = File.createTempFile(emailMessage.getImageIdentifier(), String.format(".%s",imageFormat));
            if (!tempFile.canWrite()) {
                log.error("Unable to write the image to a temporary file for emailing");
                return;
            }
            ImageIO.write(image, imageFormat, tempFile);
            sendEmail.sendImagesTo(emailMessage.getRecipient(), tempFile);
            tempFile.delete();
        } catch (Exception x) {
            x.printStackTrace();
            log.error("Failed to send email with image attached: {}",x.getMessage());
        }
    }

}
