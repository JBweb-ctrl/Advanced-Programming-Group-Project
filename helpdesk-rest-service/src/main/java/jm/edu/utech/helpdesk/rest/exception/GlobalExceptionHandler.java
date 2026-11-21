package jm.edu.utech.helpdesk.rest.exception;

import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jm.edu.utech.helpdesk.rest.dto.ErrorResponse;

//turns every exception the controllers throw into a JSON error with the right status code and logs it
//the controllers and services just throw, they never build error responses themselves
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    //ticket id that does not exist (404)
    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTicketNotFound(TicketNotFoundException ex, HttpServletRequest req) {
        logger.warn("REST error: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "TICKET_NOT_FOUND", ex.getMessage(), req);
    }

    //user that does not exist (404)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest req) {
        logger.warn("REST error: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", ex.getMessage(), req);
    }

    //another admin already assigned the ticket (409)
    @ExceptionHandler(TicketAssignmentException.class)
    public ResponseEntity<ErrorResponse> handleAssignment(TicketAssignmentException ex, HttpServletRequest req) {
        logger.warn("REST error: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "TICKET_ASSIGNMENT_CONFLICT", ex.getMessage(), req);
    }

    //username already taken (409)
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUser(DuplicateUserException ex, HttpServletRequest req) {
        logger.warn("REST error: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "DUPLICATE_USER", ex.getMessage(), req);
    }

    //wrong username or password (401)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest req) {
        logger.warn("Invalid login attempt: {}", ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", ex.getMessage(), req);
    }

    //notification server could not be reached (503)
    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<ErrorResponse> handleNotification(NotificationException ex, HttpServletRequest req) {
        logger.error("Notification failure", ex);
        return build(HttpStatus.SERVICE_UNAVAILABLE, "NOTIFICATION_FAILURE", ex.getMessage(), req);
    }

    //missing or wrong values sent by the client (400)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex, HttpServletRequest req) {
        logger.warn("REST error (bad request): {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), req);
    }

    //body that is not valid JSON (400)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        logger.warn("REST error (malformed JSON): {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "MALFORMED_JSON", "Request body is missing or not valid JSON", req);
    }

    //address that has no endpoint, like a typo from the browser (404)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoEndpoint(NoResourceFoundException ex, HttpServletRequest req) {
        logger.warn("REST error: no endpoint at {}", req.getRequestURI());
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", "No endpoint at " + req.getRequestURI(), req);
    }

    //MySQL is down or a query failed (503), this is the Database Errors logging from the brief
    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorResponse> handleDatabase(PersistenceException ex, HttpServletRequest req) {
        logger.error("Database connection failed on {}", req.getRequestURI(), ex);
        return build(HttpStatus.SERVICE_UNAVAILABLE, "DATABASE_ERROR", "Database is unavailable, try again later", req);
    }

    //anything not handled above (500), logged with the full error so it can be fixed
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest req) {
        logger.error("Unhandled exception on {}", req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Internal server error", req);
    }

    //builds the JSON error the client reads
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message, HttpServletRequest req) {
        ErrorResponse body = new ErrorResponse(Instant.now().toString(), status.value(), code, message,
                req.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
