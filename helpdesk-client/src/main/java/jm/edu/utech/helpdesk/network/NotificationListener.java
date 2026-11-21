package jm.edu.utech.helpdesk.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jm.edu.utech.helpdesk.notification.model.NotificationMessage;
import jm.edu.utech.helpdesk.notification.model.Request;
import jm.edu.utech.helpdesk.notification.model.Response;
import jm.edu.utech.helpdesk.util.NotificationPopup;

//background thread that stays connected to the notification server and shows each notification as a popup
//if the server is down it tries again every 5 seconds, so the window never freezes
public class NotificationListener extends Thread {

    private static final Logger logger = LogManager.getLogger(NotificationListener.class);

    private static final String HOST = "localhost";
    private static final int PORT = 9090;

    private final String username;
    private Socket socket;

    public NotificationListener(String username) {
        super("notification-listener-" + username);
        this.username = username;
        setDaemon(true);   //so this thread never keeps the program open after the window closes
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                socket = new Socket(HOST, PORT);

                //output first then input, if both sides open the input first they wait on each other forever
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                output.flush();
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                //tell the server who we are
                output.writeObject(new Request("REGISTER", username, null));
                output.flush();
                Response response = (Response) input.readObject();
                logger.info("Connected to notification server {}:{} as {} ({})", HOST, PORT, username, response.getMessage());

                //wait for notifications until the connection closes
                while (true) {
                    NotificationMessage message = (NotificationMessage) input.readObject();
                    logger.info("Notification received: {} for ticket {}", message.getType(), message.getTicketId());
                    NotificationPopup.show(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                if (isInterrupted()) {
                    break;   //stopListening() was called, so this error is expected
                }
                logger.warn("Notification connection problem: {}", e.getMessage());
            } finally {
                closeSocket();
            }

            //wait 5 seconds then try to connect again
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }
        logger.info("Notification listener stopped for {}", username);
    }

    //called on logout, interrupt() ends the loop and closing the socket wakes up readObject()
    public void stopListening() {
        interrupt();
        closeSocket();
    }

    //closes the connection if there is one
    private void closeSocket() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            logger.debug("Error closing socket", e);
        }
    }
}
