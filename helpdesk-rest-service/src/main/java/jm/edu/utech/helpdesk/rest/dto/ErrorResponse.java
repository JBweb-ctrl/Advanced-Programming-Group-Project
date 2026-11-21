package jm.edu.utech.helpdesk.rest.dto;

//the JSON sent back for every error, error is a code like TICKET_NOT_FOUND
public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path) {
}
