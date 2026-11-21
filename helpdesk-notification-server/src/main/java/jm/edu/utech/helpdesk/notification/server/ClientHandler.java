package jm.edu.utech.helpdesk.notification.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jm.edu.utech.helpdesk.notification.model.NotificationMessage;
import jm.edu.utech.helpdesk.notification.model.Request;
import jm.edu.utech.helpdesk.notification.model.Response;

//handles one connection
//REGISTER: a JavaFX client that stays connected and receives notifications
//PUBLISH: the REST service sending one notification, then the connection closes
public class ClientHandler extends Thread {

    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    private final Socket socket;
    private final SessionManager sessions;
    private ObjectOutputStream output;
    private String username;

    public ClientHandler(Socket socket, SessionManager sessions) {
        this.socket = socket;
        this.sessions = sessions;
    }

    @Override
    public void run() {
        try {
            //output first then input, if both sides open the input first they wait on each other forever
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            //receive the request
            Request request = (Request) input.readObject();

            if ("REGISTER".equals(request.getCommand())) {
                username = request.getUsername();
                sessions.register(username, this);
                send(new Response(true, "Registered"));

                //keep reading so we find out when the client disconnects (readObject throws then)
                while (true) {
                    input.readObject();
                }
            } else if ("PUBLISH".equals(request.getCommand())) {
                NotificationMessage message = request.getMessage();
                int delivered = sessions.deliver(message);
                logger.info("Published {} -> delivered to {} client(s)", message, delivered);
                send(new Response(true, "Delivered to " + delivered + " client(s)"));
            } else {
                logger.warn("Unknown command: {}", request.getCommand());
                send(new Response(false, "Unknown command"));
            }
        } catch (EOFException | SocketException e) {
            //normal: the client closed the connection
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Problem with connection from {}", socket.getRemoteSocketAddress(), e);
        } finally {
            sessions.remove(username, this);
            close();
            logger.info("Client disconnected: {} ({})", socket.getRemoteSocketAddress(),
                    username == null ? "REST service" : username);
        }
    }

    //sends one object to this client, synchronized so two notifications can't be written at the same time
    public synchronized boolean send(Object object) {
        try {
            output.writeObject(object);
            output.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    //closes the connection
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            logger.debug("Error closing socket", e);
        }
    }
}
