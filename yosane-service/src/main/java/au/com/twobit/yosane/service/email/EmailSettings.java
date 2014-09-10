package au.com.twobit.yosane.service.email;

public class EmailSettings {
    private boolean sslEnabled = false;
    private String mailHost;
    private int mailPort = -1;
    private String mailUser;
    private String mailPassword;
    private String mailSubject;
    private boolean startTls = false;
    
    public String getMailSubject() {
        return mailSubject;
    }
    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }
    public boolean isSslEnabled() {
        return sslEnabled;
    }
    public void setSslEnabled(boolean useSSL) {
        this.sslEnabled = useSSL;
    }
    public String getMailHost() {
        return mailHost;
    }
    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }
    public int getMailPort() {
        return mailPort;
    }
    public void setMailPort(int mailPort) {
        this.mailPort = mailPort;
    }
    public String getMailUser() {
        return mailUser;
    }
    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }
    public String getMailPassword() {
        return mailPassword;
    }
    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }
    public boolean isStartTls() {
        return startTls;
    }
    public void setStartTls(boolean startTls) {
        this.startTls = startTls;
    }
}
