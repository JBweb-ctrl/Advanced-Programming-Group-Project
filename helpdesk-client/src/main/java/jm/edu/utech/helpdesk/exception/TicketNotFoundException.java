package jm.edu.utech.helpdesk.exception;

//ticket does not exist
public class TicketNotFoundException extends HelpDeskClientException {

    private static final long serialVersionUID = 1L;

    public TicketNotFoundException(String message) {
        super(message);
    }

    public TicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
