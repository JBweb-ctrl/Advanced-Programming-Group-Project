package jm.edu.utech.helpdesk.exception;

//ticket cant be assigned, another admin already did it
public class TicketAssignmentException extends HelpDeskClientException {

    private static final long serialVersionUID = 1L;

    public TicketAssignmentException(String message) {
        super(message);
    }

    public TicketAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
