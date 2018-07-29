package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;

public class MySQLConnection implements DBConnection {
	private Connection conn;
	
	public MySQLConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * Close database connection.
	 */
	public void close() {
		if (conn != null) {
			// Close connection if the connection was open.
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItem(Item item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
