package jm.edu.utech.helpdesk.service;
//asks the REST server about user accounts (login, register, change password, manage users)

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import jm.edu.utech.helpdesk.model.User;

public final class UserApi {

	private UserApi() {}

	//checks the username and password, gives back the username and role staff or admin
	//wrong details then the server says 401 and ApiClient throws InvalidLoginException
	public static User login(String username, String password) {
		ApiResponse response = ApiClient.send("POST", "/users/login",
				Map.of("username", username, "password", password));
		return ApiClient.read(response, User.class);
	}

	//register screen, creates a Student/Staff account, username taken throws DuplicateUserException)
	public static User register(String username, String password) {
		ApiResponse response = ApiClient.send("POST", "/users", Map.of("username", username, "password", password));
		return ApiClient.read(response, User.class);
	}

	//changes the password, the current one must be right, wrong gives InvalidLoginException)
	public static void changePassword(String username, String currentPassword, String newPassword) {
		ApiClient.send("PUT", "/users/" + username + "/password", Map.of("currentPassword", currentPassword, "newPassword", newPassword));
	}

	//every user on the server
	public static List<User> getUsers() {
		ApiResponse response = ApiClient.send("GET", "/users", null);
		return ApiClient.read(response, new TypeReference<List<User>>() {});
	}

	//admin adds a user, the server gives them the default password
	public static User addUser(String username, String role) {
		ApiResponse response = ApiClient.send("POST", "/users", Map.of("username", username, "role", role));
		return ApiClient.read(response, User.class);
	}

	//admin removes a user
	public static void removeUser(String username) {
		ApiClient.send("DELETE", "/users/" + username, null);
	}
}
