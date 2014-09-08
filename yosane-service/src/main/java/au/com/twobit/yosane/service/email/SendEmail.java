package au.com.twobit.yosane.service.email;

import java.io.File;

/**
 * Sends an email to the recipient with the files attached, if possible
 * 
 * @param recipient The email address of the intended recipient
 * @param files Zero to many (presumably) image files to be attached to the email 
 * @author paul
 *
 */
public interface SendEmail {
    public void sendImagesTo(String recipient, File ... files) throws Exception;
}
