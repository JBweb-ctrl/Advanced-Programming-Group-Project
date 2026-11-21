package jm.edu.utech.helpdesk.service;
//asks the REST server for one of the five reports (the server builds the text, the client just shows it)
import jm.edu.utech.helpdesk.dto.ReportResponse;

public final class ReportApi {

	private ReportApi() {}

	//path is the end of the address, like "tickets-submitted" -> GET /api/reports/tickets-submitted
	public static String getReport(String path) {
		ApiResponse response = ApiClient.send("GET", "/reports/" + path, null);
		return ApiClient.read(response, ReportResponse.class).report();
	}
}
