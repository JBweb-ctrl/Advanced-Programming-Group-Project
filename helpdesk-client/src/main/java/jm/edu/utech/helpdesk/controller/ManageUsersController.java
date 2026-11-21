package jm.edu.utech.helpdesk.controller;

import javafx.collections.FXCollections;
import jm.edu.utech.helpdesk.exception.HelpDeskClientException;
import jm.edu.utech.helpdesk.model.User;
import jm.edu.utech.helpdesk.service.TicketApi;
import jm.edu.utech.helpdesk.service.UserApi;
import jm.edu.utech.helpdesk.util.AlertUtil;
import jm.edu.utech.helpdesk.util.ErrorHandler;
import jm.edu.utech.helpdesk.util.SceneManager;
import jm.edu.utech.helpdesk.view.ManageUsersView;
//handles the manage users screen for admin the users come from the REST server
public class ManageUsersController {
	private final ManageUsersView view;
	private final String username;
	
	public ManageUsersController(ManageUsersView view, String username) {
        this.view = view;
        this.username = username;
        loadUsers();
        attachHandlers();
	} 
	//fills the table with the users on the server
	private void loadUsers() {
		try {
			view.getUserTable().setItems(FXCollections.observableArrayList(UserApi.getUsers()));
		} catch (HelpDeskClientException e) {
			ErrorHandler.handle(e);
		}
	}
	//what happens when each button is clicked
	private void attachHandlers() {
		view.getAddButton().setOnAction(e -> handleAdd());
        view.getRemoveButton().setOnAction(e -> handleRemove());
        view.getBackButton().setOnAction(e -> SceneManager.showAdminDashboard(username));
	}
	//admin adds a user, the server gives them the default password and says no if the username is taken
	private void handleAdd() {
        String newUsername = view.getUsernameField().getText().trim();
        String role = view.getRoleBox().getValue();
        if (newUsername.isEmpty() || role == null) {
            AlertUtil.showError("Incomplete", "Enter username and choose a role");
            return;
        }
        try {
        	User added = UserApi.addUser(newUsername, role);
        	view.getUserTable().getItems().add(added);
        	AlertUtil.showInfo("User Added", added.getUsername() + "can log in with password123 and change it after");
        	//empty the form so it is ready for the next user.
        	view.getUsernameField().clear();
        	view.getRoleBox().setValue(null);
        } catch (HelpDeskClientException e) {
        	ErrorHandler.handle(e);
        }
    }
    // admin can remove a selected user, not themselves and not someone who still has tickets
    private void handleRemove() {
        User selected = view.getUserTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No user selected", "Select a user");
            return;
        }
        if (selected.getUsername().equalsIgnoreCase(username)) {
            AlertUtil.showWarning("Cannot remove", "You cannot remove your own account");
            return;
        }
        try {
        	if (!TicketApi.getTicketsByUser(selected.getUsername()).isEmpty()) {
        		AlertUtil.showWarning("Cannot remove", "This user still has tickets");
        		return;
        	}
        	UserApi.removeUser(selected.getUsername());
        	view.getUserTable().getItems().remove(selected);
        } catch (HelpDeskClientException e) {
        	ErrorHandler.handle(e);
        }
    }
}
