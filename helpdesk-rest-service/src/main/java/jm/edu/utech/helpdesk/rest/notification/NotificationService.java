package jm.edu.utech.helpdesk.rest.notification;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jm.edu.utech.helpdesk.notification.model.NotificationMessage;
import jm.edu.utech.helpdesk.notification.model.NotificationType;
import jm.edu.utech.helpdesk.rest.exception.NotificationException;
import jakarta.annotation.PreDestroy;

//builds the four notification types and sends them on background threads
//uses a fixed thread pool so the REST request returns straight away
//if the notification server is down the failure is only logged, the ticket change still saves
@Service
public class NotificationService {

    private static final Logger logger = LogManager.getLogger(NotificationService.class);

    private final NotificationClient client;

    //2 worker threads that send the notifications
    private final ExecutorService pool = Executors.newFixedThreadPool(2);

    @Autowired
    public NotificationService(NotificationClient client) {
        this.client = client;
    }

    //tell the technician a ticket was assigned to them
    public void notifyTicketAssigned(String ticketId, String technician, String subject) {
        NotificationMessage message = new NotificationMessage(NotificationType.TICKET_ASSIGNED, ticketId, technician,
                "Ticket " + ticketId + " was assigned to you: " + subject);
        pool.submit(() -> send(message));
    }

    //tell the user who submitted the ticket that it was resolved
    public void notifyTicketResolved(String ticketId, String submittedBy, String subject) {
        NotificationMessage message = new NotificationMessage(NotificationType.TICKET_RESOLVED, ticketId, submittedBy,
                "Ticket " + ticketId + " has been resolved: " + subject);
        pool.submit(() -> send(message));
    }

    //tell the other person on the ticket that a comment was added
    public void notifyCommentAdded(String ticketId, String targetUser, String author) {
        NotificationMessage message = new NotificationMessage(NotificationType.COMMENT_ADDED, ticketId, targetUser,
                author + " added a comment to ticket " + ticketId);
        pool.submit(() -> send(message));
    }

    //tell everyone connected that a HIGH priority ticket was created
    public void notifyUrgentTicket(String ticketId, String subject) {
        NotificationMessage message = new NotificationMessage(NotificationType.URGENT_TICKET_CREATED, ticketId,
                NotificationMessage.BROADCAST, "URGENT ticket " + ticketId + ": " + subject);
        pool.submit(() -> send(message));
    }

    //runs on a pool thread, sends one message and logs if it fails
    private void send(NotificationMessage message) {
        try {
            client.publish(message);
            logger.info("Notification sent: {} for ticket {}", message.getType(), message.getTicketId());
        } catch (NotificationException e) {
            logger.error("Notification failure ({} for ticket {}): {}",
                    message.getType(), message.getTicketId(), e.getMessage());
        }
    }

    //spring calls this when the REST service stops, an executor must always be shut down
    @PreDestroy
    public void shutdown() {
        pool.shutdown();
    }
}
