package jm.edu.utech.helpdesk.notification.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

//console client for testing the server without the JavaFX app or REST
//run with arguments: listen student1 (waits for student1's notifications)
//or: publish TICKET_ASSIGNED 1 student1 "message" (sends one, use * to send to everyone)
public class NotificationTestClient {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: listen <username>  |  publish <TYPE> <ticketId> <target|*> <message>");
            return;
        }
        String host = System.getProperty("notification.host", "localhost");
        int port = Integer.getInteger("notification.port", 9090);

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

            if (args[0].equalsIgnoreCase("listen")) {
                out.print("REGISTER\t" + args[1] + "\n");
                out.flush();
                System.out.println("Registered as " + args[1] + ". Waiting for notifications (Ctrl+C to stop)...");
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("<< " + line.replace('\t', ' '));
                }
            } else if (args[0].equalsIgnoreCase("publish") && args.length >= 5) {
                out.print(String.join("\t", "PUBLISH", args[1], args[2], args[3], args[4]) + "\n");
                out.flush();
                System.out.println("Server replied: " + in.readLine());
            } else {
                System.out.println("Unknown mode or missing arguments.");
            }
        }
    }
}
