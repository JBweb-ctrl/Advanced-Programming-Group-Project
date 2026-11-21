package jm.edu.utech.helpdesk.rest.model;

//the seven statuses from the project brief
public enum TicketStatus {
    NEW,
    ASSIGNED,
    IN_PROGRESS,
    PENDING,
    RESOLVED,
    CLOSED,
    REOPENED;

//turns text into a status (any case), error if its not one of the seven
    public static TicketStatus parse(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }
        try {
            return TicketStatus.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown ticket status: " + text);
        }
    }
}