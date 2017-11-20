package hufs.ces.udp;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import hufs.ces.utils.DBConn;

public class UDPVisualMessangerClientDB extends JFrame {

	private static final long serialVersionUID = 1L;
	final static int BUF_SIZE = 65535;
	final static int SERVER_PORT = 7772;
	//final static String SERVER_HOST = "192.168.219.154";
	//final static String SERVER_PORT = "220.67.121.119";
	final static String SERVER_HOST = "192.168.219.103";

	private UDPVisualMessangerClientDB thisClass = this;

	private Connection conn = null;
	
	private static String hostname;
	private static int port;

	private InetAddress server;
	public Sender sender = null;
	private ReceiverThread receiver = null;

	private JPanel jContentPane = null;

	private JButton jbtConnect = null;

	private JScrollPane jScrollPane1 = null;

	private JTextArea jtaMessage = null;

	private JPanel jpMessages = null;

	private JPanel jpTextArea = null;

	private JLabel jLabel1 = null;

	private JTextField jtfSender = null;

	private JPanel jpLabels = null;

	private String chatID = null;
	/**
	 * This is the default constructor
	 */
	public UDPVisualMessangerClientDB() {
		super();
		hostname = SERVER_HOST;
		port = SERVER_PORT;

		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(373, 319);
		this.setContentPane(getJContentPane());
		this.setTitle("Messanger Client");

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				try {
					String regLine = "##disconnect##"+chatID;
					if (sender!=null) {
						sender.send(regLine);
					}
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				ev.getWindow().dispose();
			}
		});

	}
	/**
	 * This method initializes jbtConnect	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJbtConnect() {
		if (jbtConnect == null) {
			jbtConnect = new JButton();
			jbtConnect.setText("Connect");
			jbtConnect.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					jbtConnection();
				}

				private void jbtConnection() {
					try {
						server = InetAddress.getByName(hostname);
						sender = new Sender(server, port);

						jtaMessage.setText(hostname + ":" + port +" connected\n");
						jbtConnect.setEnabled(false);

						String id = JOptionPane.showInputDialog(thisClass, "Enter Your ID");
						if (id==null) return;
						jtfSender.setEnabled(true);

						String regLine = "##register##"+id;
						sender.send(regLine);
						chatID = id;

						receiver = new ReceiverThread(sender.getSocket());
						receiver.start();
					} 
					catch (UnknownHostException ex) {
						// TODO Auto-generated catch block
						//System.err.println(ex);
					} 
					catch (SocketException ex) {
						// TODO Auto-generated catch block
						System.err.println(ex);
					}

				}				

			});
		}

		return jbtConnect;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane1.setViewportView(getJtaMessage());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes jtaMessage	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJtaMessage() {
		if (jtaMessage == null) {
			jtaMessage = new JTextArea();
			jtaMessage.setLineWrap(true);
			jtaMessage.setEditable(false);
		}
		return jtaMessage;
	}

	/**
	 * This method initializes jpMessages	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJpMessages() {
		if (jpMessages == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			gridLayout.setHgap(4);
			gridLayout.setVgap(4);
			gridLayout.setColumns(2);
			jpMessages = new JPanel();
			jpMessages.setLayout(gridLayout);
			jpMessages.add(getJpTextArea(), null);
		}
		return jpMessages;
	}

	/**
	 * This method initializes jpTextArea	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJpTextArea() {
		if (jpTextArea == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Messanger");
			jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
			jpTextArea = new JPanel();
			jpTextArea.setLayout(new BorderLayout());
			jpTextArea.add(getJScrollPane1(), BorderLayout.CENTER);
			jpTextArea.add(jLabel1, BorderLayout.NORTH);
			jpTextArea.add(getJtfSender(), BorderLayout.SOUTH);
		}
		return jpTextArea;
	}

	/**
	 * This method initializes jtfSender	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJtfSender() {
		if (jtfSender == null) {
			jtfSender = new JTextField();
			jtfSender.setEnabled(false);
			jtfSender.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent ev) {
					String theLine = jtfSender.getText();

					jtaMessage.append("<<" + theLine +"\n");					
					jtfSender.setText("");

					if (theLine.equals(".")) {
						jtfSender.setEnabled(false);
						jtaMessage.append("disconnected\n");
					}
					else { // insert to db table to trigger udp send
						try {
							conn = new DBConn().getConnection();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						String SQL = null;
						PreparedStatement pstmt = null;
						int affectedRows = 0;

						try {
							SQL = "insert into chat_message_table(chat_id, msg_text) "
									+ "values(?,?)";

							pstmt = conn.prepareStatement(SQL);
							pstmt.setString(1, chatID);
							pstmt.setString(2, theLine);

							affectedRows = pstmt.executeUpdate();

						} catch (SQLException ex) {
							ex.printStackTrace();
						} finally {
							try {
								if (pstmt!=null) pstmt.close();
								if (conn!=null) conn.close();
							} catch (SQLException ex) {
								ex.printStackTrace();
							}
						}
					}

				}
			});
		}
		return jtfSender;
	}

	/**
	 * This method initializes jpLabels	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJpLabels() {
		if (jpLabels == null) {
			GridLayout gridLayout1 = new GridLayout();
			gridLayout1.setRows(1);
			jpLabels = new JPanel();
			jpLabels.setLayout(gridLayout1);
			jpLabels.add(getJbtConnect(), null);
		}
		return jpLabels;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UDPVisualMessangerClientDB thisClass = new UDPVisualMessangerClientDB();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});

	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJpMessages(), BorderLayout.CENTER);
			jContentPane.add(getJpLabels(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}
	class ReceiverThread extends Thread {

		DatagramSocket socket;
		private boolean stopped = false;

		public ReceiverThread(DatagramSocket ds) throws SocketException {
			this.socket = ds;
			socket.setSoTimeout(10000); // 10 sec.
		}

		public void halt() {
			this.stopped = true; 
		}

		public void run() {

			byte[] buffer = new byte[BUF_SIZE];
			while (true) {
				if (stopped) return;
				DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(dp);
					String s = new String(dp.getData(), 0, dp.getLength());
					jtaMessage.append(">>" + s + "\n");
					Thread.yield();
				}
				catch (SocketTimeoutException ex) {
					//System.err.println(ex);
				} 
				catch (IOException ex) {
					System.err.println(ex);
				} 

			}  

		}

	}

}
