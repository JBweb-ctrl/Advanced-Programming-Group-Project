package jm.edu.utech.helpdesk.controller;
import jm.edu.utech.helpdesk.exception.HelpDeskClientException;
import jm.edu.utech.helpdesk.exception.InvalidLoginException;
import jm.edu.utech.helpdesk.service.UserApi;
import jm.edu.utech.helpdesk.util.AlertUtil;
import jm.edu.utech.helpdesk.util.ErrorHandler;
import jm.edu.utech.helpdesk.util.SceneManager;
import jm.edu.utech.helpdesk.view.ChangePasswordView;

//handles the change password screen
public class ChangePasswordController {
	
	//screen the controller handles
	private final ChangePasswordView view;
	private final String username;
	
	public ChangePasswordController(ChangePasswordView view, String username) {
		this.view = view;
		this.username = username;
		attachHandlers();
	}
	//what each button does when clicked
	private void attachHandlers() {
		view.getChangeButton().setOnAction(e -> handleChange());
		view.getBackButton().setOnAction(e -> SceneManager.showDashboard(username));
		
	}
	private void handleChange() {
		String current = view.getCurrentField().getText();
		String newPassword = view.getNewField().getText();
		String confirm = view.getConfirmField().getText();
		
		//clear old error message
		view.getErrorLabel().setText("");
		//error handling
		 if (current.isEmpty() || newPassword.isEmpty() || confirm.isEmpty()) {
	            view.getErrorLabel().setText("Fill in all fields");
	            return;
	      }
	     if (newPassword.length() < 6) {
	            view.getErrorLabel().setText("New password is at least 6 characters");
	            return;
	      }
	     if (!newPassword.equals(confirm)) {
	            view.getErrorLabel().setText("Passwords do not match");
	            return;
	      }
	     if (newPassword.equals(current)) {
	            view.getErrorLabel().setText("New password must be different");
	            return;
	        }
	     /*placeholder until backend can check current password (old c0de)
	     AlertUtil.showInfo("Password", "Password change validated for: " + username);
	        SceneManager.showDashboard(username);
	        */
	     //the server checks the current password then saves the new one
	     try {
	    	 UserApi.changePassword(username, current, newPassword);
	    	 AlertUtil.showInfo("Password", "Password changed for: " + username);
	    	 SceneManager.showDashboard(username);
	     } catch (InvalidLoginException e) {
	    	 //wrong current password, shown on the screen like the other checks
	    	 view.getErrorLabel().setText("Current password is incorrect");
	     } catch (HelpDeskClientException e) {
	    	 ErrorHandler.handle(e);
	     }
	}
}
