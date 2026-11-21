package jm.edu.utech.helpdesk.controller;

import jm.edu.utech.helpdesk.exception.HelpDeskClientException;
import jm.edu.utech.helpdesk.model.User;
import jm.edu.utech.helpdesk.service.UserApi;
import jm.edu.utech.helpdesk.util.ErrorHandler;
import jm.edu.utech.helpdesk.util.SceneManager;
import jm.edu.utech.helpdesk.view.LoginView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//handles login screen checks the fields then send the user to the right place
public class LoginController {
	//login screen the controller needs
	private final LoginView view;
	private static final Logger logger = LogManager.getLogger(LoginController.class);
	public LoginController (LoginView view) {
	this.view = view;
	attachHandlers();
	}
	//whats happens when each button is pressed
	private void attachHandlers() {
		view.getLoginButton().setOnAction( e -> handleLogin());
		view.getPasswordField().setOnAction(e -> handleLogin());
		//view.getRegisterButton().setOnAction( e->  AlertUtil.showInfo("Login", "Register"));
		view.getRegisterButton().setOnAction(e -> SceneManager.showRegister());
	}
	//reads the info entered in the view
	private void handleLogin() {
		String username = view.getUsernameField().getText().trim();
		String password = view.getPasswordField().getText();
		view.getErrorLabel().setText("");
		//checks that both fields are filled in
		if (username.isEmpty() || password.isEmpty()) {
			view.getErrorLabel().setText("Please enter username and password");
			return;
		}
		//the server checks the username and password and says which role they have
		//wrong details the ErrorHandler shows "Invalid Login Credentials"
		try {
			User account = UserApi.login(username, password);
			//old code
			/*AlertUtil.showInfo("Login", "Validation passed for: " + username);
			/SceneManager.showUserDashboard(username);
			Temporary
			if (username.toLowerCase().startsWith("tech")) {
			    SceneManager.showTechnicianDashboard(username);
			}else if (username.equalsIgnoreCase("admin")) {
				SceneManager.showAdminDashboard(username);
			} else {
			    SceneManager.showUserDashboard(username);
			}*/
			//scenemanager shows the dashboard based on the role of the user that logs in
			SceneManager.loginAs(account.getUsername(), account.getRole());
		} catch (HelpDeskClientException e) {
			logger.warn("Login failed for {}: {}", username, e.getMessage());
			view.getPasswordField().clear();
			ErrorHandler.handle(e);
		}
	}
}
