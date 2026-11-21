package jm.edu.utech.helpdesk.controller;

import jm.edu.utech.helpdesk.exception.HelpDeskClientException;
import jm.edu.utech.helpdesk.model.Ticket;
import jm.edu.utech.helpdesk.service.TicketApi;
import jm.edu.utech.helpdesk.service.TechnicianApi;
import jm.edu.utech.helpdesk.util.AlertUtil;
import jm.edu.utech.helpdesk.util.ErrorHandler;
import jm.edu.utech.helpdesk.util.SceneManager;
import jm.edu.utech.helpdesk.view.AdminDashboardView;
import javafx.collections.FXCollections;
import java.time.LocalDate;
import java.util.List;
//handles the admin dashboard, lets admin assign tickets to techs
public class AdminDashboardController {
	//screen thee controller needs
    private final AdminDashboardView view;
    private final String username;  
    //loads every
    public AdminDashboardController(AdminDashboardView view, String username) {
        this.view = view;
        this.username = username;
        loadAllTickets();
        loadTechnicians();
        attachHandlers();
    }
    //fills the assign and search dropdowns with the technicians on the server
    private void loadTechnicians() {
        try {
            List<String> names = TechnicianApi.getTechnicianNames();
            view.getTechnicianBox().getItems().setAll(names);
            view.getSearchTechnician().getItems().setAll(names);
        } catch (HelpDeskClientException e) {
            ErrorHandler.handle(e);
        }
    }
    //gets all the tickets in the table
    private void loadAllTickets() {
        //every ticket now comes from the REST server
        try {
            view.getTicketTable().setItems(FXCollections.observableArrayList(TicketApi.getAllTickets()));
        } catch (HelpDeskClientException e) {
            ErrorHandler.handle(e);
        }
    }
  //whats happens when each button is pressed
    private void attachHandlers() {
        view.getLogoutButton().setOnAction(e -> SceneManager.showLogin());
        view.getAssignButton().setOnAction(e -> handleAssign());
        //button to change password
        view.getChangePasswordButton().setOnAction(e -> SceneManager.showChangePassword(username));
        //button to manage a user
        view.getManageUsersButton().setOnAction(e -> SceneManager.showManageUsers(username));
        //button to manage a tech
        view.getManageTechsButton().setOnAction(e -> SceneManager.showManageTechnicians(username));
        //button to show a report
        view.getReportsButton().setOnAction(e -> SceneManager.showReports(username));
        //show selected ticket description
        view.getTicketTable().getSelectionModel().selectedItemProperty().addListener((obs, old, ticket) -> {
            if (ticket == null) {
                view.getDescriptionArea().clear();
            } else {
                view.getDescriptionArea().setText(
                        ticket.getDescription() == null ? "" : ticket.getDescription());
            }
        });
        //button that actually does searching
        view.getSearchButton().setOnAction(e -> handleSearch());
        //button that empties the fields
        view.getClearButton().setOnAction(e -> handleClear());
        
    }
//assign tickets to techs and handle errors
    private void handleAssign() {
        Ticket ticket = view.getTicketTable().getSelectionModel().getSelectedItem();
        String technician = view.getTechnicianBox().getValue();

        if (ticket == null) {
            AlertUtil.showError("No ticket selected", "Select a ticket");
            return;
        }
        if (technician == null) {
            AlertUtil.showError("No technician selected", "Choose a tech");
            return;
        }

        try {
            Ticket assigned = TicketApi.assignTicket(ticket, technician);
            //show what the server saved
            ticket.setAssignedTechnician(assigned.getAssignedTechnician());
            ticket.setStatus(assigned.getStatus());
            view.getTicketTable().refresh();
            AlertUtil.showInfo("Ticket Assigned", "Ticket " + ticket.getId() + " assigned to " + technician);
        } catch (HelpDeskClientException e) {
            //already assigned by someone else, ticket gone, server off ErrorHandler shows the right alert
            ErrorHandler.handle(e);
            loadAllTickets();
        }
    }
  //looks for matching tickets in the search 
    private void handleSearch() {
        String id = view.getSearchId().getText().trim();
        String user = view.getSearchUser().getText().trim();
        String technician = valueOrEmpty(view.getSearchTechnician().getValue());
        String status = valueOrEmpty(view.getSearchStatus().getValue());
        String priority = valueOrEmpty(view.getSearchPriority().getValue());
        LocalDate from = view.getFromDate().getValue();
        LocalDate to = view.getToDate().getValue();

    if (!id.isEmpty()) {
        try {
            Integer.parseInt(id);
        } catch (NumberFormatException exception) {
            AlertUtil.showError("Invalid", "Ticket ID must be a whole number");
            return;
        }
    }
    if (from != null && to != null && from.isAfter(to)) {
        AlertUtil.showError("Invalid dates", "The From date cannot be after the To date");
        return;
    }
    try {
        List<Ticket> results = TicketApi.searchTickets(id, user, technician, status, priority, from == null ? "" : from.toString(), to == null ? "" : to.toString());
        view.getTicketTable().setItems(FXCollections.observableArrayList(results));
    } catch (HelpDeskClientException e) {
        ErrorHandler.handle(e);
    }
}
    //empties the search boxes if you had a specific filter set up
    private void handleClear() {
        view.getSearchId().clear();
        view.getSearchUser().clear();
        view.getSearchTechnician().setValue(null);
        view.getSearchStatus().setValue(null);
        view.getSearchPriority().setValue(null);
        view.getFromDate().setValue(null);
        view.getToDate().setValue(null);
        loadAllTickets();
    }
    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}