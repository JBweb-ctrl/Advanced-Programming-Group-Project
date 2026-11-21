package jm.edu.utech.helpdesk.service;
//converts what the REST server sends (ServerTicket, ServerComment) into the client's Ticket and Comment
//the server and the client name and type some fields differently, this is the one place that deals with it


import jm.edu.utech.helpdesk.dto.ServerComment;
import jm.edu.utech.helpdesk.dto.ServerTicket;
import jm.edu.utech.helpdesk.model.Comment;
import jm.edu.utech.helpdesk.model.Ticket;

public final class TicketMapper {

	private TicketMapper() {}

	//one ticket from the server -> client Ticket
	public static Ticket fromServer(ServerTicket server) {
		Ticket ticket = new Ticket();
		ticket.setId(idFromServer(server.ticketId()));
		ticket.setSubject(server.subject());
		ticket.setDescription(server.description());
		ticket.setPriority(priorityName(server.priority()));
		ticket.setStatus(server.status());
		ticket.setCreationDate(dateOnly(server.creationDate()));
		ticket.setAssignedTechnician(server.assignedTechnician());
		ticket.setResolutionNotes(server.resolutionNotes());
		ticket.setSubmittedBy(server.submittedBy());
		return ticket;
	}

	
	//one comment from the server -> client Comment (the server already sends the date like 2026-11-15 18:20)
	public static Comment commentFromServer(ServerComment server) {
		String date = server.createdAt() == null ? "" : server.createdAt();
		return new Comment(server.author(), server.text(), date);
		}

	//"TKT-1001" -> 1001 (the client keeps a Long id)
	static Long idFromServer(String ticketId) {
		if (ticketId == null) {
			return null;
		}
		return Long.valueOf(ticketId.replace("TKT-", ""));
	}

	//1001 -> "TKT-1001" (the id the server expects in the address, the opposite of idFromServer)
	public static String toServerId(Long id) {
		return "TKT-" + id;
	}

	//server uses 1, 2, 3 and the client uses LOW, MEDIUM, HIGH
	static String priorityName(int priority) {
		if (priority == 1) {
			return "LOW";
		}
		if (priority == 3) {
			return "HIGH";
		}
		return "MEDIUM";
	}

	//client LOW, MEDIUM, HIGH -> server 1, 2, 3 (the opposite of priorityName)
	static int priorityNumber(String priority) {
		if ("LOW".equals(priority)) {
			return 1;
		}
		if ("HIGH".equals(priority)) {
			return 3;
		}
		return 2;
	}

	//server sends 2026-11-15 18:20, the ticket table and search only use the 2026-11-15 part
	static String dateOnly(String date) {
		if (date == null || date.length() < 10) {
			return "";
		}
			return date.substring(0, 10);
	}
}
