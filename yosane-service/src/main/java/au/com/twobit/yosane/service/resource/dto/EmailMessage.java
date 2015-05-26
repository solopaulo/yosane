package au.com.twobit.yosane.service.resource.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;


public class EmailMessage {
    private String recipient;
    private String subject;
    @NotNull @NotEmpty
    private String [] imageIdentifiers;
    private String naming;

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

    public String getNaming() {
        return naming;
    }

    public void setNaming(String naming) {
        this.naming = naming;
    }
}
