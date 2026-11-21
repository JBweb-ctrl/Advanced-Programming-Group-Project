package jm.edu.utech.helpdesk.util;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import jm.edu.utech.helpdesk.dto.ErrorResponse;
import jm.edu.utech.helpdesk.exception.DuplicateUserException;
import jm.edu.utech.helpdesk.exception.HelpDeskClientException;
import jm.edu.utech.helpdesk.exception.InvalidLoginException;
import jm.edu.utech.helpdesk.exception.ServerConnectionException;
import jm.edu.utech.helpdesk.exception.TicketAssignmentException;
import jm.edu.utech.helpdesk.exception.TicketNotFoundException;
import jm.edu.utech.helpdesk.exception.UserNotFoundException;

//turns the custom exceptions into the JavaFX Alerts the project asks for and logs them
//(Ticket Not Found, Invalid Login Credentials, Unable to Connect to Server, Duplicate User Account)
//controllers call ErrorHandler
public final class ErrorHandler {

    private static final Logger logger = LogManager.getLogger(ErrorHandler.class);

    //reads the server's JSON error like in Guided Lab 5
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ErrorHandler() {}

    //logs the problem and shows the matching alert, must be called on the JavaFX thread
    public static void handle(Exception e) {
        if (e instanceof ServerConnectionException) {
            logger.error("Unable to connect to server", e);
            AlertUtil.showError("Unable to Connect to Server",
                    "The help desk server cannot be reached. Check that the REST service is running and try again.");
        } else if (e instanceof InvalidLoginException) {
            logger.warn("Invalid login: {}", e.getMessage());
            AlertUtil.showError("Invalid Login Credentials", e.getMessage());
        } else if (e instanceof DuplicateUserException) {
            logger.warn("Duplicate user: {}", e.getMessage());
            AlertUtil.showError("Duplicate User Account", e.getMessage());
        } else if (e instanceof TicketNotFoundException) {
            logger.warn("Ticket not found: {}", e.getMessage());
            AlertUtil.showError("Ticket Not Found", e.getMessage());
        } else if (e instanceof TicketAssignmentException) {
            logger.warn("Ticket assignment failed: {}", e.getMessage());
            AlertUtil.showWarning("Ticket Assignment", e.getMessage());
        } else if (e instanceof UserNotFoundException) {
            logger.warn("User not found: {}", e.getMessage());
            AlertUtil.showError("User Not Found", e.getMessage());
        } else {
            logger.error("Unexpected error", e);
            AlertUtil.showError("Unexpected Error", "Something went wrong: " + e.getMessage());
        }
    }

    //turns the server's JSON error into the matching custom exception (used by ApiClient)
    public static HelpDeskClientException fromHttpError(int statusCode, String responseBody) {
        String code = "";
        String message = "Request failed (HTTP " + statusCode + ")";
        try {
            ErrorResponse error = objectMapper.readValue(responseBody, ErrorResponse.class);
            code = error.error();
            message = error.message();
        } catch (IOException e) {
            //the body was not the JSON error, keep the default message
        }

        if ("TICKET_NOT_FOUND".equals(code)) {
            return new TicketNotFoundException(message);
        } else if ("USER_NOT_FOUND".equals(code)) {
            return new UserNotFoundException(message);
        } else if ("DUPLICATE_USER".equals(code)) {
            return new DuplicateUserException(message);
        } else if ("INVALID_CREDENTIALS".equals(code)) {
            return new InvalidLoginException(message);
        } else if ("TICKET_ASSIGNMENT_CONFLICT".equals(code)) {
            return new TicketAssignmentException(message);
        }
        return new HelpDeskClientException(message);
    }
}
