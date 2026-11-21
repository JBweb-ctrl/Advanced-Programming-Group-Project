package jm.edu.utech.helpdesk.rest.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jm.edu.utech.helpdesk.rest.entity.Technician;
import jm.edu.utech.helpdesk.rest.entity.User;
import jm.edu.utech.helpdesk.rest.exception.TicketAssignmentException;
import jm.edu.utech.helpdesk.rest.exception.TicketNotFoundException;
import jm.edu.utech.helpdesk.rest.exception.UserNotFoundException;
import jm.edu.utech.helpdesk.rest.model.Comment;
import jm.edu.utech.helpdesk.rest.model.Ticket;
import jm.edu.utech.helpdesk.rest.model.TicketStatus;
import jm.edu.utech.helpdesk.rest.notification.NotificationService;
import jm.edu.utech.helpdesk.rest.persistence.CommentRepository;
import jm.edu.utech.helpdesk.rest.persistence.TechnicianRepository;
import jm.edu.utech.helpdesk.rest.persistence.TicketRepository;
import jm.edu.utech.helpdesk.rest.persistence.UserRepository;

//the ticket rules, tickets and comments are saved in MySQL through the repositories
//the controllers still get the same Ticket and Comment records, so the client JSON does not change
//the entities are also called Ticket and Comment, so they are written out in full (jm.edu.utech.helpdesk.rest.entity.Ticket)
//every ticket has its own ReentrantLock so two admins cant assign the same ticket at the same time
@Service
public class TicketService {

    private static final Logger logger = LogManager.getLogger(TicketService.class);

    //priority 3 (HIGH) or above sends the Urgent Ticket Created notification to everyone
    public static final int URGENT_PRIORITY = 3;

    private static final String PREFIX = "TKT-";

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    //one lock per ticket id
    private final Map<Long, ReentrantLock> locks = new HashMap<>();

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TechnicianRepository technicianRepository;
    private final NotificationService notifications;

    @Autowired
    public TicketService(TicketRepository ticketRepository, CommentRepository commentRepository,
            UserRepository userRepository, TechnicianRepository technicianRepository,
            NotificationService notifications) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.technicianRepository = technicianRepository;
        this.notifications = notifications;
    }

    //every ticket, oldest first
    public List<Ticket> getAlltickets() {
        List<Ticket> tickets = new ArrayList<>();
        for (jm.edu.utech.helpdesk.rest.entity.Ticket entity : ticketRepository.findAll()) {
            tickets.add(toModel(entity));
        }
        return tickets;
    }

    //one ticket by its id (TKT-1)
    public Ticket getTicketById(String id) {
        return toModel(requireTicket(id));
    }

    //creates a new ticket for the user who submitted it, HIGH priority tells everyone
    public Ticket addTicket(Ticket newTicket) {
        if (newTicket.subject() == null || newTicket.subject().isBlank()) {
            throw new IllegalArgumentException("Subject is required");
        }
        if (newTicket.submittedBy() == null || newTicket.submittedBy().isBlank()) {
            throw new IllegalArgumentException("Submitted by is required");
        }
        User user = findUser(newTicket.submittedBy());
        int priority = newTicket.priority() == 0 ? 1 : newTicket.priority();

        jm.edu.utech.helpdesk.rest.entity.Ticket entity = new jm.edu.utech.helpdesk.rest.entity.Ticket(newTicket.subject(), newTicket.description() == null ? "" : newTicket.description(), priorityName(priority), user);
        entity = ticketRepository.save(entity);
        Ticket created = toModel(entity);
        logger.info("Ticket created: {} (priority {}) by {}", created.ticketId(), created.priority(), created.submittedBy());

        if (priority >= URGENT_PRIORITY) {
            notifications.notifyUrgentTicket(created.ticketId(), created.subject());   //runs on a background thread
        }
        return created;
    }

    //changes the fields that were sent, anything left empty stays the same
    public Ticket updateTicket(String id, Ticket updatedInfo) {
        ReentrantLock lock = lockFor(id);
        lock.lock();
        try {
            jm.edu.utech.helpdesk.rest.entity.Ticket entity = requireTicket(id);
            if (updatedInfo.subject() != null) {
                entity.setSubject(updatedInfo.subject());
            }
            if (updatedInfo.description() != null) {
                entity.setDescription(updatedInfo.description());
            }
            if (updatedInfo.priority() != 0) {
                entity.setPriority(priorityName(updatedInfo.priority()));
            }
            if (updatedInfo.status() != null) {
                setStatus(entity, TicketStatus.parse(updatedInfo.status()));
            }
            if (updatedInfo.assignedTechnician() != null) {
                entity.setTechnician(findTechnicianOrNone(updatedInfo.assignedTechnician()));
            }
            if (updatedInfo.resolutionNotes() != null) {
                entity.setResolutionNotes(updatedInfo.resolutionNotes());
            }
            entity = ticketRepository.save(entity);
            logger.info("Ticket updated: {}", id);
            return toModel(entity);
        } finally {
            lock.unlock();
        }
    }

    //deletes a ticket, its comments go with it
    public void deleteTicket(String id) {
        ReentrantLock lock = lockFor(id);
        lock.lock();
        try {
            jm.edu.utech.helpdesk.rest.entity.Ticket entity = requireTicket(id);
            ticketRepository.delete(entity.getId());
            logger.info("Ticket deleted: {}", id);
        } finally {
            lock.unlock();
        }
    }

    //assigns a NEW or REOPENED ticket to a technician
    //the second admin waits at lock(), then sees it is already ASSIGNED and gets a TicketAssignmentException
    public Ticket assignTicket(String id, String technician) {
        if (technician == null || technician.isBlank()) {
            throw new IllegalArgumentException("Technician is required");
        }
        Technician tech = findTechnician(technician);
        ReentrantLock lock = lockFor(id);
        lock.lock();
        Ticket assigned;
        try {
            //read it again inside the lock, the first admin may have just finished
            jm.edu.utech.helpdesk.rest.entity.Ticket entity = requireTicket(id);
            TicketStatus status = TicketStatus.parse(entity.getStatus());
            if (status != TicketStatus.NEW && status != TicketStatus.REOPENED) {
                String current = entity.getTechnician() == null ? "Unassigned" : entity.getTechnician().getUsername();
                logger.warn("Assignment rejected: ticket {} is already {} (technician {})", id, status, current);
                throw new TicketAssignmentException("Ticket " + id + " is already " + status
                        + " (technician: " + current + ")");
            }
            entity.setTechnician(tech);
            entity.setStatus(TicketStatus.ASSIGNED.name());
            assigned = toModel(ticketRepository.save(entity));
            logger.info("Ticket assigned: {} -> {}", id, tech.getUsername());
        } finally {
            lock.unlock();
        }
        notifications.notifyTicketAssigned(id, assigned.assignedTechnician(), assigned.subject());
        return assigned;
    }

    //technician changes the status (IN_PROGRESS, PENDING and so on)
    public Ticket updateStatus(String id, String newStatus) {
        TicketStatus status = TicketStatus.parse(newStatus);
        ReentrantLock lock = lockFor(id);
        lock.lock();
        try {
            jm.edu.utech.helpdesk.rest.entity.Ticket entity = requireTicket(id);
            String old = entity.getStatus();
            setStatus(entity, status);
            entity = ticketRepository.save(entity);
            logger.info("Ticket {} status changed {} -> {}", id, old, status);
            return toModel(entity);
        } finally {
            lock.unlock();
        }
    }

    //technician resolves the ticket with notes, the user who submitted it is told
    public Ticket resolveTicket(String id, String notes) {
        if (notes == null || notes.isBlank()) {
            throw new IllegalArgumentException("Resolution notes are required");
        }
        ReentrantLock lock = lockFor(id);
        lock.lock();
        Ticket resolved;
        try {
            jm.edu.utech.helpdesk.rest.entity.Ticket entity = requireTicket(id);
            entity.setResolutionNotes(notes.trim());
            setStatus(entity, TicketStatus.RESOLVED);
            resolved = toModel(ticketRepository.save(entity));
            logger.info("Ticket resolved: {}", id);
        } finally {
            lock.unlock();
        }
        notifications.notifyTicketResolved(id, resolved.submittedBy(), resolved.subject());
        return resolved;
    }

    //adds a comment, the ticket owner and technician are told (not whoever wrote it)
    public Comment addComment(String id, String author, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Comment text is required");
        }
        jm.edu.utech.helpdesk.rest.entity.Ticket ticket = requireTicket(id);
        String writer = author == null || author.isBlank() ? "unknown" : author.trim();
        jm.edu.utech.helpdesk.rest.entity.Comment entity = commentRepository.save(
                new jm.edu.utech.helpdesk.rest.entity.Comment(ticket, writer, text.trim()));
        Comment comment = toModel(entity, id);
        logger.info("Comment added to ticket {} by {}", id, writer);

        Ticket model = toModel(ticket);
        notifyIfOther(id, model.submittedBy(), writer);
        notifyIfOther(id, model.assignedTechnician(), writer);
        return comment;
    }

    //every comment on a ticket, oldest first
    public List<Comment> getComments(String id) {
        jm.edu.utech.helpdesk.rest.entity.Ticket ticket = requireTicket(id);
        List<Comment> comments = new ArrayList<>();
        for (jm.edu.utech.helpdesk.rest.entity.Comment entity : commentRepository.findByTicketId(ticket.getId())) {
            comments.add(toModel(entity, id));
        }
        return comments;
    }

    //sends the comment notification unless the target wrote the comment
    private void notifyIfOther(String ticketId, String target, String author) {
        if (target != null && !target.isBlank() && !"Unassigned".equals(target)
                && !target.equalsIgnoreCase(author)) {
            notifications.notifyCommentAdded(ticketId, target, author);
        }
    }

    //sets the status, RESOLVED or CLOSED gets a resolved date, anything else clears it
    private void setStatus(jm.edu.utech.helpdesk.rest.entity.Ticket entity, TicketStatus status) {
        entity.setStatus(status.name());
        if (status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED) {
            if (entity.getResolvedDate() == null) {
                entity.setResolvedDate(LocalDateTime.now());
            }
        } else {
            entity.setResolvedDate(null);
        }
    }

    //the database row for TKT-, error if there is none
    private jm.edu.utech.helpdesk.rest.entity.Ticket requireTicket(String id) {
        Optional<jm.edu.utech.helpdesk.rest.entity.Ticket> found = ticketRepository.findById(parseId(id));
        if (found.isEmpty()) {
            throw new TicketNotFoundException("Ticket not found: " + id);
        }
        return found.get();
    }

    //the user who submitted a ticket
    private User findUser(String username) {
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isEmpty()) {
            throw new UserNotFoundException("User not found: " + username);
        }
        return found.get();
    }

    //the technician a ticket is assigned to
    private Technician findTechnician(String username) {
        Optional<Technician> found = technicianRepository.findByUsername(username);
        if (found.isEmpty()) {
            throw new UserNotFoundException("Technician not found: " + username);
        }
        return found.get();
    }

    //same as findTechnician but "Unassigned" or empty means no technician
    private Technician findTechnicianOrNone(String username) {
        if (username.isBlank() || "Unassigned".equalsIgnoreCase(username)) {
            return null;
        }
        return findTechnician(username);
    }

    //the lock for a ticket, made the first time it is needed
    //synchronized so two threads cant make two different locks for the same ticket
    private synchronized ReentrantLock lockFor(String id) {
        long key = parseId(id);
        ReentrantLock lock = locks.get(key);
        if (lock == null) {
            lock = new ReentrantLock();
            locks.put(key, lock);
        }
        return lock;
    }

    //"TKT-1" -> 1, anything else is a ticket that does not exist
    private long parseId(String id) {
        if (id == null || !id.startsWith(PREFIX)) {
            throw new TicketNotFoundException("Ticket not found: " + id);
        }
        try {
            return Long.parseLong(id.substring(PREFIX.length()));
        } catch (NumberFormatException e) {
            throw new TicketNotFoundException("Ticket not found: " + id);
        }
    }

    //turns the ticket entity into the record the client gets as JSON
    private Ticket toModel(jm.edu.utech.helpdesk.rest.entity.Ticket entity) {
        String technician = entity.getTechnician() == null ? "Unassigned" : entity.getTechnician().getUsername();
        String notes = entity.getResolutionNotes() == null ? "" : entity.getResolutionNotes();
        return new Ticket(
                PREFIX + entity.getId(),
                entity.getSubject(),
                entity.getDescription(),
                toText(entity.getCreationDate()),
                toText(entity.getResolvedDate()),
                priorityNumber(entity.getPriority()),
                entity.getStatus(),
                technician,
                notes,
                entity.getUser().getUsername());
    }

    //turns the comment entity into the record the client gets as JSON
    private Comment toModel(jm.edu.utech.helpdesk.rest.entity.Comment entity, String ticketId) {
        return new Comment("CMT-" + entity.getId(), ticketId, entity.getAuthor(), entity.getText(),
        		toText(entity.getCommentDate()));
    }

    //the database keeps dates as LocalDateTime, the client uses seconds (0 means no date)
    private String toText(LocalDateTime date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMAT);
    }

    //the client sends 1, 2 or 3, the database keeps LOW, MEDIUM or HIGH
    private String priorityName(int priority) {
        if (priority >= 3) {
            return "HIGH";
        }
        if (priority == 2) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private int priorityNumber(String priority) {
        if ("HIGH".equalsIgnoreCase(priority)) {
            return 3;
        }
        if ("MEDIUM".equalsIgnoreCase(priority)) {
            return 2;
        }
        return 1;
    }
}
