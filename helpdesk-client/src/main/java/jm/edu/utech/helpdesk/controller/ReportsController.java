package jm.edu.utech.helpdesk.controller;
import jm.edu.utech.helpdesk.exception.HelpDeskClientException;
import jm.edu.utech.helpdesk.service.ReportApi;
import jm.edu.utech.helpdesk.util.AlertUtil;
import jm.edu.utech.helpdesk.util.ErrorHandler;
import jm.edu.utech.helpdesk.util.SceneManager;
import jm.edu.utech.helpdesk.view.ReportsView;

//reports screen the admin will see
public class ReportsController {

	private final ReportsView view;
	private final String username;
	
	public ReportsController(ReportsView view, String username) {
		this.view = view;
		this.username = username;
		attachHandlers();
	}
	//what happens when each button is pressed
	private void attachHandlers() {
		//generate a report once you choose what type of report to generate
		view.getGenerateButton().setOnAction(e -> handleGenerate());
		//go back to admin dashboard
		view.getBackButton().setOnAction(e -> SceneManager.showAdminDashboard(username));
	}
	//runs when the admin clicks generate 
	private void handleGenerate() {
		String report = view.getReportBox().getValue();
		
		if (report == null) {
			AlertUtil.showError("No report selected", "Select a report");
			return;
		}
		//checks wich report was picked in the dropdown option, then asks the server for that report
		//so if you choose "Tickets Submitted" it calls GET /api/reports/tickets-submitted
		String path;
		if (report.equalsIgnoreCase("Tickets Submitted")) {
			path = "tickets-submitted";
		} else if (report.equalsIgnoreCase("Tickets Resolved")) {
			path = "tickets-resolved";
		} else if (report.equalsIgnoreCase("Open Tickets")) {
			path = "open-tickets";
		} else if (report.equalsIgnoreCase("Technician Workload")) {
			path = "technician-workload";
		} else if (report.equalsIgnoreCase("Average Resolution Time")) {
			path = "average-resolution-time";
		} else {
			AlertUtil.showError("Unknown report", "That report is not available");
			return;
		}

		try {
			view.getResultArea().setText(ReportApi.getReport(path));
		} catch (HelpDeskClientException e) {
			ErrorHandler.handle(e);
		}
	}
}
