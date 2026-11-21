package jm.edu.utech.helpdesk.service;
//this was a class for demo info and testing the GUI this stuff like the demo is now in sql 
/*import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jm.edu.utech.helpdesk.model.Ticket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//keeps tickets in one place
public class TicketService {
	
	//copy of the service that each controller use
    private static final Logger logger = LogManager.getLogger(TicketService.class);
    private static final TicketService INSTANCE = new TicketService();
    
    //all the tickets in memory, list of all ticket objects
    private final ObservableList<Ticket> tickets = FXCollections.observableArrayList();
    private long nextId = 1; // placeholder ticket number until the database generates IDs

    //four demo tickets for testing (hardcoded tickets)
    private TicketService() {
        seed("Wi-Fi down in Block A", "Wi-Fi drops every few minutes.", "HIGH", "ASSIGNED", "tech", "student1");
        seed("Projector not working", "Projector in Room 47C-2 shows no signal.", "MEDIUM", "ASSIGNED", "tech", "student1");
        seed("Cannot reset password", "Reset email never arrives.", "LOW", "IN_PROGRESS", "tech", "student1");
        seed("Cannot access student portal", "Portal shows an error page.", "MEDIUM", "NEW", null, "student1");
    }
    
    public static TicketService getInstance() {
        return INSTANCE;
    }
    
    //creates a demo ticket and add it to the list
    //Temporary goes away when backend exist
    private void seed(String subject, String description, String priority,String status, String technician, String submittedBy) {
        Ticket t = new Ticket(nextId++, subject, priority, status, LocalDate.now().toString());
        t.setDescription(description);
        t.setAssignedTechnician(technician);
        t.setSubmittedBy(submittedBy);
        tickets.add(t);
    }
    //create a new ticket for users. Is synhronized so two users can't have the same ID
    public synchronized Ticket createTicket(String subject, String description, String priority, String submittedBy) {
        Ticket t = new Ticket(nextId++, subject, priority, "NEW", LocalDate.now().toString());
        t.setDescription(description);
        t.setSubmittedBy(submittedBy);
        tickets.add(t);
        logger.info("Ticket created: {} by {} (priority {})", t.getId(), submittedBy, priority);
        return t;
    }
    
    //returns every ticket for the admin
    public synchronized List<Ticket> getAllTickets() {
        return List.copyOf(tickets);
    }
    //returns tickets made by user
    public synchronized List<Ticket> getTicketsByUser(String username) {
        return tickets.stream().filter(t -> username.equalsIgnoreCase(t.getSubmittedBy())).collect(Collectors.toList());
    }
    //returns tickets assigned to technicians
    public synchronized List<Ticket> getTicketsByTechnician(String technician) {
        return tickets.stream().filter(t -> technician.equalsIgnoreCase(t.getAssignedTechnician())).collect(Collectors.toList());
    }
    //assign a ticket to a tech as the admin, cant assign the same ticket one time
    public synchronized void assignTicket(Ticket ticket, String technician) {
        logger.info("Ticket assigned: {} -> {}", ticket.getId(), technician);
        ticket.setAssignedTechnician(technician);
        ticket.setStatus("ASSIGNED");
    }
    //update ticket details
    public synchronized void updateTicket(Ticket ticket, String subject, String description, String priority) {
    	ticket.setSubject(subject);
    	ticket.setDescription(description);
    	ticket.setPriority(priority);
    	}
    //mark a ticket closed
    public synchronized void closeTicket(Ticket ticket) {
    	ticket.setStatus("CLOSED");
    }
    //mark a ticket reopened
    public synchronized void reopenTicket(Ticket ticket) {
    	ticket.setStatus("REOPENED");
    }
    
    //makes a ticket status todays date if it was just resolved (for average resolved time)
    public synchronized void updateStatus(Ticket ticket, String status) {
    	logger.info("Ticket {} status changed to {}", ticket.getId(), status);
    	ticket.setStatus(status);
    	if (status.equals("RESOLVED")) {
    		ticket.setResolvedDate(LocalDate.now().toString());
    	}
    }
    
    //saves the notes marks the ticket resolved and record todays date
    public synchronized void resolveTicket(Ticket ticket, String notes) {
    	logger.info("Ticket resolved: {}", ticket.getId());
    	ticket.setResolutionNotes(notes);
    	ticket.setStatus("RESOLVED");
    	ticket.setResolvedDate(LocalDate.now().toString());
    	
    }
    
    //used for check tickets based on what admin filter to see 
    public synchronized List<Ticket> searchTickets(String id, String user, String technician, String status, String priority, String fromDate, String toDate){
    	List<Ticket> results = new ArrayList<>();
    	
    	for (Ticket t : tickets) {
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
            //dates are stored as yyyy-MM-dd text, so comparing the text compares the dates
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
    } */