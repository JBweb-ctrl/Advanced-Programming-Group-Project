package jm.edu.utech.helpdesk.notification.model;

import java.io.Serializable;

//what the notification server sends BACK after a request

public class Response implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
