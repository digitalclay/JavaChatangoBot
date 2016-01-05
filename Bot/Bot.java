package bot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
 
/** 
 * @author Jon Rollins Synaptic Studios Jp - 2015
 * Chatango Room Client
 * Screen name DigitalClay
 * Bot name MentalEncryption
 */
public class Bot {

	JFrame frame;
	static JTextArea area;
	private JTextField userText;

	JFrame infoFrame;
	JButton login;
	JTextField room;
	JTextField user;
	JTextField password;
	JRadioButton useRoom;
	boolean useRoomB;
	JRadioButton usePM;
	boolean usePMB;
	JRadioButton useAnon;
	boolean useAnonB;
	JTextArea info;
	
	static PrintWriter writer;

	static Date date;
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		try{
			new Bot();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Bot() throws IOException{
		// main frame//
		frame = new JFrame();
		frame.setSize(800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setTitle("Java Chatango Client : 0.9 ");
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(frame,
						"Are you sure to close this window?",
						"Really Closing?", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					closeFile();
				}
			}
		});
		area = new JTextArea("[INFO] Connecting...\n");
		area.setEditable(false);
		area.setForeground(Color.WHITE);
		area.setBackground(Color.BLACK);
		userText = new JTextField();
		userText.setEditable(true);
		userText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String s1 = userText.getText().trim();

				try {
					Room.sendFieldText(s1);
					Pm.consoleCommands(s1);
				} catch (Exception e1) {
					e1.printStackTrace();
				} finally {
					userText.setText("");
				}
			}
		});

		frame.add(userText, BorderLayout.SOUTH);
		frame.add(new JScrollPane(area));
		frame.setVisible(true);

		// User and Room Info Frame//
		infoFrame = new JFrame();
		infoFrame.setSize(500, 200);
		infoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		infoFrame.setResizable(false);
		infoFrame.setLayout(new FlowLayout());
		infoFrame.setTitle("Java Chatango Client : 0.9 - Room and User Info ");

		room = new JTextField("digitalmasterminds69");
		room.setEditable(true);
		room.setSize(150, 20);
		user = new JTextField("mentalencryption");
		user.setEditable(true);
		user.setSize(150, 20);
		password = new JTextField("YourPassword");
		password.setEditable(true);
		password.setSize(150, 20);

		usePM = new JRadioButton("Use PM");
		useRoom = new JRadioButton("Use Rooms");
		useAnon = new JRadioButton("Login as Anon");

		login = new JButton("Login");
		login.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				usePMB = usePM.isSelected();
				useRoomB = useRoom.isSelected();
				useAnonB = useAnon.isSelected();
				if (useAnonB) {
					if (useRoomB) {
						try {
							String roomS = room.getText().toLowerCase().trim();
							new Room(roomS);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					if (usePMB) {
						try {
							String userS = user.getText().toLowerCase().trim();
							String passS = password.getText().toLowerCase()
									.trim();
							new Pm(userS, passS);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				if (!useAnonB) {
					if (useRoomB) {
						try {
							String userS = user.getText().toLowerCase().trim();
							String roomS = room.getText().toLowerCase().trim();
							String passS = password.getText().toLowerCase()
									.trim();
							new Room(roomS, userS, passS);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					if (usePMB) {
						try {
							String userS = user.getText().toLowerCase().trim();
							String passS = password.getText().toLowerCase()
									.trim();
							new Pm(userS, passS);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				infoFrame.dispose();
				
				JFrame consoleCmds = new JFrame();
				consoleCmds.setSize(300,200);
				consoleCmds.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				consoleCmds.setResizable(false);
				consoleCmds.setTitle("Java Chatango Client : 0.9 - Console Commands");
				consoleCmds.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent windowEvent) {
						if (JOptionPane.showConfirmDialog(frame,
								"Are you sure to close this window?",
								"Really Closing?", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
							closeFile();
						}
					}
				});
				
				JTextArea cmdList = new JTextArea(
						"Console Commands :\n" 
								
								+"\n"
						
								+"***ROOM***\n"
								+"-/cmd:quiet:true/false\n" 
								+"-/cmd:fontcolor:\"fff\"\n" 
								+"-/cmd:fontface:\"adobe garamond pro bold\"\n"
								+"-/cmd:fontsize:\"20\"\n"
								+"-/cmd:namecolor:\"fff\"\n"
								+"-/cmd:addmod:\"name\"\n"
								+"-/cmd:removemod:\"name\"\n"
								+"-/cmd:ban:\"name\"\n"
								+"-/cmd:unban:\"name\"\n"
								+"-/cmd:join:\"roomname\"\n"
								+"-/cmd:leave:\"roomname\"\n"
								+"-/cmd:post:\"roomname\":\"the message\"\n" 
								+"-/cmd:getbanlist\n"
								+"-/cmd:getmods\n"
								+"-/cmd:getowner\n"
								+"-/cmd:getchatlist\n"
								+"-/cmd:getcount\n"
								
								+"\n"
								
								+"***PM***\n"
								+"-/cmd:pm:\"name\":\"the message\"\n"
								+"-/cmd:addfriend:name\n"
								+"-/cmd:removefriend:name/false\n"
								+"-/cmd:block:name\n"
								+"-/cmd:unblock:name\n"
								+"-/cmd:getfriendlist\n"
								+"-/cmd:getblocklist\n"
								
						);

				cmdList.setEditable(false);
				cmdList.setForeground(Color.WHITE);
				cmdList.setBackground(Color.BLACK);
				consoleCmds.add(new JScrollPane(cmdList));
				consoleCmds.setVisible(true);
			}
		});

		info = new JTextArea(
				"Enter room name and account info."
						+ "\nAlso,check to use rooms and pms.\nIf using pms you must always enter account info.");
		info.setEditable(false);

		infoFrame.add(info);
		infoFrame.add(usePM);
		infoFrame.add(useRoom);
		infoFrame.add(useAnon);
		infoFrame.add(room);
		infoFrame.add(user);
		infoFrame.add(password);
		infoFrame.add(login);
		infoFrame.setVisible(true);

		createLogFile();

		date = new Date();
	}
	
	
	
	
	/**
	 * @throws IOException
	 */
	private void createLogFile() throws IOException {
		writer = new PrintWriter(new FileWriter("log.txt", true));
	}

	public static void closeFile() {
		writer.close();
	}
	

	
	
	////////////////////////////////////////////////////
	//Static methods///////////////////////////////////
	//////////////////////////////////////////////////
	
	
	/**
	 * Print to console, window, and file.
	 * @param line
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static void print(String line) throws FileNotFoundException,
			UnsupportedEncodingException {
		String s1 = String.format("%s \n", line);
		area.append("[ "+date.toString()+" ] "+s1);

		writer.append("[ "+date.toString()+" ] "+s1);

	}
	
	/**
	 * Join a new Room.
	 * @param roomName
	 * @param username
	 * @param password
	 */
	public static void joinRoom(String roomName, String username, String password){
		try {
			Room room = new Room(roomName, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getDate(){
		return "[ "+date.toString()+" ] ";
	}

	
	public static Timestamp convertTimestamp(String timeStmp){
		
		long g = Long.parseLong(timeStmp.trim());
		Timestamp timeStamp = new Timestamp(g*1000);
		return timeStamp;
		
	}
}
