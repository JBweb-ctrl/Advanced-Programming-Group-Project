package jm.edu.utech.helpdesk.notification.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//the TCP notification server
//waits for connections and starts one ClientHandler thread for each one
public class NotificationServer {

    private static final Logger logger = LogManager.getLogger(NotificationServer.class);

    public static final int PORT = 9090;

    //who is connected right now (shared by every ClientHandler)
    private final SessionManager sessions = new SessionManager();

    //open the server socket and keep accepting clients
    public void start() {
        try {
            ServerSocket server = new ServerSocket(PORT);
            logger.info("Notification server listening on port {}", PORT);

            while (true) {
                Socket socket = server.accept();
                logger.info("Client connected from {}", socket.getRemoteSocketAddress());

                //one client = one ClientHandler thread
                ClientHandler handler = new ClientHandler(socket, sessions);
                handler.start();
            }
        } catch (IOException e) {
            logger.error("Notification server stopped (is port " + PORT + " already in use?)", e);
        }
    }
}
