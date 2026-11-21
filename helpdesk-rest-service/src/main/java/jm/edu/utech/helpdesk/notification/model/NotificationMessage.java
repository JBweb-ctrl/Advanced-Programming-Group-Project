package jm.edu.utech.helpdesk.notification.model;

import java.io.Serializable;

//one notification sent over the socket (Serializable so it can go through an ObjectOutputStream)
public class NotificationMessage implements Serializable {

    //all three copies must have the same number so Java accepts the object on the other side
    private static final long serialVersionUID = 1L;

    //targetUser "*" means send to everyone connected
    public static final String BROADCAST = "*";

    private NotificationType type;
    private String ticketId;
    private String targetUser;
    private String message;

    public NotificationMessage(NotificationType type, String ticketId, String targetUser, String message) {
        this.type = type;
        this.ticketId = ticketId;
        this.targetUser = targetUser;
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public String getMessage() {
        return message;
    }

    //true when it should go to every connected client
    public boolean isBroadcast() {
        return BROADCAST.equals(targetUser);
    }

    //used in the log lines
    @Override
    public String toString() {
        return type + " ticket=" + ticketId + " to=" + targetUser;
    }
}
