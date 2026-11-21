package jm.edu.utech.helpdesk.notification.model;

//the four notifications the project needs, the label is what the popup shows
//this exact file is copied into all three projects (same package), like Guided Lab 9 copies Book/Request/Response
public enum NotificationType {
    TICKET_ASSIGNED("Ticket Assigned"),
    TICKET_RESOLVED("Ticket Resolved"),
    COMMENT_ADDED("New Comment Added"),
    URGENT_TICKET_CREATED("Urgent Ticket Created");

    private final String label;

    NotificationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
