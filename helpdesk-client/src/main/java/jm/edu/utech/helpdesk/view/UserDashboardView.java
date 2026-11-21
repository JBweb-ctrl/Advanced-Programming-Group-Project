package jm.edu.utech.helpdesk.view;

import jm.edu.utech.helpdesk.model.Comment;
import jm.edu.utech.helpdesk.model.Ticket;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
//user dashboard that allows them to see their tickets, submit a new one, read and add comments
public class UserDashboardView {
//vertical layout
    private final VBox root = new VBox(15);
    private final Button logoutButton = new Button("Logout");
    private final Button changePasswordButton = new Button("Change Password");
    //table that list user tickets
    private final TableView<Ticket> ticketTable = new TableView<>();

    // inputs to make a new ticket
    private final TextField subjectField = new TextField();
    private final ComboBox<String> priorityBox = new ComboBox<>();
    private final TextArea descriptionField = new TextArea();
    private final Button submitButton = new Button("Submit Ticket");
    private final Button editButton = new Button("Edit selected");
    private final Button saveButton = new Button("Save Changes");
    private final Button closeButton = new Button("Close Ticket");
    private final Button reopenButton = new Button("Reopen Ticket");

    // used to view ticket descriptions and comments
    private final TextArea detailsArea = new TextArea();
    //shows how the ticket was solved
    private final TextArea resolutionArea = new TextArea();
    private final ListView<Comment> commentList = new ListView<>();
    private final TextField commentField = new TextField();
    private final Button addCommentButton = new Button("Add Comment");
    
//creates the controls and puts them on screen
    public UserDashboardView(String username) {
    	//welcome the user and put the logout button beside it
        Label welcomeLabel = new Label("Welcome, " + username);
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox topRow = new HBox(20, welcomeLabel, changePasswordButton, logoutButton);

        createTable();

        //creating a new ticket
        subjectField.setPromptText("Enter subject");
        priorityBox.getItems().addAll("LOW", "MEDIUM", "HIGH");
        priorityBox.setPromptText("Select priority");
        descriptionField.setPromptText("Describe the problem");
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);
        
        //grid to keep the label and fields lined up
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Subject:"), 0, 0);
        form.add(subjectField, 1, 0);
        form.add(new Label("Priority:"), 0, 1);
        form.add(priorityBox, 1, 1);
        form.add(new Label("Description:"), 0, 2);
        form.add(descriptionField, 1, 2);
        HBox formButtons = new HBox(10, submitButton, editButton, saveButton);
        form.add(formButtons, 1, 3);

        Label formHeading = new Label("Submit a new support request");
        formHeading.setStyle("-fx-font-weight: bold;");
        //puts the table and new ticket form on the left side
        HBox statusButtons = new HBox(10, closeButton, reopenButton);
        VBox leftSide = new VBox(15, ticketTable,statusButtons ,formHeading, form);

        //details of the ticket (description)
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefRowCount(4);
        detailsArea.setPromptText("Select a ticket to see its description");
        resolutionArea.setEditable(false);
        resolutionArea.setWrapText(true);
        resolutionArea.setPrefRowCount(3);
        resolutionArea.setPromptText("No resolution notes yet");
        commentList.setPrefHeight(150);
        commentField.setPromptText("Write a comment");
        
        //puts the comment, tech resolution notes and description on the right side
        VBox rightSide = new VBox(10, new Label("Description:"), detailsArea, new Label("Resolution Notes:"), resolutionArea, new Label("Comments:"), commentList, commentField, addCommentButton);
        rightSide.setPrefWidth(300);
        //puts the sides beside each other
        HBox content = new HBox(20, leftSide, rightSide);

        root.setPadding(new Insets(20));
        root.getChildren().addAll(topRow, content);
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
        ticketTable.getColumns().addAll(idColumn, subjectColumn, priorityColumn, statusColumn, createdColumn);

        ticketTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        //when table has no rows
        ticketTable.setPlaceholder(new Label("No tickets yet"));
        ticketTable.setPrefSize(560, 220);
    }
//getters for controllers
	public VBox getRoot() {
		return root;
	}

	public Button getLogoutButton() {
		return logoutButton;
	}

	public TableView<Ticket> getTicketTable() {
		return ticketTable;
	}

	public TextField getSubjectField() {
		return subjectField;
	}

	public ComboBox<String> getPriorityBox() {
		return priorityBox;
	}

	public TextArea getDescriptionField() {
		return descriptionField;
	}

	public Button getSubmitButton() {
		return submitButton;
	}

	public TextArea getDetailsArea() {
		return detailsArea;
	}

	public ListView<Comment> getCommentList() {
		return commentList;
	}

	public TextField getCommentField() {
		return commentField;
	}

	public Button getAddCommentButton() {
		return addCommentButton;
	}

	public Button getEditButton() {
        return editButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCloseButton() {
        return closeButton;
    }

    public Button getReopenButton() {
        return reopenButton;
    }
    
    public Button getChangePasswordButton() { 
    	return changePasswordButton; 
    }
    
    public TextArea getResolutionArea() {
        return resolutionArea;
    }
}