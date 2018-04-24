package application;

import javafx.stage.Stage;

/**
 * CSTable class that extends Application for capable of running the method
 * switchWindow. This window is for entering the customers table number for food
 * ordering.
 * 
 * @author Piyawat & Vichapol
 *
 */
public class CSTable extends AbstractWindow {

	@Override
	public void start(Stage stage) {
		try {
			super.setFilename("customer-table.fxml");
			super.start(stage);
			stage.setTitle("Table Number");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
