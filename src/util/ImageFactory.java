package util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.bcel.internal.util.ClassLoader;

import database.DBManager;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;
import model.Menu;
import model.Order;
import sun.security.jca.GetInstance;

public class ImageFactory {
	/** singleton instance of ImageFactory. */
	protected static ImageFactory factory;
	private static DBManager dbm = DBManager.getInstance();
	private static Order o = Order.getInstance();

	private static List<Menu> foodname = dbm.getFoodname("Foods");
	private static List<String> foodUrl = dbm.getFoodUrl("Foods");
	private static List<Button> buttonList = new ArrayList<Button>();

	/**
	 * Get an instance of ImageFactory.
	 * 
	 * @return object of a subclass
	 */
	public static ImageFactory getInstance() {
		if (factory == null)
			factory = new ImageFactory();
		return factory;
	}

	public ImageView getImage(String filename) {
		Image image = new Image(filename);
		return new ImageView(image);
	}

	public static List<Button> getButton() {
		return buttonList;

	}

	public List<Button> setImageToButton() {
		int i = 0;
		for (Menu item : foodname) {
			Button button = new Button(item.getName());
			Image image = new Image(foodUrl.get(i));
			i++;
			ImageView view = new ImageView(image);
			view.setFitHeight(100);
			view.setFitWidth(100);
			button.setPrefSize(150, 150);
			button.setWrapText(true);
			button.setTextAlignment(TextAlignment.CENTER);
			button.setGraphic(view);
			button.setUserData(item);

			// set handler for the button
			button.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					o.addOrder((Menu) button.getUserData());
					System.out.println(((Menu) button.getUserData()).getName());

				}
			});
			buttonList.add(button);
		}
		return buttonList;

	}
}
