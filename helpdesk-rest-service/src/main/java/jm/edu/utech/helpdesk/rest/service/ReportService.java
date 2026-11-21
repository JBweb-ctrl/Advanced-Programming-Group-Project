package jm.edu.utech.helpdesk.rest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import jm.edu.utech.helpdesk.rest.model.Ticket;

//builds the report for the admin on the server side
@Service
public class ReportService {

    private final TicketService ticketService;
    private final TechnicianService technicianService;

    @Autowired
    public ReportService(TicketService ticketService, TechnicianService technicianService) {
        this.ticketService = ticketService;
        this.technicianService = technicianService;
    }
    //all tickets users submitted
    public String ticketsSubmitted() {
        List<Ticket> all = ticketService.getAlltickets();
        //collect each unique user name
        List<String> users = new ArrayList<>();
        for (Ticket t : all) {
            if (t.submittedBy() != null && !users.contains(t.submittedBy())) {
                users.add(t.submittedBy());
            }
        }

        StringBuilder report = new StringBuilder("Tickets Submitted\nTotal: " + all.size() + "\n");
        //ticket count for each user
        for (String user : users) {
            int count = 0;
            for (Ticket t : all) {
                if (user.equals(t.submittedBy())) {
                    count++;
                }
            }
            report.append(user).append(": ").append(count).append("\n");
        }
        return report.toString();
    }
    //resolved tickets
    public String ticketsResolved() {
        StringBuilder lines = new StringBuilder();
        int count = 0;
        for (Ticket t : ticketService.getAlltickets()) {
            if ("RESOLVED".equals(t.status())) {
                count++;
                lines.append("#").append(shortId(t)).append(" ").append(t.subject())
                     .append(" (").append(t.assignedTechnician()).append(")\n");
            }
        }
        return "Tickets Resolved\nTotal: " + count + "\n" + lines;
    }
    //open tickets
    public String openTickets() {
        StringBuilder lines = new StringBuilder();
        int count = 0;
        for (Ticket t : ticketService.getAlltickets()) {
            if (isOpen(t)) {
                count++;
                lines.append("#").append(shortId(t)).append("  ").append(t.subject())
                     .append("  [").append(t.status()).append("]\n");
            }
        }
        return "Open Tickets\nTotal: " + count + "\n" + lines;
    }
    //tech workload
    public String technicianWorkload() {
        List<Ticket> all = ticketService.getAlltickets();
        StringBuilder report = new StringBuilder("Technician Workload\n");
        for (String technician : technicianService.getTechnicianNames()) {
            int open = 0;
            int total = 0;
            for (Ticket t : all) {
                if (technician.equalsIgnoreCase(t.assignedTechnician())) {
                    total++;
                    if (isOpen(t)) {
                        open++;
                    }
                }
            }
            report.append(technician).append(": ").append(open).append(" open & ")
                  .append(total).append(" assigned in total\n");
        }
        return report.toString();
    }
    //average of how long it takes to resolve a ticket (minutes between the two dates)
    public String averageResolutionTime() {
        long totalMinutes = 0;
        int count = 0;
        for (Ticket t : ticketService.getAlltickets()) {
            if (!isOpen(t) && !t.creationDate().isEmpty() && !t.resolvedDate().isEmpty()) {
                LocalDateTime created = LocalDateTime.parse(t.creationDate(), TicketService.DATE_FORMAT);
                LocalDateTime resolved = LocalDateTime.parse(t.resolvedDate(), TicketService.DATE_FORMAT);
                totalMinutes += Duration.between(created, resolved).toMinutes();
                count++;
            }
        }
        if (count == 0) {
            return "Average Resolution Time\nNo resolved tickets yet.\n";
        }
        long avgMinutes = totalMinutes / count;
        return "Average Resolution Time\nResolved tickets: " + count + "\nAverage: "
                + (avgMinutes / 60) + " hours " + (avgMinutes % 60) + " minutes\n";
    }

    private boolean isOpen(Ticket t) {
        return !"RESOLVED".equals(t.status()) && !"CLOSED".equals(t.status());
    }

    //"TKT-1001" -> "1001" so the report matches the ID the client shows in its tables
    private String shortId(Ticket t) {
        return t.ticketId() == null ? "" : t.ticketId().replace("TKT-", "");
    }
}
