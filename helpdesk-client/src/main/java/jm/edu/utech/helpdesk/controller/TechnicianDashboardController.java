package jm.edu.utech.helpdesk.controller;

import jm.edu.utech.helpdesk.exception.HelpDeskClientException;
import jm.edu.utech.helpdesk.model.Ticket;
import jm.edu.utech.helpdesk.service.TicketApi;
import jm.edu.utech.helpdesk.service.TicketRefreshService;
import jm.edu.utech.helpdesk.util.AlertUtil;
import jm.edu.utech.helpdesk.util.ErrorHandler;
import jm.edu.utech.helpdesk.util.SceneManager;
import jm.edu.utech.helpdesk.view.TechnicianDashboardView;
import javafx.collections.FXCollections;
//handles tech dashboard
public class TechnicianDashboardController {

	 private final TechnicianDashboardView view;
	 private final String username;
	 //reloads this technician's tickets in the background without freezing the screen
	 private TicketRefreshService refresher;
//put tech tickets into table 
 public TechnicianDashboardController(TechnicianDashboardView view, String username) {
	        this.view = view;
	        this.username = username;
	        loadAssignedTickets();
	        attachHandlers();
	        startAutoRefresh();
	    }
//old code for the samples now in the sql
 /*private void loadSampleTickets() {
 view.getTicketTable().setItems(FXCollections.observableArrayList(
         new Ticket(1L, "Wi-Fi down", "HIGH", "ASSIGNED", "2026-10-02"),
         new Ticket(2L, "Projector not working", "MEDIUM", "ASSIGNED", "2026-10-03"),
         new Ticket(3L, "Cannot reset password", "LOW", "IN_PROGRESS", "2026-10-04")
 ));
}*/
	    //fills the table with only assigned tickets
	 private void loadAssignedTickets() {
		 //now comes from the REST server, if it fails the error alert shows and the table stays empty
		 try {
			 view.getTicketTable().setItems(FXCollections.observableArrayList(
					 TicketApi.getTicketsByTechnician(username)));
		 } catch (HelpDeskClientException e) {
			 ErrorHandler.handle(e);
		 }
	    }
	 //whats happens when each button is pressed
private void attachHandlers() {
	        //logout and change password stop the auto refresh first, they are the only ways off this screen
	        view.getLogoutButton().setOnAction(e -> {
	            refresher.stop();
	            SceneManager.showLogin();
	        });
	        view.getTicketTable().getSelectionModel().selectedItemProperty().addListener((obs, old, ticket) -> {
	            if (ticket != null) {
	        view.getStatusBox().setValue(ticket.getStatus());
	        view.getNotesArea().setText(ticket.getResolutionNotes() == null ? "" : ticket.getResolutionNotes());
	        view.getDescriptionArea().setText(ticket.getDescription() == null ? "" : ticket.getDescription());
	            }
	        });

	        view.getUpdateStatusButton().setOnAction(e -> handleUpdateStatus());
	        view.getResolveButton().setOnAction(e -> handleResolve());
	        view.getChangePasswordButton().setOnAction(e -> {
	            refresher.stop();
	            SceneManager.showChangePassword(username);
	        });
	    }
//checks for new or changed tickets every 10 seconds on a background thread
	 private void startAutoRefresh() {
		 refresher = new TicketRefreshService(view.getTicketTable(), username, 10);
		 refresher.start();
	 }
//changes the selected ticket to the status chosen
	  private void handleUpdateStatus() {
	        Ticket ticket = view.getTicketTable().getSelectionModel().getSelectedItem();
	        String status = view.getStatusBox().getValue();

	        if (ticket == null) {
	            AlertUtil.showWarning("No ticket selected", "Select a ticket");
	            return;
	        }
	        if (status == null) {
	            AlertUtil.showWarning("No status selected", "Choose a status");
	            return;
	        }
	        try {
	            Ticket updated = TicketApi.updateStatus(ticket, status);
	            ticket.setStatus(updated.getStatus());   //show what the server saved
	            view.getTicketTable().refresh();
	        } catch (HelpDeskClientException e) {
	            ErrorHandler.handle(e);
	        }
	    }
//saves notes and marks ticket as resolved
private void handleResolve() {
	        Ticket ticket = view.getTicketTable().getSelectionModel().getSelectedItem();
	        String notes = view.getNotesArea().getText().trim();

	        if (ticket == null) {
	            AlertUtil.showWarning("No Ticket selected", "Select a ticket");
	            return;
	        }
	        if (notes.isEmpty()) {
	            AlertUtil.showWarning("Missing notes", "Add notes");
	            return;
	        }
	        try {
	            Ticket resolved = TicketApi.resolveTicket(ticket, notes);
	            ticket.setStatus(resolved.getStatus());
	            ticket.setResolutionNotes(resolved.getResolutionNotes());
	            view.getStatusBox().setValue(resolved.getStatus());
	            view.getTicketTable().refresh();
	            AlertUtil.showInfo("Resolved", "Ticket " + ticket.getId() + " resolved.");
	        } catch (HelpDeskClientException e) {
	            ErrorHandler.handle(e);
	        }
	    }
}