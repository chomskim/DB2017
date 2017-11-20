package hufs.ces.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {

	private Config conf = null;
	
	private String driverName = null;
	private String dbmsName = null;
	private String serverip = null;
	private String portNum = null;
	private String dbname = null;
	private String username = null;
	private String password = null;


	public DBConn() {
		conf = Config.getInstance();
	}
	public Connection getConnection() throws ClassNotFoundException, SQLException {

		driverName = conf.getProperty("db.driver");
		dbmsName = conf.getProperty("db.dbms");
		serverip = conf.getProperty("db.conn.serverip");
		portNum = conf.getProperty("db.conn.port");
		dbname = conf.getProperty("db.conn.dbname");
		username = conf.getProperty("db.conn.username");
		password = conf.getProperty("db.conn.password");
		
		Connection conn = null;

		Class.forName(driverName);
		String serverURL = "jdbc:"+dbmsName+"://"+serverip+":"+portNum+"/"+dbname;
		conn = DriverManager.getConnection(serverURL, username, password);

		return conn;
	}
	public String getDriverName() {
		return driverName;
	}
	public String getDBMSName() {
		return dbmsName;
	}
	public String getServerIP() {
		return serverip;
	}
	public String getPortNum() {
		return portNum;
	}
	public String getDBname() {
		return dbname;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
}
