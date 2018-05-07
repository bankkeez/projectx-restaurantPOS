package controller;

import java.util.List;
import java.util.Map;
import java.util.Observable;

import application.CheckBill;
import application.Login;
import application.Main;
import application.Tableview;
import database.DBManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.Menu;
import model.Order;
import util.ScreenController;
import util.UserManager;

/**
 * OrderController(manager/employee) contains method for handling all event
 * receive from the UserInterface. Contains method for viewing and ordering
 * customer orders. (With help form TA for spacing nodes)
 * 
 * @author Piyawat & Vichaphol & P'Jacky
 *
 */
public class OrderViewController implements java.util.Observer {
	@FXML
	private Button order;
	@FXML
	private Button clear;
	@FXML
	private Button remove;
	@FXML
	private Button back;
	@FXML
	private Button exit;
	@FXML
	private Button checkBill;
	@FXML
	private TextField totalPrice;
	@FXML
	private FlowPane foodpane;
	@FXML
	private FlowPane drinkpane;
	@FXML
	private TextArea display;
	@FXML
	private TextArea display2;
	@FXML
	private Alert alert;

	private static String tablenumber;

	// for single instantiation
	private static List<Menu> foods;
	private static List<Menu> drinks;
	private static UserManager um = UserManager.getInstance();
	private static Order o = Order.getInstance();
	private static DBManager dbm = DBManager.getInstance();

	// under construction
	private static OrderViewController instance;

	private boolean admin = um.isAdmin();

	@FXML
	public void initialize() {
		if (!admin) {
			remove.setDisable(true);
			remove.setVisible(false);
		}
		// adding buttons to foodpane
		System.out.println(tablenumber);
		setButtons(foods, foodpane);
		setButtons(drinks, drinkpane);
		display.setDisable(true);
		display.setText(tablenumber);
		display2.setDisable(true);
		setDisplay2();
	}

	// during in test
	public OrderViewController() {

	}

	// during in test
	public static OrderViewController getInstance() {
		if (instance == null)
			instance = new OrderViewController();
		return instance;
	}

	// during in test (used in OrderTable)
	public void setDisplay(String text) {
		try {
			display.setText(text);
			System.out.println("display : " + text);
			System.out.println("display method is working");
		} catch (NullPointerException ex) {
			System.out.println("display method is not working");
			ex.printStackTrace();
		}
	}

	// during in test
	public TextArea getDisplay() {
		return this.display;
	}

	// during in test (use in this class)
	public void setDisplay() {
		String text = null;
		try {
			text = o.orderToText(o.getOrders());
			System.out.println("text is not null");
		} catch (NullPointerException ex) {
			System.out.println("text is null");
			ex.printStackTrace();
		}
		try {
			instance.getDisplay();
			System.out.println("display is not null");
		} catch (NullPointerException ex) {
			System.out.println("display is null");
			ex.printStackTrace();
		}
		// this line below keeps null
		instance.getDisplay().setText(text);
		System.out.println("method update in OVC is working");
	}

	public void setTemporary() {
		String text = o.orderToText(o.getOrders());
		display.setText(text);
	}

	// during in test (seems to work the most)
	@Override
	public void update(Observable observable, Object arg) {
		setDisplay();
	}

	public void setDisplay2() {
		Map<Menu, Integer> temp = dbm.getDBOrders(tablenumber);
		String text = o.orderToText(temp);
		display2.setText(text);
	}

	/**
	 * Private method for the controller to create and add buttons to the
	 * container.
	 * 
	 * @param List<Menu>
	 *            any menu list
	 */
	private void setButtons(List<Menu> items, FlowPane pane) {
		for (Menu item : items) {
			Button button = new Button(item.getName());
			button.setPrefSize(100, 100);
			button.setWrapText(true);
			button.setTextAlignment(TextAlignment.CENTER);
			button.setUserData(item);
			// set handler for the button
			button.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					o.addOrder((Menu) button.getUserData());
					setTemporary();
					System.out.println(o.orderToText(o.getOrders()));
				}
			});
			// add button to the pane
			pane.getChildren().add(button);
		}
	}

	/**
	 * Handler for order button. When event receive orders are sent out to the
	 * database.
	 * 
	 * @param event
	 */
	public void orderButtonHandler(MouseEvent event) {
		// if order list is empty
		if (o.getOrders().isEmpty()) {
			alert = new Alert(AlertType.ERROR, "You have not order anything !", ButtonType.OK);
			alert.show();
		}
		// order confirmation
		else {
			alert = new Alert(AlertType.CONFIRMATION, "Are you sure to order?", ButtonType.YES, ButtonType.NO);
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.YES) {
					o.printOrders();
					System.out.println("Current order(s): " + o.getOrders().size());
					Map<Menu, Integer> temp = o.getOrders();
					dbm.orderToDB(tablenumber, temp);
					o.clearOrders();
					//during in test (will use observer pattern)
					setDisplay2();
					display.setText("");
				}
			});
		}
	}

	/**
	 * Handler for clear button. When event receive current orders from
	 * Map<Menu,QTY> is removed.
	 * 
	 * @param event
	 */
	public void clearButtonHandler(MouseEvent event) {
		o.clearOrders();
		System.out.println("all current orders cleared.");
		System.out.println("Map size: " + o.getOrders().size());
		//during in test (will use observer pattern)
		display.setText("");
	}

	/**
	 * Handler for back button. When event receive the CS table scene is shown.
	 * 
	 */
	public void backButtonHandler(ActionEvent event) {
		ScreenController.switchWindow((Stage) back.getScene().getWindow(), new Tableview());
	}

	/**
	 * Handler for back button. When event receive the CS table scene is shown.
	 * 
	 */
	public void billButtonHandler(MouseEvent event) {
		ScreenController.switchWindow((Stage) back.getScene().getWindow(), new CheckBill(0 + ""));
	}

	/**
	 * Handler for logout button. When event receive the Start up scene is
	 * shown.
	 * 
	 */
	public void exitButtonHandler(ActionEvent event) {
		ScreenController.switchWindow((Stage) exit.getScene().getWindow(), new Main());
	}

	/**
	 * Static method for scene before opening this scene to get the button text
	 * and set as table number.
	 * 
	 * @param buttonText
	 */
	public static void setTable(String buttonText) {
		tablenumber = buttonText;
	}

	/**
	 * Static method for scene before opening this scene to get list of menu
	 * names and set the List<Menu> attribute above.
	 * 
	 * @param List
	 *            of menu names List<Menu>
	 */
	public static void setMenu(List<Menu> arg, List<Menu> arg2) {
		foods = arg;
		drinks = arg2;
	}

	// during in test
	public int getTable() {
		return Integer.parseInt(tablenumber);
	}
}
