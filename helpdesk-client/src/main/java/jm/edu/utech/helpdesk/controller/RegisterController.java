package jm.edu.utech.helpdesk.controller;

import jm.edu.utech.helpdesk.exception.HelpDeskClientException;
import jm.edu.utech.helpdesk.service.UserApi;
import jm.edu.utech.helpdesk.util.AlertUtil;
import jm.edu.utech.helpdesk.util.ErrorHandler;
import jm.edu.utech.helpdesk.util.SceneManager;
import jm.edu.utech.helpdesk.view.RegisterView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//handles the create account screen
public class RegisterController {
//the register screen the controller needs
	private final RegisterView view;
	private static final Logger logger = LogManager.getLogger(RegisterController.class);
    public RegisterController(RegisterView view) {
        this.view = view;
        attachHandlers();
    }
  //whats happens when each button is pressed
    private void attachHandlers() {
    	//button to register and go to the register screen
        view.getRegisterButton().setOnAction(e -> handleRegister());
        //back button goes back to the login screen
        view.getBackButton().setOnAction(e -> SceneManager.showLogin());
    }
  //reads the info entered in the view and show error if something is wrong
    private void handleRegister() {
        String username = view.getUsernameField().getText().trim();
        String password = view.getPasswordField().getText();
        String confirm = view.getConfirmField().getText();
        view.getErrorLabel().setText("");
//checks for any errors and shows error message
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            view.getErrorLabel().setText("Fill in all fields");
            return;
        }
        if (password.length() < 6) {
            view.getErrorLabel().setText("Password should be least 6 characters");
            return;
        }
        if (!password.equals(confirm)) {
            view.getErrorLabel().setText("Passwords do not match");
            return;
        }
        //the server creates the account, username already taken -> ErrorHandler shows "Duplicate User Account"
        try {
        	UserApi.register(username, password);
        	AlertUtil.showInfo("Register", "Account created for: " + username);
        	SceneManager.showLogin();
        } catch (HelpDeskClientException e) {
        	logger.warn("Registration failed for {}: {}", username, e.getMessage());
        	ErrorHandler.handle(e);
        }
   /*Technician name is already taken (old code)
       
        if (TechnicianService.getInstance().exists(username) || !UserService.getInstance().addUser(username, "Student/Staff")) {
        	logger.warn("Duplicate user account attempt: {}", username);
        	AlertUtil.showError("Duplicate User Account","That username already exists" );
        	return;
        }
        logger.info("User registered: {}", username);
        AlertUtil.showInfo("Register", "Account created for: " + username);
        SceneManager.showLogin();

        // Placeholder until the backend registration endpoint exists
        AlertUtil.showInfo("Register", "Account validated for: " + username);
        SceneManager.showLogin();
        */
    }
}
