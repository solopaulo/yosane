package au.com.twobit.yosane.service.resource.dto;

public class LocalFileMessage {
    private String localPath;
    
    private String [] imageIdentifiers;

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
}
