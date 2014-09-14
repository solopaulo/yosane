package au.com.twobit.yosane.service.dw;


import io.dropwizard.Configuration;

public class YosaneServiceConfiguration extends Configuration {
    private EmailConfiguration emailConfiguration;
    
    public YosaneServiceConfiguration() {
    }
    
    public void setEmailConfiguration(EmailConfiguration emailConfiguration) {
        this.emailConfiguration = emailConfiguration;
    }
    
    public EmailConfiguration getEmailConfiguration() {
        return emailConfiguration;
    }

}
