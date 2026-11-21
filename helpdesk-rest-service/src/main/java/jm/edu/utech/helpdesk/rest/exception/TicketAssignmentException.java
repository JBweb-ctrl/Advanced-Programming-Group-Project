package jm.edu.utech.helpdesk.rest.exception;

//ticket cant be assigned, another admin already did it (409)
public class TicketAssignmentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TicketAssignmentException(String message) {
        super(message);
    }

    public TicketAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
