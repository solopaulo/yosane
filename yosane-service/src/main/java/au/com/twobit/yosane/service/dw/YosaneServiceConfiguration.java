package au.com.twobit.yosane.service.dw;


import au.com.twobit.yosane.service.dw.config.EmailConfiguration;
import au.com.twobit.yosane.service.dw.config.LocalDirectoryConfiguration;
import io.dropwizard.Configuration;

public class YosaneServiceConfiguration extends Configuration {
    private EmailConfiguration emailConfiguration;
    private LocalDirectoryConfiguration localDirectoryConfiguration;
    private FileStorageConfiguration fileStorageConfiguration = new FileStorageConfiguration();
    
    public boolean isMockScannerModule() {
        return mockScannerModule;
    }

    public void setMockScannerModule(boolean mockScannerModule) {
        this.mockScannerModule = mockScannerModule;
    }

    private boolean mockScannerModule = false;
    
    public YosaneServiceConfiguration() {
    }
    
    public void setEmailConfiguration(EmailConfiguration emailConfiguration) {
        this.emailConfiguration = emailConfiguration;
    }
    
    public EmailConfiguration getEmailConfiguration() {
        return emailConfiguration;
    }

    public LocalDirectoryConfiguration getLocalDirectoryConfiguration() {
        return localDirectoryConfiguration;
    }

    public void setLocalDirectoryConfiguration(LocalDirectoryConfiguration localDirectoryConfiguration) {
        this.localDirectoryConfiguration = localDirectoryConfiguration;
    }

    public FileStorageConfiguration getFileStorageConfiguration() {
        return fileStorageConfiguration;
    }

    public void setFileStorageConfiguration(FileStorageConfiguration fileStorageConfiguration) {
        this.fileStorageConfiguration = fileStorageConfiguration;
    }

}
