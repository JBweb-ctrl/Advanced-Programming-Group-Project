package jm.edu.utech.helpdesk.util;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

//used to show pop up messages to the user
public class AlertUtil {

	//error popup (take 2 arguments so it shows what the error is in the top left og the popup then what to do)
	public static void showError(String title, String message) {
        show(AlertType.ERROR, title, message);
    }
	//info popup
    public static void showInfo(String title, String message) {
        show(AlertType.INFORMATION, title, message);
    }
    //warning popup
	public static void showWarning(String title, String message) {
		show(AlertType.WARNING, title, message);
	}
    
    //building the alert
    private static void show(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
