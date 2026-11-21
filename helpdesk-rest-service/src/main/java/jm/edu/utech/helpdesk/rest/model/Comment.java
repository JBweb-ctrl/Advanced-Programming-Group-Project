package jm.edu.utech.helpdesk.rest.model;

public record Comment(
        String commentId,
        String ticketId,
        String author,
        String text,
        String createdAt) {
}
