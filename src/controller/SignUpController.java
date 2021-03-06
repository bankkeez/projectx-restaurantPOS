package controller;

import application.Login;
import application.Main;
import database.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.ScreenController;

/**
 * SignUpController contains method for handling all event receive from the
 * UserInterface.
 * 
 * @author Piyawat & Vichaphol
 *
 */
public class SignUpController {
	@FXML
	private Button confirm;
	@FXML
	private Button cancel;
	@FXML
	private TextField username;
	@FXML
	private PasswordField password;
	@FXML
	private PasswordField password2;
	@FXML
	private Alert alert;

	private DBManager dbm = DBManager.getInstance();

	/**
	 * Method for handling confirm button. When event receive then the
	 * implementation below is done. Every fail cases gives different Alert
	 * reply message.
	 * 
	 * @param event
	 */
	public void confirmButtonHandler(ActionEvent event) {
		if (username.getText().equals("")) {
			alert = new Alert(AlertType.ERROR, "Username is empty.", ButtonType.OK);
			alert.show();
		} else if (password.getText().equals("") || password2.getText().equals("")) {
			alert = new Alert(AlertType.ERROR, "Password or the confirmation is empty!", ButtonType.OK);
			alert.show();
		} else if (!password.getText().equals(password2.getText())) {
			alert = new Alert(AlertType.ERROR, "Password and the confirmation does not match.", ButtonType.OK);
			alert.show();
		} else {
			boolean allow = dbm.checkUser(username.getText());
			if (!allow) {
				alert = new Alert(AlertType.ERROR, "Username already exist! Please use another username.",
						ButtonType.OK);
				alert.setHeaderText("Inputfield Error");
				alert.show();
			}
			if (allow) {
				alert = new Alert(AlertType.NONE,
						"You are registered as an restaurant employee. Press ok to continue...", ButtonType.OK);
				alert.showAndWait().ifPresent(response -> {
					if (response == ButtonType.OK) {
						dbm.signUp(username.getText(), password.getText());
						ScreenController.switchWindow((Stage) confirm.getScene().getWindow(), new Login());
					}
				});
			}
		}
	}

	/**
	 * Method for handling cancel button. When event receive Main scene is
	 * shown.
	 * 
	 * @param event
	 */
	public void cancelButtonHandler(MouseEvent event) {
		ScreenController.switchWindow((Stage) cancel.getScene().getWindow(), new Main());
	}
}
