package au.com.twobit.yosane.service.send.provider;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import javax.validation.constraints.NotNull;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;

import au.com.twobit.yosane.service.dw.EmailConfiguration;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class SendFilesEmailACE  implements SendFilesEmail  {

    private EmailConfiguration emailConfiguration;

    @Inject
    public SendFilesEmailACE(@NotNull EmailConfiguration emailSettings) {
        this.emailConfiguration = emailSettings;
    }

    @Override
    public void sendFilesTo(Map<String, String> settings, File... files) throws Exception {
        if (emailConfiguration == null) {
            throw new Exception("Email is not configured");
        } else if (files == null) {
            throw new Exception("There are no files to be attached");
        }
        
        // determine a recipient
        String recipient = Optional.fromNullable( settings.get(RECIPIENT) ).or( emailConfiguration.getDefaultRecipient() );
        if ( Strings.isNullOrEmpty( recipient ) ) {
            throw new Exception("No recipient specified and no suitable default recipient is found");
        }

        MultiPartEmail email = null;
        try {
            email = transformEmailSettingsToMultiPartEmail(emailConfiguration);
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

    protected MultiPartEmail transformEmailSettingsToMultiPartEmail(EmailConfiguration emailConfiguration) throws Exception {
        // create a new multi part email
        MultiPartEmail email = new MultiPartEmail();
        // set the hostname
        email.setHostName(emailConfiguration.getSmtpHost().toLowerCase());
        // if the username is set in the mail settings, then perhaps
        // authentication is required
        if (!Strings.isNullOrEmpty(emailConfiguration.getUsername())) {
            email.setAuthentication(emailConfiguration.getUsername(), Optional.fromNullable(emailConfiguration.getPassword()).or(""));
        }
        // if SSL is enabled, then set it so
        if (emailConfiguration.isSslEnabled()) {
            email.setSSLOnConnect(true);
            if (emailConfiguration.getSmtpPort() >= 0) {
                // only override ssl port if it was specified
                email.setSslSmtpPort(String.valueOf(emailConfiguration.getSmtpPort()));
            }
        } else if (emailConfiguration.getSmtpPort() >= 0) {
            // only override smtp port if it was specified
            email.setSmtpPort(emailConfiguration.getSmtpPort());
        }
        email.setStartTLSEnabled( Boolean.valueOf( emailConfiguration.isStartTls()));
        email.setStartTLSRequired( email.isStartTLSEnabled());
        email.setSubject(Optional.fromNullable(emailConfiguration.getDefaultSubject()).or(DEFAULT_SUBJECT));
        email.setFrom(emailConfiguration.getDefaultSender(),emailConfiguration.getDefaultSenderName());
        return email;
    }

    @Override
    public String getDestinationDescription() {
        return "Send via Email";
    }
}
