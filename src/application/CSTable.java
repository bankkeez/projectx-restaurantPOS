package application;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * CSTable class that extends Application for capable of running the method
 * switchWindow. This window is for entering the customers table number for food
 * ordering.
 * 
 * @author Piyawat & Vichapol
 *
 */
public class CSTable extends Application {

	@Override
	public void start(Stage stage) {
		try {
			String fxmlfile = "customer-table.fxml";
			URL url = getClass().getResource(fxmlfile);
			// Load the FXML and get reference to the loader
			FXMLLoader loader = new FXMLLoader(url);
			// Create scene graph from file (UI)
			Parent root = loader.load();
			// Show the scene
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.sizeToScene();
			stage.setTitle("Table Number");
			stage.setResizable(false);
			stage.centerOnScreen();
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
