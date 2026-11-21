package jm.edu.utech.helpdesk.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import jm.edu.utech.helpdesk.notification.model.NotificationMessage;

//shows a notification as a popup without blocking the screen the user is on
//show() instead of showAndWait() so more notifications can arrive without freezing anything
public final class NotificationPopup {

    private NotificationPopup() {}

    //called from the listener thread, Platform.runLater moves the popup onto the JavaFX thread
    public static void show(NotificationMessage message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Notification");
            alert.setHeaderText(message.getType().getLabel());
            alert.setContentText(message.getMessage());
            alert.show();
        });
    }
}
