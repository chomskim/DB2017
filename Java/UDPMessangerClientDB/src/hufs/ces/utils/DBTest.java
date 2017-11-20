package hufs.ces.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class DBTest {

	public static void main(String[] args) {

		Connection conn = null;
		try {
			conn = new DBConn().getConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String SQL = "INSERT INTO id_address_table(chat_id, host_ip, port, chat_state) "
                + "VALUES(?,?,?,?)";
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			
			pstmt.setString(1, "aaa");
            pstmt.setString(2, "192.168.219.172");
			pstmt.setInt(3, 53235);
            //pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            pstmt.setString(4, "on");
 
            int affectedRows = pstmt.executeUpdate();
            System.out.println(affectedRows+" row(s) has been changed");
            
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("select * from id_address_table");
            
            while(resultSet.next()) {
            	String chat_id = resultSet.getString("chat_id");
            	String host_ip = resultSet.getString("host_ip");
            	int port = resultSet.getInt("port");
            	Timestamp reg_date = resultSet.getTimestamp("reg_date");
            	String chat_state = resultSet.getString("chat_state");
            	
            	System.out.format("%s	%s	%d	%s	%s", chat_id, host_ip, port, reg_date, chat_state);
            	System.out.println();
            	            
            }            
	        resultSet.close();
        } catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
