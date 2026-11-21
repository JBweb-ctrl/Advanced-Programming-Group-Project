package jm.edu.utech.helpdesk.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jm.edu.utech.helpdesk.controller.AdminDashboardController;
import jm.edu.utech.helpdesk.controller.ChangePasswordController;
import jm.edu.utech.helpdesk.controller.LoginController;
import jm.edu.utech.helpdesk.controller.ManageTechniciansController;
import jm.edu.utech.helpdesk.controller.ManageUsersController;
import jm.edu.utech.helpdesk.controller.RegisterController;
import jm.edu.utech.helpdesk.controller.ReportsController;
import jm.edu.utech.helpdesk.controller.TechnicianDashboardController;
import jm.edu.utech.helpdesk.controller.UserDashboardController;
import jm.edu.utech.helpdesk.network.NotificationListener;
import jm.edu.utech.helpdesk.view.AdminDashboardView;
import jm.edu.utech.helpdesk.view.ChangePasswordView;
import jm.edu.utech.helpdesk.view.LoginView;
import jm.edu.utech.helpdesk.view.ManageTechniciansView;
import jm.edu.utech.helpdesk.view.ManageUsersView;
import jm.edu.utech.helpdesk.view.RegisterView;
import jm.edu.utech.helpdesk.view.ReportsView;
import jm.edu.utech.helpdesk.view.TechnicianDashboardView;
import jm.edu.utech.helpdesk.view.UserDashboardView;

//handle moving between screens
//every controller calles it when it needs a screen it serves as navigation
public class SceneManager {

	private static final Logger logger = LogManager.getLogger(SceneManager.class);

	//main window where every screen is in
	private static Stage stage;

	//who is logged in, their role (Student/Staff, Administrator or Technician from the server)
	//and the socket thread that receives their notifications
	private static String currentUser;
	private static String currentRole;
	private static NotificationListener notificationListener;

	private SceneManager() {}

	//stores the main window so show methods can use it
	public static void init(Stage primaryStage) {
		stage = primaryStage;
	}

	//shows the login screen (also logs the user out)
	public static void showLogin() {
		endSession();
		LoginView view = new LoginView();
		new LoginController(view);
		setRoot(view.getRoot());
	}

	//show the registration screen
	public static void showRegister() {
		RegisterView view = new RegisterView();
		new RegisterController(view);
		setRoot(view.getRoot());
	}

	//opens the right dashboard for the role of whoever is logged in
	public static void showDashboard(String username) {
		if ("Technician".equals(currentRole)) {
			showTechnicianDashboard(username);
		} else if ("Administrator".equals(currentRole)) {
			showAdminDashboard(username);
		} else {
			showUserDashboard(username);
		}
	}

	//shows the user dashboard
	public static void showUserDashboard(String username) {
		UserDashboardView view = new UserDashboardView(username);
		new UserDashboardController(view, username);
		setRoot(view.getRoot());
	}

	//shows the tech dashboard
	public static void showTechnicianDashboard(String username) {
		TechnicianDashboardView view = new TechnicianDashboardView(username);
		new TechnicianDashboardController(view, username);
		setRoot(view.getRoot());
	}

	//show the admin dashboard
	public static void showAdminDashboard(String username) {
		AdminDashboardView view = new AdminDashboardView(username);
		new AdminDashboardController(view, username);
		setRoot(view.getRoot());
	}

	//shows the change password screen
	public static void showChangePassword(String username) {
		ChangePasswordView view = new ChangePasswordView();
		new ChangePasswordController(view, username);
		setRoot(view.getRoot());
	}

	//shows the manage users screen
	public static void showManageUsers(String username) {
		ManageUsersView view = new ManageUsersView();
		new ManageUsersController(view, username);
		setRoot(view.getRoot());
	}

	 //role is guessed from the username for admin (So the technicians tech and tech2 are in the list
	    //in technicianservice so you dont need if the name == tech or whatever (old code)
	/* public static void showDashboard(String username) {
		 if (TechnicianService.getInstance().exists(username)) {
	            showTechnicianDashboard(username);
	        } else if (username.equalsIgnoreCase("admin")) {
	            showAdminDashboard(username);
	        } else {
	            showUserDashboard(username);
	        }
	 } */
//show the manage technicians screen
	public static void showManageTechnicians(String username) {
		ManageTechniciansView view = new ManageTechniciansView();
		new ManageTechniciansController(view, username);
		setRoot(view.getRoot());
	}

	//show the reports screen
	public static void showReports(String username) {
		ReportsView view = new ReportsView();
		new ReportsController(view, username);
		setRoot(view.getRoot());
	}

	//puts a screen in the window
	private static void setRoot(Parent root) {
		if (stage.getScene() == null) {
			stage.setScene(new Scene(root, 900, 600));
		} else {
			stage.getScene().setRoot(root);
		}
	}

	//called after the server accepts the login and shows their screen/dashboard
	public static void loginAs(String username, String role) {
		endSession();
		currentUser = username;
		currentRole = role;
		logger.info("User login: {} ({})", username, role);
		//start receiving notifications on a background thread
		notificationListener = new NotificationListener(username);
		notificationListener.start();
		showDashboard(username);
	}

	//user logged out 
	private static void endSession() {
		if (notificationListener != null) {
			notificationListener.stopListening();
			notificationListener = null;
		}
		if (currentUser != null) {
			logger.info("User logout: {}", currentUser);
			currentUser = null;
			currentRole = null;
		}
	}

	//called by MainApp when the window closes
	public static void shutdown() {
		endSession();
	}
}