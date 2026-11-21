package jm.edu.utech.helpdesk.view;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
//the change password screen
public class ChangePasswordView {

	private final VBox root = new VBox(20);
	private final PasswordField currentField = new PasswordField();
	private final PasswordField newField = new PasswordField();
	private final PasswordField confirmField = new PasswordField();
	private final Button changeButton = new Button("Change Password");
	private final Button backButton = new Button("Go Back");
	
	private final Label errorLabel = new Label();
	
	public ChangePasswordView(){
		Label title = new Label("Change Password");
		title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
		
		currentField.setPromptText("Enter current password");
		newField.setPromptText("Enter new password");
		confirmField.setPromptText("Confirm new password");
		
		GridPane form = new GridPane();
		form.setHgap(10);
		  form.setVgap(10);
	      form.setAlignment(Pos.CENTER);
	      form.add(new Label("Current:"), 0, 0);
	      form.add(currentField, 1, 0);
	      form.add(new Label("New:"), 0, 1);
	      form.add(newField, 1, 1);
	      form.add(new Label("Confirm:"), 0, 2);
	      form.add(confirmField, 1, 2);
	      
	      HBox buttonBox = new HBox(10, changeButton, backButton);
	      buttonBox.setAlignment(Pos.CENTER);
	      
	      errorLabel.setStyle("-fx-text-fill: red;");
	      
	      root.setAlignment(Pos.CENTER);
	      root.setPadding(new Insets(20));
	      root.getChildren().addAll(title, form, buttonBox, errorLabel);
	}
	//getters

	public VBox getRoot() {
		return root;
	}

	public PasswordField getCurrentField() {
		return currentField;
	}

	public PasswordField getNewField() {
		return newField;
	}

	public PasswordField getConfirmField() {
		return confirmField;
	}

	public Button getChangeButton() {
		return changeButton;
	}

	public Button getBackButton() {
		return backButton;
	}

	public Label getErrorLabel() {
		return errorLabel;
	}
	
}
