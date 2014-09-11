package au.com.twobit.yosane.service.email.provider;

import java.io.File;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import javax.validation.constraints.NotNull;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;

import au.com.twobit.yosane.service.email.EmailSettings;
import au.com.twobit.yosane.service.email.SendEmail;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class SendEmailACE implements SendEmail {

    private EmailSettings emailSettings;
    public static final String DEFAULT_SUBJECT = "Here is your image from Yosane, the email";

    @Inject
    public SendEmailACE(@NotNull EmailSettings emailSettings) {
        this.emailSettings = emailSettings;
    }

    @Override
    public void sendImagesTo(String sendTo, File... files) throws Exception {
        if (emailSettings == null) {
            throw new Exception("Email is not configured");
        } else if (files == null) {
            throw new Exception("There are no files to be attached");
        }
        
        // determine a recipient
        String recipient = Optional.fromNullable( sendTo ).or( emailSettings.getDefaultRecipient() );
        if ( Strings.isNullOrEmpty( recipient ) ) {
            throw new Exception("No recipient specified and no suitable default recipient is found");
        }

        MultiPartEmail email = null;
        try {
            email = transformEmailSettingsToMultiPartEmail(emailSettings);
            email.setTo( Lists.newArrayList( InternetAddress.parse(recipient)));
            applyFilesToEmailAsAttachments(email, files);
        } catch (Exception x) {
            throw new Exception("Failed configuring the email: " + x.getMessage());
        }

        email.send();
    }

    protected void applyFilesToEmailAsAttachments(MultiPartEmail email, File ...files) throws Exception {
        for (File input : files) {
            EmailAttachment attachment = transformFileToEmailAttachment(input);
            if (attachment == null) {
                // log an error
                continue;
            }
            email.attach(attachment);
        }
    }
    
    protected EmailAttachment transformFileToEmailAttachment(File input) {
        if (input == null || !input.exists()) {
            return null;
        }
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(input.getPath());
        attachment.setDescription("A file scanned by Yosane");
        attachment.setName(input.getName());
        attachment.setDisposition(input.getName());
        return attachment;
    }

    protected MultiPartEmail transformEmailSettingsToMultiPartEmail(EmailSettings emailSettings) throws Exception {
        // create a new multi part email
        MultiPartEmail email = new MultiPartEmail();
        // set the hostname
        email.setHostName(emailSettings.getSmtpHost().toLowerCase());
        // if the username is set in the mail settings, then perhaps
        // authentication is required
        if (!Strings.isNullOrEmpty(emailSettings.getUsername())) {
            email.setAuthentication(emailSettings.getUsername(), Optional.fromNullable(emailSettings.getPassword()).or(""));
        }
        // if SSL is enabled, then set it so
        if (emailSettings.isSslEnabled()) {
            email.setSSLOnConnect(true);
            if (emailSettings.getSmtpPort() >= 0) {
                // only override ssl port if it was specified
                email.setSslSmtpPort(String.valueOf(emailSettings.getSmtpPort()));
            }
        } else if (emailSettings.getSmtpPort() >= 0) {
            // only override smtp port if it was specified
            email.setSmtpPort(emailSettings.getSmtpPort());
        }
        email.setStartTLSEnabled( Boolean.valueOf( emailSettings.isStartTls()));
        email.setStartTLSRequired( email.isStartTLSEnabled());
        email.setSubject(Optional.fromNullable(emailSettings.getDefaultSubject()).or(DEFAULT_SUBJECT));
        email.setFrom(emailSettings.getDefaultSender(),emailSettings.getDefaultSenderName());
        return email;
    }
}
