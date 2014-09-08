package au.com.twobit.yosane.service.dw;


import io.dropwizard.Configuration;
import au.com.twobit.yosane.service.email.EmailSettings;

public class YosaneServiceConfiguration extends Configuration {
    private EmailSettings emailSettings;
    
    public YosaneServiceConfiguration() {
        // TODO Auto-generated constructor stub
    }
    
    public void setEmailSettings(EmailSettings emailSettings) {
        this.emailSettings = emailSettings;
    }
    
    public EmailSettings getEmailSettings() {
        return emailSettings;
    }

}
