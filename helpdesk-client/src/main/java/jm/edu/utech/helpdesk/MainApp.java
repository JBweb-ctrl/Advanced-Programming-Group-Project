package jm.edu.utech.helpdesk;
import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import jm.edu.utech.helpdesk.controller.LoginController;
import jm.edu.utech.helpdesk.util.SceneManager;
import jm.edu.utech.helpdesk.view.LoginView;
//starts the help desk app
//since there are more than one screens for each major action and user all scene/screen 
//related things are in Scenemanager it serves as a helper class basically
public class MainApp extends Application {

	private static final Logger logger = LogManager.getLogger(MainApp.class);

	@Override
	public void start (Stage stage) {
		//logs any error a thread doesnt catch so it isnt lost
		Thread.setDefaultUncaughtExceptionHandler((thread, error) ->
				logger.error("Unhandled exception in thread {}", thread.getName(), error));
		logger.info("Help Desk client starting");
		//LoginView loginView = new LoginView();
		//new LoginController(loginView);
		//stage.setScene(new Scene(loginView.getRoot(), 800,500));
		SceneManager.init(stage);
		SceneManager.showLogin();
		stage.setTitle("Utech Help Desk");
		stage.show();
	}
	
	
	//called by JavaFX when the window closes: stop background threads cleanly
	@Override
	public void stop() {
		SceneManager.shutdown();
		logger.info("Help Desk client closed");
	}
	
	public static void main(String[] args) {
		
		launch(args);
	}

}
