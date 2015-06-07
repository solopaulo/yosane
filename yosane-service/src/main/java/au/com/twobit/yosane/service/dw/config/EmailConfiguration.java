package au.com.twobit.yosane.service.dw.config;

import java.util.List;

import com.google.common.collect.Lists;

public class EmailConfiguration {
    private boolean sslEnabled = false;
    private String smtpHost;
    private int smtpPort = -1;
    private String username;
    private String password;
    private String defaultSubject = "Here is your scanned image from Yosane";
    private String defaultSender = "me@privacy.net";
    private String defaultSenderName = "Yosane Scanner Emails";
    private String defaultRecipient;
    private boolean startTls = false;
    private List<String> recipients = Lists.newArrayList();
    
    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getDefaultSubject() {
        return defaultSubject;
    }
    
    public void setDefaultSubject(String defaultSubject) {
        this.defaultSubject = defaultSubject;
    }
    
    public boolean isSslEnabled() {
        return sslEnabled;
    }
    public void setSslEnabled(boolean useSSL) {
        this.sslEnabled = useSSL;
    }
    public String getSmtpHost() {
        return smtpHost;
    }
    public void setSmtpHost(String mailHost) {
        this.smtpHost = mailHost;
    }
    public int getSmtpPort() {
        return smtpPort;
    }
    public void setSmtpPort(int mailPort) {
        this.smtpPort = mailPort;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String mailUser) {
        this.username = mailUser;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isStartTls() {
        return startTls;
    }
    public void setStartTls(boolean startTls) {
        this.startTls = startTls;
    }
    public String getDefaultRecipient() {
        return defaultRecipient;
    }
    public void setDefaultRecipient(String defaultRecipient) {
        this.defaultRecipient = defaultRecipient;
    }
    public String getDefaultSender() {
        return defaultSender;
    }
    public void setDefaultSender(String defaultSender) {
        this.defaultSender = defaultSender;
    }
    public String getDefaultSenderName() {
        return defaultSenderName;
    }
    public void setDefaultSenderName(String defaultSenderName) {
        this.defaultSenderName = defaultSenderName;
    }
}
