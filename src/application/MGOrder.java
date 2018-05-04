package application;

import controller.MGOrderController;
import javafx.stage.Stage;
import util.AbstractWindow;

/**
 * MGOrder(manager) class that extends AbstractWindow for capable of running
 * the method switchWindow. This window is for selecting the food, observing
 * customer's order, remove an order, and check their bills.
 * 
 * @author Piyawat & Vichaphol
 *
 */
public class MGOrder extends AbstractWindow {
	
	//for setting the tablenumber to MGOrderController
	public MGOrder(String input) {
		MGOrderController.setTable(input);
	}

	public void start(Stage stage) {
		try {
			super.setFilename("view/manager-order.fxml");
			super.start(stage);
			stage.setTitle("Manager Order");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
