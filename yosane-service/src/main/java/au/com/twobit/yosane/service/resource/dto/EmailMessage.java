package au.com.twobit.yosane.service.resource.dto;

public class EmailMessage {
    private String recipient;
    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    private String imageIdentifier;
    public String getImageIdentifier() {
        return imageIdentifier;
    }
    public void setImageIdentifier(String imageIdentifier) {
        this.imageIdentifier = imageIdentifier;
    }
}
