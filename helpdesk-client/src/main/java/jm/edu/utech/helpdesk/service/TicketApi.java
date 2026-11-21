package jm.edu.utech.helpdesk.service;
//asks the REST server for tickets (uses ApiClient to connect and TicketMapper to convert)
//throws HelpDeskClientException when something goes wrong, the caller shows it with ErrorHandler
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import jm.edu.utech.helpdesk.dto.ServerComment;
import jm.edu.utech.helpdesk.dto.ServerTicket;
import jm.edu.utech.helpdesk.model.Comment;
import jm.edu.utech.helpdesk.model.Ticket;

public final class TicketApi {

	private TicketApi() {}

	//every ticket on the server
	public static List<Ticket> getAllTickets() {
		ApiResponse response = ApiClient.send("GET", "/tickets", null);
		List<ServerTicket> fromServer = ApiClient.read(response, new TypeReference<List<ServerTicket>>() {});

		List<Ticket> tickets = new ArrayList<>();
		for (ServerTicket server : fromServer) {
			tickets.add(TicketMapper.fromServer(server));
		}
		return tickets;
	}

	//only the tickets assigned to one technician (the server has no filter for this so it is done here)
	public static List<Ticket> getTicketsByTechnician(String technician) {
		List<Ticket> mine = new ArrayList<>();
		for (Ticket ticket : getAllTickets()) {
			if (technician != null && technician.equalsIgnoreCase(ticket.getAssignedTechnician())) {
				mine.add(ticket);
			}
		}
		return mine;
	}

	//only the tickets one user submitted (the server has no filter for this so it is done here)
	public static List<Ticket> getTicketsByUser(String username) {
		List<Ticket> mine = new ArrayList<>();
		for (Ticket ticket : getAllTickets()) {
			if (username != null && username.equalsIgnoreCase(ticket.getSubmittedBy())) {
				mine.add(ticket);
			}
		}
		return mine;
	}

	//sends a new ticket to the server, gives back the ticket as the server saved it (with its new id)
	public static Ticket createTicket(String subject, String description, String priority, String submittedBy) {
		ApiResponse response = ApiClient.send("POST", "/tickets",
				Map.of("subject", subject, "description", description,
						"priority", TicketMapper.priorityNumber(priority), "submittedBy", submittedBy));
		return TicketMapper.fromServer(ApiClient.read(response, ServerTicket.class));
	}

	//saves the edited subject, description and priority on the server
	public static Ticket updateTicket(Ticket ticket, String subject, String description, String priority) {
		ApiResponse response = ApiClient.send("PUT", "/tickets/" + TicketMapper.toServerId(ticket.getId()),
				Map.of("subject", subject, "description", description,
						"priority", TicketMapper.priorityNumber(priority)));
		return TicketMapper.fromServer(ApiClient.read(response, ServerTicket.class));
	}

	//all the comments on one ticket
	public static List<Comment> getComments(Ticket ticket) {
		ApiResponse response = ApiClient.send("GET", "/tickets/" + TicketMapper.toServerId(ticket.getId()) + "/comments", null);
		List<ServerComment> fromServer = ApiClient.read(response, new TypeReference<List<ServerComment>>() {});

		List<Comment> comments = new ArrayList<>();
		for (ServerComment server : fromServer) {
			comments.add(TicketMapper.commentFromServer(server));
		}
		return comments;
	}

	//adds a comment to one ticket, gives back the comment as the server saved it
	public static Comment addComment(Ticket ticket, String author, String text) {
		ApiResponse response = ApiClient.send("POST", "/tickets/" + TicketMapper.toServerId(ticket.getId()) + "/comments",
				Map.of("author", author, "text", text));
		return TicketMapper.commentFromServer(ApiClient.read(response, ServerComment.class));
	}

	//admin gives a ticket to a technician. If another admin already assigned it the server says no (409)
	//and ApiClient throws TicketAssignmentException
	public static Ticket assignTicket(Ticket ticket, String technician) {
		ApiResponse response = ApiClient.send("PUT", "/tickets/" + TicketMapper.toServerId(ticket.getId()) + "/assign",
				Map.of("technician", technician));
		return TicketMapper.fromServer(ApiClient.read(response, ServerTicket.class));
	}

	//admin search: gets every ticket from the server and keeps the ones that match (empty box = no filter)
	//same checks as the old in-memory search, the server has no search endpoint yet
	public static List<Ticket> searchTickets(String id, String user, String technician, String status,
			String priority, String fromDate, String toDate) {
		List<Ticket> results = new ArrayList<>();
		for (Ticket t : getAllTickets()) {
			if (!id.isEmpty() && !String.valueOf(t.getId()).equals(id)) {
				continue;
			}
			if (!user.isEmpty() && (t.getSubmittedBy() == null
					|| !t.getSubmittedBy().toLowerCase().contains(user.toLowerCase()))) {
				continue;
			}
			if (!technician.isEmpty() && !technician.equalsIgnoreCase(t.getAssignedTechnician())) {
				continue;
			}
			if (!status.isEmpty() && !status.equals(t.getStatus())) {
				continue;
			}
			if (!priority.isEmpty() && !priority.equals(t.getPriority())) {
				continue;
			}
			//dates are yyyy-MM-dd text, so comparing the text compares the dates
			if (!fromDate.isEmpty() && t.getCreationDate().compareTo(fromDate) < 0) {
				continue;
			}
			if (!toDate.isEmpty() && t.getCreationDate().compareTo(toDate) > 0) {
				continue;
			}
			results.add(t);
		}
		return results;
	}

	//changes the status of one ticket on the server, gives back the ticket as the server saved it
	public static Ticket updateStatus(Ticket ticket, String status) {
		ApiResponse response = ApiClient.send("PUT", "/tickets/" + TicketMapper.toServerId(ticket.getId()) + "/status",
				Map.of("status", status));
		return TicketMapper.fromServer(ApiClient.read(response, ServerTicket.class));
	}

	//saves the resolution notes and marks the ticket RESOLVED on the server
	public static Ticket resolveTicket(Ticket ticket, String notes) {
		ApiResponse response = ApiClient.send("PUT", "/tickets/" + TicketMapper.toServerId(ticket.getId()) + "/resolve",
				Map.of("notes", notes));
		return TicketMapper.fromServer(ApiClient.read(response, ServerTicket.class));
	}
}
