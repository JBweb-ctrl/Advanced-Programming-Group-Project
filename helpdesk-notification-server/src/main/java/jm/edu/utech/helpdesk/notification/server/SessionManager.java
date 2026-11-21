package jm.edu.utech.helpdesk.notification.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jm.edu.utech.helpdesk.notification.model.NotificationMessage;

//keeps track of which user is connected on which ClientHandler
//every method is synchronized because many ClientHandler threads use it at once
public class SessionManager {

    private static final Logger logger = LogManager.getLogger(SessionManager.class);

    //username (lower case) -> that user's connection
    private final Map<String, ClientHandler> sessions = new HashMap<>();

    //a user connected, if they were already connected the old connection is closed
    public synchronized void register(String username, ClientHandler handler) {
        ClientHandler old = sessions.put(username.toLowerCase(), handler);
        if (old != null && old != handler) {
            logger.info("User {} reconnected, closing the old session", username);
            old.close();
        }
        logger.info("Session registered for {} (connected clients: {})", username, sessions.size());
    }

    //a user disconnected
    public synchronized void remove(String username, ClientHandler handler) {
        if (username == null) {
            return;
        }
        if (sessions.get(username.toLowerCase()) == handler) {
            sessions.remove(username.toLowerCase());
            logger.info("Session removed for {} (connected clients: {})", username, sessions.size());
        }
    }

    //sends the notification to one user or to everyone, returns how many got it
    public synchronized int deliver(NotificationMessage message) {
        if (message.isBroadcast()) {
            return broadcast(message);
        }
        return sendTo(message.getTargetUser(), message) ? 1 : 0;
    }

    //send to one user
    private boolean sendTo(String username, NotificationMessage message) {
        ClientHandler handler = sessions.get(username.toLowerCase());
        if (handler == null) {
            logger.info("User {} is not connected, {} not delivered", username, message);
            return false;
        }
        if (!handler.send(message)) {
            logger.warn("Notification failure: could not send to {}", username);
            sessions.remove(username.toLowerCase());
            handler.close();
            return false;
        }
        return true;
    }

    //send to everyone connected
    private int broadcast(NotificationMessage message) {
        int delivered = 0;
        List<String> failed = new ArrayList<>();
        for (String username : sessions.keySet()) {
            if (sessions.get(username).send(message)) {
                delivered++;
            } else {
                logger.warn("Notification failure: could not send to {}", username);
                failed.add(username);
            }
        }
        //remove the broken connections after the loop (can't remove from a map while looping over it)
        for (String username : failed) {
            sessions.remove(username).close();
        }
        return delivered;
    }
}
