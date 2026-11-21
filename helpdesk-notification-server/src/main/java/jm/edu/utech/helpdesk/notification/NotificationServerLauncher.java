package jm.edu.utech.helpdesk.notification;

import jm.edu.utech.helpdesk.notification.server.NotificationServer;

//starts the notification server (run this class)
public class NotificationServerLauncher {

    public static void main(String[] args) {
        NotificationServer server = new NotificationServer();
        server.start();
    }
}
