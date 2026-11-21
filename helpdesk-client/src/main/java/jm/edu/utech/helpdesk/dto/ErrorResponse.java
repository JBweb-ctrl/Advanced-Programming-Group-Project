package jm.edu.utech.helpdesk.dto;

//the JSON error the REST server sends when something goes wrong (built by its GlobalExceptionHandler)
//error is the code like TICKET_NOT_FOUND, message is the text to show the user
public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path) {
}
