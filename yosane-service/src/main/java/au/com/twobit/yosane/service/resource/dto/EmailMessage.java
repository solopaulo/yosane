package au.com.twobit.yosane.service.resource.dto;


public class EmailMessage {
    private String recipient;
    private String subject;
    private String [] imageIdentifiers;

    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public String [] getImageIdentifiers() {
        return imageIdentifiers;
    }
    
    public void setImageIdentifiers(String [] imageIdentifiers) {
        this.imageIdentifiers = imageIdentifiers;
    }
    
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
}
