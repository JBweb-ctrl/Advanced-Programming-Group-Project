package jm.edu.utech.helpdesk.view;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//reports screen where admin picks a report and reads it
public class ReportsView {

	private final VBox root = new VBox(15);
	private final Button backButton = new Button("Back");
	//choosing and generating a report
	private final ComboBox<String> reportBox = new ComboBox<>();
	private final Button generateButton = new Button("Generate");
	private final TextArea resultArea = new TextArea();
	
	public ReportsView() {
		
		Label title = new Label("Reports");
		title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
		
		HBox topRow = new HBox(20, title, backButton);
		//admins can choose a report
		reportBox.getItems().addAll("Tickets Submitted", "Tickets Resolved", "Open Tickets", "Technician Workload", "Average Resolution Time");
		reportBox.setPromptText("Select report");
		
		HBox chooseRow = new HBox(10, new Label("Report:"), reportBox, generateButton);
		
		resultArea.setEditable(false);
		resultArea.setPrefRowCount(10);
		resultArea.setPromptText("The report will appear here");
		
		root.setPadding(new Insets(20));
		root.getChildren().addAll(topRow, chooseRow, resultArea);
	}

	public VBox getRoot() {
		return root;
	}

	public Button getBackButton() {
		return backButton;
	}

	public TextArea getResultArea() {
		return resultArea;
	}

	public ComboBox<String> getReportBox() {
		return reportBox;
	}

	public Button getGenerateButton() {
		return generateButton;
	}
	
	
	
}
