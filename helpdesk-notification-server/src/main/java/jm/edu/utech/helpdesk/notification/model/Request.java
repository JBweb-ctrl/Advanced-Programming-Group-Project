package jm.edu.utech.helpdesk.notification.model;

import java.io.Serializable;

//what gets sent TO the notification server
//REGISTER: a JavaFX client logs in and waits for notifications (username is filled in)
//PUBLISH: the REST service sends a notification out (message is filled in)

public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private String command;
    private String username;
    private NotificationMessage message;

    public Request(String command, String username, NotificationMessage message) {
        this.command = command;
        this.username = username;
        this.message = message;
    }

    public String getCommand() {
        return command;
    }

    public String getUsername() {
        return username;
    }

    public NotificationMessage getMessage() {
        return message;
    }
}
