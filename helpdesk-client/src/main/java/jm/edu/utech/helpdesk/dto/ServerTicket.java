package jm.edu.utech.helpdesk.dto;

//a ticket exactly as the REST server sends it in JSON (the names must match the server Ticket record)
//TicketMapper turns it into the client's Ticket, the same way Guided Lab 5 reads JSON into the Book record
public record ServerTicket(
        String ticketId,
        String subject,
        String description,
        String creationDate,
        String resolvedDate,
        int priority,
        String status,
        String assignedTechnician,
        String resolutionNotes,
        String submittedBy
        ) {
}
