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
//builds the login screen
public class LoginView {
	//vbox puts everything vertically
    private final VBox root = new VBox(20);
    //user inputs buttons and text fields
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final Button loginButton = new Button("Login");
    private final Button registerButton = new Button("Register");
    private final Label errorLabel = new Label();
  //shows a error when login details are wrong/invalid
    public LoginView() {
    	//heading/title on the login page
        Label title = new Label("University Help Desk");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        //shows what to do inside empty fields
        usernameField.setPromptText("Enter username");
        passwordField.setPromptText("Enter password");
      //grid to keep the labelsand fields lined up
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setAlignment(Pos.CENTER);
        form.add(new Label("Username:"), 0, 0);
        form.add(usernameField, 1, 0);
        form.add(new Label("Password:"), 0, 1);
        form.add(passwordField, 1, 1);
       //login and register button beside each other
        HBox buttonBox = new HBox(10, loginButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);
//makes the error red
        errorLabel.setStyle("-fx-text-fill: red;");
        //aligns the main layout
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(title, form, buttonBox, errorLabel);
    }
//getters the controllers will use
	public VBox getRoot() {
		return root;
	}

	public TextField getUsernameField() {
		return usernameField;
	}

	public PasswordField getPasswordField() {
		return passwordField;
	}

	public Button getLoginButton() {
		return loginButton;
	}

	public Button getRegisterButton() {
		return registerButton;
	}

	public Label getErrorLabel() {
		return errorLabel;
	}


	

	
}
