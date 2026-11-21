package jm.edu.utech.helpdesk.controller;
import javafx.collections.FXCollections;
import jm.edu.utech.helpdesk.exception.HelpDeskClientException;
import jm.edu.utech.helpdesk.model.Technician;
import jm.edu.utech.helpdesk.service.TechnicianApi;
import jm.edu.utech.helpdesk.service.TicketApi;
import jm.edu.utech.helpdesk.util.AlertUtil;
import jm.edu.utech.helpdesk.util.ErrorHandler;
import jm.edu.utech.helpdesk.util.SceneManager;
import jm.edu.utech.helpdesk.view.ManageTechniciansView;
//controller for the manage techs screen, the technicians now come from the REST server
public class ManageTechniciansController {
	//screen the controller works with
	private final ManageTechniciansView view;
	private final String username;
	
	public ManageTechniciansController(ManageTechniciansView view, String username) {
		this.view = view;
		this.username = username;
		loadTechnicians();
		attachHandlers();
	}
	//fills the table with the technicians on the server
	private void loadTechnicians() {
		try {
			view.getTechnicianTable().setItems(FXCollections.observableArrayList(TechnicianApi.getTechnicians()));
		} catch (HelpDeskClientException e) {
			ErrorHandler.handle(e);
		}
	}
	private void attachHandlers() {
		view.getAddButton().setOnAction(e-> handleAdd());
		view.getRemoveButton().setOnAction(e-> handleRemove());
		view.getBackButton().setOnAction(e-> SceneManager.showAdminDashboard(username));
	}
	//add technician, the server says no if the username is already taken
	private void handleAdd() {
		String newUsername = view.getUsernameField().getText().trim();
		
		if (newUsername.isEmpty()) {
			AlertUtil.showError("Incomplete", "Enter a username");
			return;
		}
		try {
			Technician added = TechnicianApi.addTechnician(newUsername);
			view.getTechnicianTable().getItems().add(added);
			AlertUtil.showInfo("Technician Added", added.getUsername() + " can log in with password123");
			view.getUsernameField().clear();
		} catch (HelpDeskClientException e) {
			ErrorHandler.handle(e);
		}
	}
	//remove technician, can't remove one that still has tickets
	private void handleRemove() {
	        Technician selected = view.getTechnicianTable().getSelectionModel().getSelectedItem();

	        if (selected == null) {
	            AlertUtil.showWarning("No technician selected", "Select a technician");
	            return;
	        }
	        try {
	        	if (!TicketApi.getTicketsByTechnician(selected.getUsername()).isEmpty()) {
	        		AlertUtil.showWarning("Cannot remove", "This technician still has tickets assigned");
	        		return;
	        	}
	        	TechnicianApi.removeTechnician(selected.getUsername());
	        	view.getTechnicianTable().getItems().remove(selected);
	        } catch (HelpDeskClientException e) {
	        	ErrorHandler.handle(e);
	        }
	    }

}
