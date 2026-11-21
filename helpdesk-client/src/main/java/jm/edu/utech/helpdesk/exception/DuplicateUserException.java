package jm.edu.utech.helpdesk.exception;

//username already taken
public class DuplicateUserException extends HelpDeskClientException {

    private static final long serialVersionUID = 1L;

    public DuplicateUserException(String message) {
        super(message);
    }

    public DuplicateUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
