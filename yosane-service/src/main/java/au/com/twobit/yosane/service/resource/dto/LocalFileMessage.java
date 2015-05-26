package au.com.twobit.yosane.service.resource.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class LocalFileMessage {
    private String localPath;
    
    @NotNull @NotEmpty
    private String [] imageIdentifiers;
    private String naming;

    public String [] getImageIdentifiers() {
        return imageIdentifiers;
    }

    public void setImageIdentifiers(String [] imageIdentifiers) {
        this.imageIdentifiers = imageIdentifiers;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getNaming() {
        return naming;
    }

    public void setNaming(String naming) {
        this.naming = naming;
    }
}
