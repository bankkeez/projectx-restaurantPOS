package controller;

import application.Login;
import application.SignUp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.ScreenController;

/**
 * StartUpController contains method for handling all event receive from the
 * UserInterface.
 * 
 * @author Piyawat & Vichaphol
 *
 */
public class StartUpController {
	@FXML
	private Button login;
	@FXML
	private Button signUp;

	/**
	 * Method for handling login button. When event receive Login scene is
	 * shown.
	 * 
	 * @param event
	 */
	public void loginButtonHandler(MouseEvent event) {
		ScreenController.switchWindow((Stage) login.getScene().getWindow(), new Login());
	}

	/**
	 * Method for handling signUp button. When event receive SignUp scene is
	 * shown.
	 * 
	 * @param event
	 */
	public void signUpButtonHandler(MouseEvent event) {
		ScreenController.switchWindow((Stage) signUp.getScene().getWindow(), new SignUp());
	}

}
