package jm.edu.utech.helpdesk.controller;

import jm.edu.utech.helpdesk.exception.HelpDeskClientException;
import jm.edu.utech.helpdesk.model.Comment;
import jm.edu.utech.helpdesk.model.Ticket;
import jm.edu.utech.helpdesk.service.TicketApi;
import jm.edu.utech.helpdesk.util.AlertUtil;
import jm.edu.utech.helpdesk.util.ErrorHandler;
import jm.edu.utech.helpdesk.util.SceneManager;
import jm.edu.utech.helpdesk.view.UserDashboardView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//controller that handles the user dashboard
public class UserDashboardController {
	//the screeen the controller needs
    private final UserDashboardView view;
    private final String username;
    //the users tickets
    private final ObservableList<Ticket> tickets;
    
    private Ticket editingTicket;
    //load the user tickets into the table 
    public UserDashboardController(UserDashboardView view, String username) {
        this.view = view;
        this.username = username;
        //the user's tickets now come from the REST server, if it fails the error alert shows and the table stays empty
        ObservableList<Ticket> loaded = FXCollections.observableArrayList();
        try {
            loaded.addAll(TicketApi.getTicketsByUser(username));
        } catch (HelpDeskClientException e) {
            ErrorHandler.handle(e);
        }
        this.tickets = loaded;
        view.getTicketTable().setItems(tickets);
        attachHandlers();
    }
  //what happens when each button is pressed
    private void attachHandlers() {
        view.getLogoutButton().setOnAction(e -> SceneManager.showLogin());
        view.getSubmitButton().setOnAction(e -> handleSubmit());
        view.getAddCommentButton().setOnAction(e -> handleAddComment());
        view.getTicketTable().getSelectionModel().selectedItemProperty().addListener((obs, old, ticket) -> showDetails(ticket));
        view.getEditButton().setOnAction(e -> handleEdit());
        view.getSaveButton().setOnAction(e -> handleSave());
        view.getCloseButton().setOnAction(e -> handleClose());
        view.getReopenButton().setOnAction(e -> handleReopen());
        view.getChangePasswordButton().setOnAction(e -> SceneManager.showChangePassword(username));
    }
//show selected ticket details
    private void showDetails(Ticket ticket) {
        if (ticket == null) {
            view.getDetailsArea().clear();
            view.getResolutionArea().clear();
            view.getCommentList().getItems().clear();
            return;
        }
        view.getDetailsArea().setText(ticket.getDescription() == null ? "" : ticket.getDescription());
        view.getResolutionArea().setText(ticket.getResolutionNotes() == null ? "" : ticket.getResolutionNotes());
        //comments now come from the REST server
        try {
            view.getCommentList().setItems(FXCollections.observableArrayList(TicketApi.getComments(ticket)));
        } catch (HelpDeskClientException e) {
            view.getCommentList().getItems().clear();
            ErrorHandler.handle(e);
        }
    }
//check the form and makes a new user ticket
    private void handleSubmit() {
        String subject = view.getSubjectField().getText().trim();
        String description = view.getDescriptionField().getText().trim();
        String priority = view.getPriorityBox().getValue();

        if (subject.isEmpty() || description.isEmpty() || priority == null) {
            AlertUtil.showError("Incomplete", "Please enter a subject, description and priority");
            return;
        }

        try {
            Ticket ticket = TicketApi.createTicket(subject, description, priority, username);
            tickets.add(ticket);
            editingTicket = null;
            view.getSubjectField().clear();
            view.getDescriptionField().clear();
            view.getPriorityBox().setValue(null);
            AlertUtil.showInfo("Ticket submitted", "Ticket " + ticket.getId() + " created");
        } catch (HelpDeskClientException e) {
            ErrorHandler.handle(e);
        }
    }
    //add a comment to the ticket
    private void handleAddComment() {
        Ticket ticket = view.getTicketTable().getSelectionModel().getSelectedItem();
        String text = view.getCommentField().getText().trim();

        if (ticket == null) {
            AlertUtil.showError("No Ticket selected", "Select a ticket");
            return;
        }
        if (text.isEmpty()) {
            AlertUtil.showError("Empty comment", "Write a comment");
            return;
        }

        //String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        try {
            Comment comment = TicketApi.addComment(ticket, username, text);
            view.getCommentList().getItems().add(comment);
            view.getCommentField().clear();
        } catch (HelpDeskClientException e) {
            ErrorHandler.handle(e);
        }
    }
    //edit a selected ticket, cannot edit a closed ticket
    private void handleEdit() {
    	Ticket ticket = view.getTicketTable().getSelectionModel().getSelectedItem();
    	if (ticket == null) {
    		AlertUtil.showError("No ticket slected", "Select a ticket");
    		return;
    	}
    	if ("CLOSED".equals(ticket.getStatus())) {
    		AlertUtil.showError("Ticket closed", "Cannot edit");
    		return;
    	}
    	editingTicket = ticket;
    	view.getSubjectField().setText(ticket.getSubject());
    	view.getPriorityBox().setValue(ticket.getPriority());
        view.getDescriptionField().setText(ticket.getDescription());
    }
    //save the details to the ticket being edited
    private void handleSave() {
    	if (editingTicket == null) {
    		AlertUtil.showError("Nothing to save", "Click edit selected first");
    		return;
    	}
    	String subject = view.getSubjectField().getText().trim();
    	String description= view.getDescriptionField().getText().trim();
    	String priority = view.getPriorityBox().getValue();
    	
    	if (subject.isEmpty() || description.isEmpty() || priority == null) {
    		AlertUtil.showError("Incomplete","Please enter a subject, description and priority");
    		return;
    	}
    	try {
    		Ticket saved = TicketApi.updateTicket(editingTicket, subject, description, priority);
    		//show what the server saved
    		editingTicket.setSubject(saved.getSubject());
    		editingTicket.setDescription(saved.getDescription());
    		editingTicket.setPriority(saved.getPriority());
    	} catch (HelpDeskClientException e) {
    		ErrorHandler.handle(e);
    		return;
    	}
    	//refresh the table everytime you update the subject
    	view.getTicketTable().refresh();
    	showDetails(editingTicket);
    	//empty the form
    	view.getSubjectField().clear();
        view.getDescriptionField().clear();
        view.getPriorityBox().setValue(null);
        AlertUtil.showInfo("Ticket updated", "Ticket " + editingTicket.getId() + " updated.");
        editingTicket = null;
    	
    }
    //close the ticket
    private void handleClose() {
        Ticket ticket = view.getTicketTable().getSelectionModel().getSelectedItem();

        if (ticket == null) {
            AlertUtil.showError("No Ticket selected", "Select a ticket");
            return;
        }
        if ("CLOSED".equals(ticket.getStatus())) {
            AlertUtil.showError("Already closed", "Ticket is already closed");
            return;
        }

        try {
            ticket.setStatus(TicketApi.updateStatus(ticket, "CLOSED").getStatus());
        } catch (HelpDeskClientException e) {
            ErrorHandler.handle(e);
            return;
        }
        view.getTicketTable().refresh();
        AlertUtil.showInfo("Ticket closed", "Ticket " + ticket.getId() + " closed");
    }
    //reopen a ticket
    private void handleReopen() {
        Ticket ticket = view.getTicketTable().getSelectionModel().getSelectedItem();

        if (ticket == null) {
            AlertUtil.showError("No Ticket selected", "Select a ticket");
            return;
        }
        if (!"CLOSED".equals(ticket.getStatus()) && !"RESOLVED".equals(ticket.getStatus())) {
            AlertUtil.showError("Cannot reopen", "Only resolved or closed tickets can be reopened");
            return;
        }

        try {
            ticket.setStatus(TicketApi.updateStatus(ticket, "REOPENED").getStatus());
        } catch (HelpDeskClientException e) {
            ErrorHandler.handle(e);
            return;
        }
        view.getTicketTable().refresh();
        AlertUtil.showInfo("Ticket reopened", "Ticket " + ticket.getId() + " reopened");
}
}
