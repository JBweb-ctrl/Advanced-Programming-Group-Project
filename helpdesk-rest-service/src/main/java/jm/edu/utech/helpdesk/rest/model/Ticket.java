package jm.edu.utech.helpdesk.rest.model;

//priority is 1 LOW, 2 MEDIUM, 3 HIGH (HIGH is urgent) then on the next line 
public record Ticket(
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
