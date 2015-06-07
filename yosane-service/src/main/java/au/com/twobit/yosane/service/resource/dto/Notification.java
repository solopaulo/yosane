package au.com.twobit.yosane.service.resource.dto;

import java.util.Date;

import au.com.twobit.yosane.api.NotificationType;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Notification {
    String message;
    Date timestamp;
    NotificationType notificationType;
    
    public Notification(String message, Date timestamp, NotificationType notificationType) {
        this.message = message;
        this.timestamp = timestamp;
        this.notificationType = notificationType;
    }

    public static Notification create(String message, NotificationType notificationType, Date date) {
        return new Notification(message,date,notificationType);
    }
    
    public static Notification create(String message, NotificationType notificationType) {
        return create(message,notificationType,new Date());
    }
    
    public static Notification create (String message) {
        return create(message,NotificationType.INFO);
    }
    
    @JsonProperty
    public String getMessage() {
        return message;
    }

    @JsonProperty
    public Date getTimestamp() {
        return timestamp;
    }

    @JsonProperty
    public NotificationType getNotificationType() {
        return notificationType;
    }
    
    @Override
    public String toString() {
        return null;
    }
}
