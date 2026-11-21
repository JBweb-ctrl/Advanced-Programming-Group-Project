package jm.edu.utech.helpdesk.view;
//tech dashboard that allows them to see the tickets assigned to them , update ticket status and resolve it
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
public class TechnicianDashboardView {
//vertical layout
    private final VBox root = new VBox(15);
    private final Button logoutButton = new Button("Logout");
    private final Button changePasswordButton = new Button("Change Password");
  //table that list tech tickets
    private final TableView<Ticket> ticketTable = new TableView<>();
    // inputs to change a ticket status
    private final ComboBox<String> statusBox = new ComboBox<>();
    private final Button updateStatusButton = new Button("Update Status");
    //input to write resolution notes
    private final TextArea notesArea = new TextArea();
    //shows the tech the description of the problem
    private final TextArea descriptionArea = new TextArea();
    private final Button resolveButton = new Button("Mark as Resolved");
    
  //creates the controls and puts them on screen
    public TechnicianDashboardView(String username) {
//put the title and logout button at the top of the page
        Label title = new Label("Technician Dashboard - " + username);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox topRow = new HBox(20, title, changePasswordButton, logoutButton);

        createTable();
        //choices tech can pick from 
        statusBox.getItems().addAll("IN_PROGRESS", "PENDING", "RESOLVED");
        statusBox.setPromptText("Select status");

        HBox statusRow = new HBox(10,
                new Label("Status:"), statusBox, updateStatusButton);

        notesArea.setPromptText("Enter resolution notes");
        notesArea.setPrefRowCount(3);
        notesArea.setWrapText(true);
        //technician can see description
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(3);
        descriptionArea.setPromptText("Select a ticket to see its description");
        //puts things into main layout with padding
        root.setPadding(new Insets(20));
        root.getChildren().addAll(topRow, ticketTable, new Label("Description:"), descriptionArea,statusRow,
                new Label("Resolution Notes:"), notesArea, resolveButton);
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

        TableColumn<Ticket, String> createdColumn = new TableColumn<>("Created");
        createdColumn.setCellValueFactory(ticket -> new SimpleStringProperty(ticket.getValue().getCreationDate()));
        //add the cloumns to the table
        ticketTable.getColumns().addAll(
                idColumn, subjectColumn, priorityColumn, statusColumn, createdColumn);
      //when table has no rows
        ticketTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        ticketTable.setPlaceholder(new Label("No assigned tickets"));
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

	public ComboBox<String> getStatusBox() {
		return statusBox;
	}

	public Button getUpdateStatusButton() {
		return updateStatusButton;
	}

	public TextArea getNotesArea() {
		return notesArea;
	}

	public Button getResolveButton() {
		return resolveButton;
	}
	
	public Button getChangePasswordButton() { 
		return changePasswordButton; 
	}
	
	public TextArea getDescriptionArea() {
        return descriptionArea;
    }
    
}