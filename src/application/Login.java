package application;

import javafx.stage.Stage;
import util.AbstractWindow;

/**
 * Login class that extends AbstractWindow for capable of running the method
 * switchWindow. This window is for user to login in to their account for
 * further use.
 * 
 * @author Piyawat & Vichaphol
 *
 */
public class Login extends AbstractWindow {
	@Override
	public void start(Stage stage) {
		try {
			super.setFilename("view/login.fxml");
			super.start(stage);
			stage.setTitle("Login");
		} catch (Exception e) {
			System.out.println("Couldn't find the fxml file.");

		}

	}
}
