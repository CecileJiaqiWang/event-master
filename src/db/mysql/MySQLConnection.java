package db.mysql;

import java.sql.DriverManager;
import db.DBConnection;
import entity.Item;

public class MySQLConnection {
	private Connection conn;
	
	public MySQLConnection() {
		try {
			
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}

}
