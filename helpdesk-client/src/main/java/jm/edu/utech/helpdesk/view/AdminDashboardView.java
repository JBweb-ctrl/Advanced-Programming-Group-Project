package jm.edu.utech.helpdesk.view;

import jm.edu.utech.helpdesk.model.Ticket;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
//admin dashboard, where admin can see tickets and assignss to technicians
public class AdminDashboardView {
//vertical layout
    private final VBox root = new VBox(15);
    private final Button logoutButton = new Button("Logout");
    private final Button changePasswordButton = new Button("Change Password");
    //table that lists every ticket
    private final TableView<Ticket> ticketTable = new TableView<>();
    //inputs to assign tickets
    private final ComboBox<String> technicianBox = new ComboBox<>();
    private final Button assignButton = new Button("Assign Ticket");
    private final Button manageUsersButton = new Button("Manage Users");
    private final Button manageTechsButton = new Button("Manage Technicians");
    //button to see reports
    private final Button reportsButton = new Button("Reports");
    //area to see description
    private final TextArea descriptionArea = new TextArea();
    
    //search tickets controls
    private final TextField searchId = new TextField();
    private final TextField searchUser = new TextField();
    private final ComboBox<String> searchTechnician = new ComboBox<>();
    private final ComboBox<String> searchStatus = new ComboBox<>();
    private final ComboBox<String> searchPriority = new ComboBox<>();
    private final DatePicker fromDate= new DatePicker();
    private final DatePicker toDate = new DatePicker();
    private final Button searchButton = new Button("Search");
    private final Button clearButton = new Button("Clear");
    
//controls for admin
    public AdminDashboardView(String username) {
    	//heading and logout and other buttons in the top
        Label title = new Label("Administrator Dashboard - " + username);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox topRow = new HBox(20, title, changePasswordButton, logoutButton);
        HBox adminButtons = new HBox(10, manageUsersButton, manageTechsButton, reportsButton);
        
        createTable();
        //list of tecnicians to choose from
        technicianBox.getItems().addAll("tech", "tech2"); // placeholder list (hardoded techs)
        technicianBox.setPromptText("Select technician");

        HBox assignRow = new HBox(10, new Label("Assign selected ticket to "), technicianBox, assignButton);
       //description of ticket to admin
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setPromptText("Select a ticket to see its description");
        
        //search functionalities
        searchStatus.getItems().addAll("NEW", "ASSIGNED", "IN_PROGRESS", "PENDING", "RESOLVED", "CLOSED", "REOPENED");
        searchPriority.getItems().addAll("LOW", "MEDIUM", "HIGH");
        //any is the empty/default value
        searchTechnician.setPromptText("Any");
        searchStatus.setPromptText("Any");
        searchPriority.setPromptText("Any");
        searchId.setPromptText("Any");
        searchUser.setPromptText("Any");
        
        //puts all the search/filter related stuff in a grid so like your ticket id or your user or whatever your searching for neat on the admin screen
        GridPane searchForm = new GridPane();
        searchForm.setHgap(10);
        searchForm.setVgap(10);
        
        searchForm.add(new Label("Ticket Id:"), 0, 0);
        searchForm.add(searchId, 1, 0);
        
        searchForm.add(new Label("User:"), 2, 0);
        searchForm.add(searchUser, 3, 0);
        
        searchForm.add(new Label("Technician:"), 4, 0);
        searchForm.add(searchTechnician, 5, 0);
        
        searchForm.add(new Label("Status:"), 0, 1);
        searchForm.add(searchStatus, 1, 1);
        
        searchForm.add(new Label("Priority:"), 2, 1);
        searchForm.add(searchPriority, 3, 1);
        
        searchForm.add(new Label("From:"), 0, 2);
        searchForm.add(fromDate, 1, 2);
        
        searchForm.add(new Label("To:"), 2, 2);
        searchForm.add(toDate, 3, 2);
        
        searchForm.add(new HBox(10, searchButton, clearButton), 5, 2);

        Label searchHeading = new Label("Search tickets");
        searchHeading.setStyle("-fx-font-weight: bold;");
        
        root.setPadding(new Insets(20));
        root.getChildren().addAll(topRow, adminButtons, searchHeading, searchForm , ticketTable, new Label("Description:"), descriptionArea, assignRow);
    }
 

	//makes the table columns and each one shows part of a ticket
    @SuppressWarnings("unchecked")
    private void createTable() {

        TableColumn<Ticket, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(ticket -> new SimpleLongProperty(ticket.getValue().getId()));

        TableColumn<Ticket, String> subjectColumn = new TableColumn<>("Subject");
        subjectColumn.setCellValueFactory(ticket -> new SimpleStringProperty(ticket.getValue().getSubject()));

        TableColumn<Ticket, String> priorityColumn = new TableColumn<>("Priority");
        priorityColumn.setCellValueFactory(ticket -> new SimpleStringProperty(ticket.getValue().getPriority()));

        TableColumn<Ticket, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(ticket -> new SimpleStringProperty(ticket.getValue().getStatus()));

        TableColumn<Ticket, String> submittedColumn = new TableColumn<>("Submitted By");
        submittedColumn.setCellValueFactory(ticket -> new SimpleStringProperty(ticket.getValue().getSubmittedBy()));

        TableColumn<Ticket, String> technicianColumn = new TableColumn<>("Technician");
        technicianColumn.setCellValueFactory(ticket -> new SimpleStringProperty(ticket.getValue().getAssignedTechnician()));

        TableColumn<Ticket, String> createdColumn = new TableColumn<>("Created");
        createdColumn.setCellValueFactory(ticket -> new SimpleStringProperty(ticket.getValue().getCreationDate()));
      //add the columns to the table
        ticketTable.getColumns().addAll(idColumn, subjectColumn, priorityColumn, statusColumn, submittedColumn, technicianColumn, createdColumn);

        ticketTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        //when table has no rows
        ticketTable.setPlaceholder(new Label("No tickets"));
        ticketTable.setPrefHeight(180);
    }
//getters for controller
	public VBox getRoot() {
		return root;
	}

	public Button getLogoutButton() {
		return logoutButton;
	}

	public TableView<Ticket> getTicketTable() {
		return ticketTable;
	}

	public ComboBox<String> getTechnicianBox() {
		return technicianBox;
	}

	public Button getAssignButton() {
		return assignButton;
	}
	public Button getChangePasswordButton() {
		return changePasswordButton;
	}
	
	public Button getManageUsersButton() {
	    return manageUsersButton;
	}
    
	public Button getManageTechsButton() {
        return manageTechsButton;
    }
	
	public Button getReportsButton() {
	     return reportsButton;
	}
	
	public TextArea getDescriptionArea() {
        return descriptionArea;
    }
	
	//search related getters cause this a nuff getters
	public TextField getSearchId() {
	        return searchId;
	}

	public TextField getSearchUser() {
	        return searchUser;
	}

	public ComboBox<String> getSearchTechnician() {
	        return searchTechnician;
	}

	public ComboBox<String> getSearchStatus() {
	        return searchStatus;
	}

	public ComboBox<String> getSearchPriority() {
	        return searchPriority;
	}

	public DatePicker getFromDate() {
	        return fromDate;
	}

	public DatePicker getToDate() {
	        return toDate;
	}

	public Button getSearchButton() {
	        return searchButton;
	    }

	 public Button getClearButton() {
	        return clearButton;
	    }	
}