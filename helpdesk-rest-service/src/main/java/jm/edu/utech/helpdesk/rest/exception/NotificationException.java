package jm.edu.utech.helpdesk.rest.exception;

//notification server cant be reached or said no
public class NotificationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
