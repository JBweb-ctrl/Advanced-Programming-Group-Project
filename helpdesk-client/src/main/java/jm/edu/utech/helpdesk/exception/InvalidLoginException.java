package jm.edu.utech.helpdesk.exception;

//wrong username or password
public class InvalidLoginException extends HelpDeskClientException {

    private static final long serialVersionUID = 1L;

    public InvalidLoginException(String message) {
        super(message);
    }

    public InvalidLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
