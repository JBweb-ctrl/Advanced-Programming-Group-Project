package jm.edu.utech.helpdesk.exception;

//The REST service or Notification Server could not be reached
public class ServerConnectionException extends HelpDeskClientException {

    private static final long serialVersionUID = 1L;

    public ServerConnectionException(String message) {
        super(message);
    }

    public ServerConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
