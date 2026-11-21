package jm.edu.utech.helpdesk.view;

import jm.edu.utech.helpdesk.model.Technician;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//screen admin uses to manage technicians
public class ManageTechniciansView {
	
	private final VBox root = new VBox (15);
	
	private final Button backButton = new Button("Back");
	//table of technicians
	private final TableView<Technician> technicianTable = new TableView<>();
	
	private final TextField usernameField = new TextField();
	private final Button addButton = new Button ("Add Technician");
	private final Button removeButton = new Button ("Remove Technician");
//method that creates the controls and arrange them
	public ManageTechniciansView() {
		Label title = new Label("Manage Technicians");
		title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
		
		HBox topRow = new HBox(20, title,backButton);
		
		createTable();
		
		usernameField.setPromptText("Enter Username");
		
		GridPane form = new GridPane();
		form.setVgap(10);
		form.setHgap(10);
		form.add(new Label("Username"), 0, 0);
		form.add(usernameField, 1, 0);
		form.add(new HBox(10, addButton, removeButton), 1, 1);
		
		root.setPadding(new Insets(20));
		root.getChildren().addAll(topRow, technicianTable, form);
	}
	
	@SuppressWarnings ("unchecked")
	private void createTable() {
		
		TableColumn<Technician, String> usernameColumn = new TableColumn<>("Username");
		usernameColumn.setCellValueFactory(technician -> 
		new SimpleStringProperty(technician.getValue().getUsername()));
		//add the column to the table
		technicianTable.getColumns().addAll(usernameColumn);
		technicianTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		
		technicianTable.setPlaceholder(new Label("No Technicians"));
		technicianTable.setPrefHeight(300);
		
	}

	public VBox getRoot() {
		return root;
	}

	public Button getBackButton() {
		return backButton;
	}

	public TableView<Technician> getTechnicianTable() {
		return technicianTable;
	}

	public TextField getUsernameField() {
		return usernameField;
	}

	public Button getAddButton() {
		return addButton;
	}

	public Button getRemoveButton() {
		return removeButton;
	}
	
}
