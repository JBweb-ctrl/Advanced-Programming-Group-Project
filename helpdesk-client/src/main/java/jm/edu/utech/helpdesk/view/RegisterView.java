package jm.edu.utech.helpdesk.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
//screen to create account with username and password
public class RegisterView {
	//vbox put everything vertically
    private final VBox root = new VBox(20);
    //user inputs
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField confirmField = new PasswordField();
    private final Button registerButton = new Button("Create Account");
    private final Button backButton = new Button("Back to Login");
    private final Label errorLabel = new Label();
//red error when register details are not valid
    public RegisterView() {
    	//the heading on the register page
        Label title = new Label("Create Account");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        //prompt user what to enter
        usernameField.setPromptText("Enter username");
        passwordField.setPromptText("Enter password");
        confirmField.setPromptText("Confirm password");
        //grid to keep the labels and fields lined up
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setAlignment(Pos.CENTER);
        form.add(new Label("Username:"), 0, 0);
        form.add(usernameField, 1, 0);
        form.add(new Label("Password:"), 0, 1);
        form.add(passwordField, 1, 1);
        form.add(new Label("Confirm:"), 0, 2);
        form.add(confirmField, 1, 2);
        //puts the buttons together side by side
        HBox buttonBox = new HBox(10, registerButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);
        //red error
        errorLabel.setStyle("-fx-text-fill: red;");
        //align the main layout
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(title, form, buttonBox, errorLabel);
    }
    //getters for the controllers
	public VBox getRoot() {
		return root;
	}

	public TextField getUsernameField() {
		return usernameField;
	}

	public PasswordField getPasswordField() {
		return passwordField;
	}

	public PasswordField getConfirmField() {
		return confirmField;
	}

	public Button getRegisterButton() {
		return registerButton;
	}

	public Button getBackButton() {
		return backButton;
	}

	public Label getErrorLabel() {
		return errorLabel;
	}
    
}