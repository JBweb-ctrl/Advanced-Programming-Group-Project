package jm.edu.utech.helpdesk.service;
//asks the REST server for technicians (list, add, remove)
//throws HelpDeskClientException when something goes wrong
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import jm.edu.utech.helpdesk.model.Technician;

public final class TechnicianApi {

	private TechnicianApi() {}

	//every technician on the server
	public static List<Technician> getTechnicians() {
		ApiResponse response = ApiClient.send("GET", "/technicians", null);
		return ApiClient.read(response, new TypeReference<List<Technician>>() {});
	}

	//just the usernames, for the admin dropdowns
	public static List<String> getTechnicianNames() {
		List<String> names = new ArrayList<>();
		for (Technician technician : getTechnicians()) {
			names.add(technician.getUsername());
		}
		return names;
	}

	//adds a technician, the server says no (409 error) if the username is taken
	public static Technician addTechnician(String username) {
		ApiResponse response = ApiClient.send("POST", "/technicians", Map.of("username", username));
		return ApiClient.read(response, Technician.class);
	}

	//removes a technician
	public static void removeTechnician(String username) {
		ApiClient.send("DELETE", "/technicians/" + username, null);
	}
}
