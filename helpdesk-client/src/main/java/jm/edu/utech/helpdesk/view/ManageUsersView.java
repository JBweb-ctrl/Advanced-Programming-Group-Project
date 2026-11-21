package jm.edu.utech.helpdesk.view;

import jm.edu.utech.helpdesk.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ManageUsersView {

	private final VBox root = new VBox(15);
    private final Button backButton = new Button("Back");
    //table that lists the users.
    private final TableView<User> userTable = new TableView<>();

    
    private final TextField usernameField = new TextField();
    private final ComboBox<String> roleBox = new ComboBox<>();
    private final Button addButton = new Button("Add User");
    private final Button removeButton = new Button("Remove Selected");
    
    public ManageUsersView() {
    	Label title = new Label("Manage Users");
    	title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
    	HBox topRow = new HBox(20, title, backButton);
    	
    	createTable();
    	
    	usernameField.setPromptText("Enter username");
    	roleBox.getItems().addAll("Student/Staff", "Administrator");
    	roleBox.setPromptText("Select your role");
    	
    	GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Username:"), 0, 0);
        form.add(usernameField, 1, 0);
        form.add(new Label("Role:"), 0, 1);
        form.add(roleBox, 1, 1);
        form.add(new HBox(10, addButton, removeButton), 1, 2);
        
        root.setPadding(new Insets(20));
        root.getChildren().addAll(topRow, userTable, form);
    }
    
    @SuppressWarnings("unchecked")
    private void createTable() {
    	//makes the table columns
    	TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(user -> new SimpleStringProperty(user.getValue().getUsername()));

        TableColumn<User, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(user -> new SimpleStringProperty(user.getValue().getRole()));
        //add the colums to the table
        userTable.getColumns().addAll(usernameColumn, roleColumn);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        //when table has no users
        userTable.setPlaceholder(new Label("No users."));
        userTable.setPrefHeight(300);
    }

	public VBox getRoot() {
		return root;
	}

	public Button getBackButton() {
		return backButton;
	}

	public TableView<User> getUserTable() {
		return userTable;
	}

	public TextField getUsernameField() {
		return usernameField;
	}

	public ComboBox<String> getRoleBox() {
		return roleBox;
	}

	public Button getAddButton() {
		return addButton;
	}

	public Button getRemoveButton() {
		return removeButton;
	}
}
