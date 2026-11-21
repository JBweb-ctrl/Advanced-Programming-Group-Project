package jm.edu.utech.helpdesk.dto;

//a comment exactly as the REST server sends it in JSON (the names must match the server's Comment record)
public record ServerComment(
        String commentId,
        String ticketId,
        String author,
        String text,
        String createdAt) {
}
