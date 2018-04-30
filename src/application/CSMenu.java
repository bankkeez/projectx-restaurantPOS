package application;

import javafx.stage.Stage;

/**
 * CSMenu class that extends AbstractWindow for capable of running the method
 * switchWindow. This window is for selecting the food, which can be edited, in
 * the customer mode.
 * 
 * @author Piyawat & Vichaphol
 *
 */
public class CSMenu extends AbstractWindow {

	private String tablenumber;

	// for getting the table number from CSTable
	public CSMenu(String input) {
		this.tablenumber = input;
	}

	public int getTableNumber() {
		return Integer.parseInt(this.tablenumber);
	}

	public void start(Stage stage) {
		try {
			super.setFilename("view/customer-menu.fxml");
			super.start(stage);
			stage.setTitle("Customer Menu");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}