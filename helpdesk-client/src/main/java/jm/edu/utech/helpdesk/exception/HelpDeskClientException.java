package jm.edu.utech.helpdesk.exception;

//parent of all the client exceptions
public class HelpDeskClientException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public HelpDeskClientException(String message) {
        super(message);
    }

    public HelpDeskClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
