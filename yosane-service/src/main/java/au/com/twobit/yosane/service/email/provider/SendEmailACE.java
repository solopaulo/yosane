package au.com.twobit.yosane.service.email.provider;

import java.io.File;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;

import au.com.twobit.yosane.service.email.EmailSettings;
import au.com.twobit.yosane.service.email.SendEmail;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

public class SendEmailACE implements SendEmail {

    private EmailSettings emailSettings;
    public static final String DEFAULT_SUBJECT = "Here is your image from Yosane, the email";

    @Inject
    public SendEmailACE(@NotNull EmailSettings emailSettings) {
        this.emailSettings = emailSettings;
    }

    @Override
    public void sendImagesTo(String recipient, File... files) throws Exception {
        if (emailSettings == null) {
            throw new Exception("Email is not configured");
        } else if (files == null) {
            throw new Exception("There are no files to be attached");
        }

        MultiPartEmail email = null;
        try {
            email = transformEmailSettingsToMultiPartEmail(emailSettings);
            for (File input : files) {
                EmailAttachment attachment = transformFileToEmailAttachment(input);
                if (attachment == null) {
                    // log an error
                    continue;
                }
                email.attach(attachment);
            }
        } catch (Exception x) {
            throw new Exception("Failed configuring the email: " + x.getMessage());
        }

        email.send();
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

    protected MultiPartEmail transformEmailSettingsToMultiPartEmail(EmailSettings emailSettings) {
        // create a new multi part email
        MultiPartEmail email = new MultiPartEmail();
        // set the hostname
        email.setHostName(emailSettings.getMailHost().toLowerCase());
        // if the username is set in the mail settings, then perhaps
        // authentication is required
        if (!Strings.isNullOrEmpty(emailSettings.getMailUser())) {
            email.setAuthentication(emailSettings.getMailUser(), Optional.fromNullable(emailSettings.getMailPassword()).or(""));
        }
        // if SSL is enabled, then set it so
        if (emailSettings.isSslEnabled()) {
            email.setSSLOnConnect(true);
            if (emailSettings.getMailPort() >= 0) {
                // only override ssl port if it was specified
                email.setSslSmtpPort(String.valueOf(emailSettings.getMailPort()));
            }
        } else if (emailSettings.getMailPort() >= 0) {
            // only override smtp port if it was specified
            email.setSmtpPort(emailSettings.getMailPort());
        }
        email.setSubject(Optional.fromNullable(emailSettings.getMailSubject()).or(DEFAULT_SUBJECT));
        return email;
    }
}
