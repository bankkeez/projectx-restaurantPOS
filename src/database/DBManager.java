package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import model.Menu;
import model.PrivilegeEnum;
import model.User;
import util.PropertyManager;

/**
 * DBManager contains method for managing data on database. Also associate with
 * classes that use the database. With help from BCrypt all password are
 * encrypted.
 * 
 * @author Piyawat & Vichaphol
 *
 */
public class DBManager {
	private PropertyManager pm = PropertyManager.getInstance();
	private static DBManager instance;
	private Connection connection;
	private String DB_URL = pm.getProperty("database.url");
	private String USER = pm.getProperty("database.user");
	private String PASS = pm.getProperty("database.password");
	private String sqlCommand;

	/**
	 * Private constructor for DBManger. Getting the connection from the database.
	 */
	private DBManager() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (SQLException ex) {
			System.out.println("Couldn't connect to the database");
		} catch (ClassNotFoundException e) {
			System.out.println("Couldn't find the class");
		}
	}

	/**
	 * Method for getting the instance of DBManager with the condition if the
	 * instance is null, create new instance.
	 * 
	 * @return instance of the DBManager
	 */
	public static DBManager getInstance() {
		if (instance == null) {
			instance = new DBManager();
		}
		return instance;
	}

	/**
	 * Method for getting data from the database to check the Login's input.
	 * 
	 * @param username
	 *            from Login's input
	 * @param password
	 *            from Login's input
	 * @return 2 for manager, 1 for normal employee, 0 = wrong password, -1 = user
	 *         doesn't exists
	 */
	public int login(String user, String pass) {
		sqlCommand = "SELECT * FROM User WHERE name = ?";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			stmt.setString(1, user);
			ResultSet rs = stmt.executeQuery();
			String dbPass = "";
			if (rs.next()) {
				dbPass = rs.getString("password");
			}
			if (BCrypt.checkpw(pass, dbPass)) {
				return rs.getInt("access type");
			}
			if (!dbPass.equals("")) {
				return 0;
			}
		} catch (SQLException ex) {
			System.out.println("Couldn't get data from the database");
		} catch (StringIndexOutOfBoundsException ex) {
			return -1;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
		return -1;

	}

	/**
	 * Method for inserting data(new user's data) to the database. The access type
	 * is set to 1 by default but can be change later on.
	 * 
	 * @param username
	 *            from SignUp window
	 * @param password
	 *            from SignUp window
	 */
	public void signUp(String user, String pass) {
		sqlCommand = "INSERT INTO `User` (`name`, `password`, `access type`) VALUES (?, ?, ?)";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			stmt.setString(1, user);
			String hashpw = BCrypt.hashpw(pass, BCrypt.gensalt());
			stmt.setString(2, hashpw);
			stmt.setInt(3, 1);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Couldn't update the data to database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
	}

	/**
	 * Method for getting data from the database to check whether if the username
	 * inputed has already exist or not.
	 * 
	 * @param username
	 *            from SignUp window
	 * @return false if username match, true if no match
	 */
	public boolean checkUser(String user) {
		sqlCommand = "SELECT * FROM User WHERE name = ?";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			stmt.setString(1, user);
			ResultSet rs = stmt.executeQuery();
			int dbInt = 0;
			if (rs.next()) {
				dbInt = rs.getInt("access type");
			}
			if (dbInt == 1 || dbInt == 2) {
				return false;
			}
		} catch (SQLException e) {
			System.out.println("Couldn't get data from the database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
		return true;
	}

	/**
	 * Method for getting all the food urls from the database.
	 * 
	 * @param table
	 * @return List<String> of food urls
	 */
	public List<String> getFoodUrl(String table) {
		List<String> tmpUrl = new ArrayList<>();
		sqlCommand = "SELECT * FROM " + table;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String text = rs.getString("url");
				tmpUrl.add(text);
			}
		} catch (SQLException e) {
			System.out.println("Couldn't get data from the database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
		return tmpUrl;
	}

	/**
	 * Method for getting data from the database which are food names and prices to
	 * create a Menu object.
	 * 
	 * @param tablename
	 * @return List<Menu> of food names
	 */
	public List<Menu> getFoodname(String foodkind) {
		List<Menu> tmpFoodname = new ArrayList<>();
		sqlCommand = "SELECT * FROM " + foodkind;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String text = rs.getString("name");
				int price = rs.getInt("price");
				Menu mn = new Menu(text, price);
				tmpFoodname.add(mn);
			}
		} catch (SQLException e) {
			System.out.println("Couldn't get data from the database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database.");
			}
		}
		return tmpFoodname;
	}

	/**
	 * Method for inserting the data into the database.
	 * 
	 * @param tablename
	 *            in the database
	 * @param food's
	 *            name
	 * @param food's
	 *            price
	 * @param url
	 */
	public void insertTo(String foodtable, String name, Integer price, String url) {
		sqlCommand = "INSERT INTO `" + foodtable + "` (`name`, `price`, `url`) VALUES (?, ?, ?)";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			stmt.setString(1, name);
			stmt.setInt(2, price);
			stmt.setString(3, url);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Couldn't update the data to database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
	}

	/**
	 * Method for checking table existence in database.
	 * 
	 * @param table
	 *            number
	 * @return true if table exist, false if not
	 */
	public boolean checkTable(String tableNumber) {
		DatabaseMetaData dbm = null;
		String tmpTable = "table" + tableNumber;
		try {
			dbm = connection.getMetaData();
			ResultSet table = dbm.getTables(null, null, tmpTable, null);
			if (table.next()) {
				return true;
			}
		} catch (SQLException e) {
			System.out.println("Couldn't get data from the database");
		}
		return false;
	}

	/**
	 * Method for creating a new table in database.
	 * 
	 * @param table
	 *            number
	 */
	public void createTable(String tableNumber) {
		String table = "table" + tableNumber;
		sqlCommand = "CREATE TABLE " + table + "(name VARCHAR (255), price INT(11), quantity INT(11))";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			stmt.execute();
		} catch (SQLException e) {
			System.out.println("Couldn't connect to the database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
	}

	/**
	 * Method for getting list of users from the database.
	 * 
	 * @return List<User> of users
	 */
	public List<User> getDBUser() {
		List<User> tmpUser = new ArrayList<>();
		sqlCommand = "SELECT * FROM " + "User";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String text = rs.getString("name");
				User user = new User(text, PrivilegeEnum.USER);
				tmpUser.add(user);
			}
		} catch (SQLException e) {
			System.out.println("Couldn't get data from the database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
		return tmpUser;
	}

	/**
	 * Method for inserting current orders into the requested table in database.
	 * 
	 * @param tablenumber
	 * @param Map<Menu,Integer>
	 *            of orders
	 */
	public void orderToDB(String tableNumber, Map<Menu, Integer> map) {
		String tmpTable = "table" + tableNumber;
		sqlCommand = "INSERT INTO `" + tmpTable + "` (`name`, `price`, `quantity`) VALUES (?, ?, ?)";
		PreparedStatement stmt = null;
		try {
			for (Map.Entry<Menu, Integer> order : map.entrySet()) {
				stmt = connection.prepareStatement(sqlCommand);
				String name = order.getKey().getName();
				int price = order.getKey().getPrice();
				int qty = order.getValue();
				stmt.setString(1, name);
				stmt.setInt(2, price);
				stmt.setInt(3, qty);
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("Couldn't update the data to database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
	}

	/**
	 * Method for getting all orders and from the wanted table in database and
	 * collect them as Map<Menu,Integer>.
	 * 
	 * @param tableNumber
	 * @return Map<Menu,Integer> of orders
	 */
	public Map<Menu, Integer> getDBOrders(String tableNumber) {
		Map<Menu, Integer> temp = new LinkedHashMap<>();
		Map<Menu, Integer> temp2 = new LinkedHashMap<>();
		sqlCommand = "SELECT * FROM " + tableNumber;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				int price = rs.getInt("price");
				int qty = rs.getInt("quantity");
				Menu menu = new Menu(name, price);
				if (!temp.containsKey(menu)) {
					temp.put(menu, qty);
				} else {
					temp.put(menu, temp.get(menu) + qty);
				}
			}
			for (Map.Entry<Menu, Integer> x : temp.entrySet()) {
				if (x.getValue() > 0)
					temp2.put(x.getKey(), x.getValue());
			}
		} catch (SQLException e) {
			System.out.println("Couldn't get data from the database");
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
		return temp2;
	}

	/**
	 * Method for clearing all records in the wanted database table.
	 * 
	 * @param table
	 *            requested
	 */
	public void clearTable(String table) {
		PreparedStatement stmt = null;
		sqlCommand = "DELETE FROM " + table;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			stmt.execute();
		} catch (SQLException ex) {
			System.out.println("Couldn't connect to the database");
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
	}

	/**
	 * Method for inserting items, which is going to be paid, to the table in
	 * database with specific tbale number.
	 * 
	 * @param tablenumber
	 * @param list
	 *            of the table items
	 */
	public void insertToSummary(Map<Menu, Integer> map) {
		sqlCommand = "INSERT INTO `Summary` (`name`, `price`, `quantity`) VALUES (?, ?, ?)";
		PreparedStatement stmt = null;
		try {
			for (Map.Entry<Menu, Integer> order : map.entrySet()) {
				stmt = connection.prepareStatement(sqlCommand);
				String name = order.getKey().getName();
				int price = order.getKey().getPrice();
				int qty = order.getValue();
				stmt.setString(1, name);
				stmt.setInt(2, price);
				stmt.setInt(3, qty);
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("Couldn't update the data to database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
	}

	/**
	 * Boolean method to check whether the requested menu exist in the table
	 * database or not.
	 * 
	 * @param foodName
	 * @param tableNumber
	 * @return true if the order exist, false if not
	 */
	public boolean checkDBFood(String foodName, String tableNumber) {
		String tmpTable = "table" + tableNumber;
		sqlCommand = "SELECT * FROM " + tmpTable + " WHERE name = ?";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			stmt.setString(1, foodName);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return true;
			}

		} catch (SQLException e) {
			System.out.println("Couldn't get data from the database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
		return false;
	}

	/**
	 * Method for inserting data directly to the database without using the model.
	 * 
	 * @param tableNumber
	 * @param Menu
	 *            wish to remove
	 */
	public void insertTo(String tableNumber, Menu menu) {
		sqlCommand = "INSERT INTO `" + tableNumber + "` (`name`, `price`, `quantity`) VALUES (?, ?, ?)";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			stmt.setString(1, menu.getName());
			stmt.setInt(2, menu.getPrice());
			stmt.setInt(3, -1);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Couldn't insert the data to database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
	}

	/**
	 * Method for inserting tablenumber into 'Tables'.
	 * 
	 * @param tableNumber
	 */
	public void insertTableNumber(String tableNumber) {
		sqlCommand = "INSERT INTO `Tables` (`number`) VALUES (?)";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			stmt.setString(1, tableNumber);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Couldn't insert the data to database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
	}

	/**
	 * Method for getting all number of tables from database.
	 * 
	 * @return List<String> of tables that is created
	 */
	public List<String> getDBTables() {
		List<String> tables = new ArrayList<>();
		sqlCommand = "SELECT * FROM Tables";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String table = rs.getString("number");
				tables.add(table);
			}

		} catch (SQLException e) {
			System.out.println("Couldn't get data from the database");
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
		return tables;
	}

	/**
	 * Method for checking is there is orders in the table.
	 * 
	 * @param tableNumber
	 * @return true if table is empty, false if not
	 */
	public boolean checkTableData(String tableNumber) {
		int value = 1;
		String tmpTable = "table" + tableNumber;
		sqlCommand = "SELECT * FROM " + tmpTable;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {
				return true;
			}

		} catch (SQLException e) {
			System.out.println("Couldn't connect to the database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
		Map<Menu, Integer> temp = getDBOrders("table" + tableNumber);
		for (Map.Entry<Menu, Integer> tmp : temp.entrySet()) {
			int tmpValue = tmp.getValue();
			value += tmpValue;
		}
		if (value == 1) {
			return true;
		}
		return false;
	}

	/**
	 * Method for deleting requested table in database.
	 * 
	 * @param tableNumber
	 */
	public void deleteTable(String tableNumber) {
		String table = "table" + tableNumber;
		sqlCommand = "DROP TABLE " + table;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			stmt.execute();
		} catch (SQLException e) {
			System.out.println("Couldn't connect to the database");
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
	}

	/**
	 * Method for removing requested tablenumber from 'Tables'.
	 * 
	 * @param tableNumber
	 */
	public void removeTableinTables(String tableNumber) {
		sqlCommand = "DELETE FROM Tables WHERE number = ?";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			stmt.setString(1, tableNumber);
			stmt.execute();
		} catch (SQLException e) {
			System.out.println("Couldn't connect to the database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
	}

	/**
	 * Method for removing user from the database
	 * 
	 * @param User
	 */
	public void removeUserDB(String name) {
		sqlCommand = "DELETE FROM User WHERE name = ?";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			stmt.setString(1, name);
			stmt.execute();
		} catch (SQLException e) {
			System.out.println("Couldn't connect to the database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
	}

	/**
	 * Method for removing image data from the database
	 * 
	 * @param foodtable
	 * @param Menu
	 */
	public void removeImage(String foodtable, Menu item) {
		sqlCommand = "DELETE FROM " + foodtable + " WHERE name = ?";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			stmt.setString(1, item.getName());
			stmt.execute();
		} catch (SQLException e) {
			System.out.println("Couldn't connect to the database");
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
	}

	/**
	 * Refresh records by getting tale rows for comparing later.
	 * 
	 * @param tableNumber
	 * @return amount of rows in table
	 */
	public int refreshRecords(String tableNumber) {
		String table = "table" + tableNumber;
		int count = 0;
		sqlCommand = "SELECT * FROM " + table;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sqlCommand);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				count++;
			}

		} catch (SQLException e) {
			System.out.println("Couldn't connect to the database");
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				System.out.println("Couldn't close the connection from the database");
			}
		}
		return count;
	}

}
