package au.com.twobit.yosane.service.resource.dto;

public class EmailMessage {
    private String recipient;
    private String subject;
    private String imageIdentifier;

    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public String getImageIdentifier() {
        return imageIdentifier;
    }
    
    public void setImageIdentifier(String imageIdentifier) {
        this.imageIdentifier = imageIdentifier;
    }
    
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
}
