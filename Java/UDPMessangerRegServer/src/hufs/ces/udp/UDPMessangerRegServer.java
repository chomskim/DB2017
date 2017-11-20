package hufs.ces.udp;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import hufs.ces.utils.DBConn;


public class UDPMessangerRegServer extends Thread {

	final static int BUF_SIZE = 65535;
	public final static int DEFAULT_PORT = 7772;
	
	private Connection conn = null;
	
	private int bufferSize; // in bytes
	private DatagramSocket socket;

	private SocketAddress theSender;
	private String chatID;
	
	private String fromHost;
	private int fromPort;
	
	public UDPMessangerRegServer(int port, int bufferSize) 
			throws SocketException {
		this.bufferSize = bufferSize;
		this.socket = new DatagramSocket(port);

		updateAllChatStateOff();
	}
	public UDPMessangerRegServer(int port) throws SocketException {
		this(port, BUF_SIZE);
	}
	public UDPMessangerRegServer() throws SocketException {
		this(DEFAULT_PORT); 
	}

	public void run() {	  
		byte[] buffer = new byte[bufferSize];

		while (true) {
			DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
			try {			
				socket.receive(incoming);
				theSender = incoming.getSocketAddress();
				InetAddress inetHost = incoming.getAddress();
				fromHost = inetHost.getHostAddress();
				fromPort = incoming.getPort();
				
				System.out.println("theSender="+theSender.toString()+"<<");
				System.out.println("host ip="+fromHost+":"+"port="+fromPort);

				byte[] data = new byte[incoming.getLength()];
				System.arraycopy(incoming.getData(), 0, data, 0, incoming.getLength());
				String inText = new String(data, "UTF-8");
				System.out.println("inText="+inText+"<<");
				
				if (inText.startsWith("##register##")) {
					chatID = inText.substring("##register##".length());
					System.out.println("id="+chatID+"<<");
					int incount = insertRegisterRecord (chatID, fromHost, fromPort, "on");
					if (incount > 0) {
						register(chatID);
					}
					else {
						System.out.println(chatID+" is invalid");
					}
				}
				else if (inText.startsWith("##disconnect##")) {
					String id = inText.substring("##disconnect##".length());
					int updcount = updateConnRecord (id, "off");
					System.out.println(id+" set state off, retcount="+updcount);
				}
				else if (inText.startsWith("##send:")) {
					String restText = inText.substring("##send:".length());
					String[] indata = restText.split("##", 2);
					chatID = indata[0];
					String msgText = indata[1];
					System.out.println("chatID="+chatID+"--msgText="+msgText+"<<");
					String buddyID = findBuddy(chatID);
					System.out.println("chatID="+chatID+",buddyID="+buddyID+"--msgText="+msgText+"<<");

					sendMsg(buddyID, msgText);
				}

			}
			catch (IOException e) {
				System.err.println(e);
			}
		} // end while

	}  // end run
	int  insertRegisterRecord (String chatID, String hostIP, int port, String chatState) {

		int affectedRows = 0;
		try {
			conn = new DBConn().getConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		PreparedStatement pstmt = null;

		String SQL = "select * from id_address_table where chat_state = \'off\'"
				+ " and chat_id = ? ";
		ResultSet resultSet = null;
		try {
			pstmt = conn.prepareStatement(SQL, 
					ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_UPDATABLE);
			pstmt.setString(1, chatID);
			resultSet = pstmt.executeQuery();

			int rowcount = 0;
			if (resultSet.last()) {
				rowcount = resultSet.getRow();
				//resultSet.beforeFirst(); 
			}	  
			resultSet.close();
			pstmt.close();

			if (rowcount > 0) {
				SQL = "UPDATE id_address_table SET "
						+ "host_ip=?, port=?, chat_state=? WHERE chat_id=?";

				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, hostIP);
				pstmt.setInt(2, port);
				pstmt.setString(3, chatState);
				pstmt.setString(4, chatID);

				affectedRows = pstmt.executeUpdate();
			}
			else {
				SQL = "INSERT INTO id_address_table(chat_id, host_ip, port, chat_state) "
						+ "VALUES(?,?,?,?)";

				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, chatID);
				pstmt.setString(2, hostIP);
				pstmt.setInt(3, port);
				pstmt.setString(4, chatState);

				affectedRows = pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt!=null) pstmt.close();
				if (conn!=null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return affectedRows;
	}
	int updateConnRecord (String chatID, String chatState) {

		int affectedRows = 0;
		try {
			conn = new DBConn().getConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		PreparedStatement pstmt = null;

		String SQL = "UPDATE id_address_table SET chat_state=? WHERE chat_id=?";

		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, chatState);
			pstmt.setString(2, chatID);

			affectedRows = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt!=null) pstmt.close();
				if (conn!=null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return affectedRows;
	}
	public void register (String chatID) {

		try {
			conn = new DBConn().getConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String SQL = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		int affectedRows = 0;

		try {
			SQL = "select * from id_address_table where chat_state = \'on\'"
					+ " and chat_id != ? ";
			pstmt = conn.prepareStatement(SQL, 
					ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_UPDATABLE);
			pstmt.setString(1, chatID);
			resultSet = pstmt.executeQuery();

			int rowcount = 0;
			if (resultSet.last()) {
				rowcount = resultSet.getRow();
				//resultSet.beforeFirst(); 
			}

			String buddy_id = chatID; // self
			if (rowcount > 0) { // find first on and not yet connected
				resultSet.first();
				buddy_id = resultSet.getString("chat_id");
			}
			else { 
				// myself is only on
			}
			resultSet.close();
			pstmt.close();


			if (chatID.equals(buddy_id)) {
				SQL = "insert into buddy_conn_table(chat_id, buddy_id, conn_state) "
						+ "values(?,?,?)";

				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, chatID);
				pstmt.setString(2, chatID);
				pstmt.setString(3, "on");

				affectedRows = pstmt.executeUpdate();
				pstmt.close();
			}
			else {
				SQL = "insert into buddy_conn_table(chat_id, buddy_id, conn_state) "
						+ "values(?,?,?)";
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, chatID);
				pstmt.setString(2, buddy_id);
				pstmt.setString(3, "on");

				affectedRows = pstmt.executeUpdate();				
				pstmt.close();
				
				SQL = "UPDATE buddy_conn_table SET "
				+ "chat_id=?, buddy_id=?, conn_state=? WHERE chat_id=?";
				pstmt = conn.prepareStatement(SQL);
				pstmt.setString(1, buddy_id);
				pstmt.setString(2, chatID);
				pstmt.setString(3, "on");
				pstmt.setString(4, buddy_id);

				affectedRows = pstmt.executeUpdate();
				pstmt.close();
				
				// now pair is connected
				updateConnRecord (chatID, "conn");
				updateConnRecord (buddy_id, "conn");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt!=null) pstmt.close();
				if (conn!=null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	void updateAllChatStateOff() {

		int affectedRows = 0;
		try {
			conn = new DBConn().getConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		PreparedStatement pstmt = null;

		String SQL = "UPDATE id_address_table SET chat_state=\'off\' ";

		try {
			pstmt = conn.prepareStatement(SQL);

			affectedRows = pstmt.executeUpdate();
			System.out.println(affectedRows+" rows are changed to off");

			// clear all record in connection table
			Statement stmt  = conn.createStatement();
			stmt.execute("TRUNCATE buddy_conn_table");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt!=null) pstmt.close();
				if (conn!=null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	String findBuddy(String chatID) {
		
		try {
			conn = new DBConn().getConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String SQL = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		String buddyID = chatID; // self
		
		try {
			SQL = "select * from buddy_conn_table where chat_id = ? ";
			pstmt = conn.prepareStatement(SQL, 
					ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_UPDATABLE);
			pstmt.setString(1, chatID);
			resultSet = pstmt.executeQuery();

			int rowcount = 0;
			if (resultSet.last()) {
				rowcount = resultSet.getRow();
				//resultSet.beforeFirst(); 
			}
			if (rowcount > 0) { 
				resultSet.first();
				buddyID = resultSet.getString("buddy_id");
			}
			System.out.println("chatID="+chatID+",buddyID="+buddyID+"--buddy count="+rowcount);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt!=null) pstmt.close();
				if (conn!=null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return buddyID;

	}

	void sendMsg(String chatID, String msgText) {

		String toHost = null;
		int toPort = 0;

		try {
			conn = new DBConn().getConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String SQL = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		
		try {
			SQL = "select * from id_address_table where chat_id = ? ";
			pstmt = conn.prepareStatement(SQL, 
					ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_UPDATABLE);
			pstmt.setString(1, chatID);
			resultSet = pstmt.executeQuery();

			int rowcount = 0;
			if (resultSet.last()) {
				rowcount = resultSet.getRow();
				//resultSet.beforeFirst(); 
			}			
			if (rowcount > 0) { 
				resultSet.first();
				toHost = resultSet.getString("host_ip");
				toPort = resultSet.getInt("port");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt!=null) pstmt.close();
				if (conn!=null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			InetAddress hostAddr = InetAddress.getByName(toHost);
			byte[] msgData = msgText.getBytes("UTF-8");
			DatagramPacket outgoing = new DatagramPacket(msgData, msgData.length, hostAddr, toPort);
			
			System.out.println("inetaddr="+hostAddr+",outaddr="+outgoing.getAddress()
			+",port="+toPort+"--Msg="+msgText+"<<");
			socket.send(outgoing);
		}
		catch (java.io.UnsupportedEncodingException ex) {
			System.err.println(ex);
		}
		catch (IOException ex) {
			System.err.println(ex);
		}

	}
	public static void main(String[] args) {

		try {
			UDPMessangerRegServer server = new UDPMessangerRegServer();
			server.start();
		}
		catch (SocketException ex) {
			System.err.println(ex);
		}

	}

}
