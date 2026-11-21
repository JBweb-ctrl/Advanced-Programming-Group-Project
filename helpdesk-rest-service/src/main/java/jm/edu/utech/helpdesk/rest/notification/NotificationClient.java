package jm.edu.utech.helpdesk.rest.notification;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.springframework.stereotype.Component;

import jm.edu.utech.helpdesk.notification.model.NotificationMessage;
import jm.edu.utech.helpdesk.notification.model.Request;
import jm.edu.utech.helpdesk.notification.model.Response;
import jm.edu.utech.helpdesk.rest.exception.NotificationException;

//sends notifications from the REST service to the notification server
//connect, send a Request, read the Response, close
@Component
public class NotificationClient {

    private static final String HOST = "localhost";
    private static final int PORT = 9090;

    //sends one notification, throws NotificationException if the server can't be reached or says no
    public void publish(NotificationMessage message) {
        try (Socket socket = new Socket(HOST, PORT)) {
            //output first then input, if both sides open the input first they wait on each other forever
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            output.writeObject(new Request("PUBLISH", null, message));
            output.flush();

            Response response = (Response) input.readObject();
            if (!response.isSuccess()) {
                throw new NotificationException("Notification server rejected the message: " + response.getMessage());
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new NotificationException("Unable to reach the notification server at " + HOST + ":" + PORT, e);
        }
    }
}
