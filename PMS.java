package bot;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * @author Jon Rollins Synaptic Studios Jp - 2015
 * Chatango Room Client
 * Screen name DigitalClay
 * Bot name MentalEncryption
 */
public class Pm {

	/**
	 * Website to login to get AUID
	 */
	final private String url = "http://chatango.com/login";

	/**
	 * Login Params
	 */
	private String user_id = "mentalencryption";
	private String password = "yourpassword /=";
	final private String storecookie = "on";
	final private String checkerrors = "yes";


	/**
	 * Used for socket
	 */
	final private String host = "c1.chatango.com";
	final private int port = 5222;

	/**
	 * Encoding
	 */
	final private String charset = "UTF-8";

	/**
	 * Authority user id
	 */
	private String auid = "";

	/**
	 * Socket
	 */
	private static Socket socket;
	
	/**
	 * Output and Input Streams
	 */
	private static ByteArrayOutputStream baos;
	private static DataOutputStream sockOutput;
	private static WritableByteChannel channel;
	private BufferedReader inputReader;
	
	/**
	 * Message and message sender
	 */
	private String rawMessageIn;
	private String cleanMessageIn;
	private String sender;
	
	/**
	 * Used for synchronization
	 */
	private final static Lock writeLock = new ReentrantLock();
	
	
	/**
	 * Idle Ping Timer
	 */
    private final ScheduledExecutorService executorService =
    		Executors.newSingleThreadScheduledExecutor();

    /**
     * Connected boolean
     */
    private boolean connected = false;
    
    
    /**
     * Freind and block lists
     */
	private static ArrayList<String> freinds = new ArrayList<String>();
	private static ArrayList<String> blocklist = new ArrayList<String>();
	private static HashMap<String,String> friendStatus = new HashMap<String,String>();//name , online:time offline
    
	
	/**
	 * First command - used for login command
	 */
	private static boolean firstCommand = true;
	
	
	
    /**
     *  CONSTRUCTOR
     *  
     * @throws IOException
     */
	public Pm() throws IOException {
		connect();
	}

	
	public Pm(String userS, String passS) throws IOException{
		user_id = userS;
		password = passS;
		connect();
	}


	/**
	 * 	CONNECT TO PMS
	 * 
	 * - Handles opening http connection , posting login params , getting the AUID ,
	 * 		creates a socket, send the login command, starts the reading loop,
	 * 		and the idle ping. 
	 */
	private void connect() {
		try {
			String params = formatParams();
			HttpURLConnection connection = setUpConnection(params);
			postParams(params, connection);
			checkResponseCode(connection);
			auid = getAUID(connection);
			String loginAuid = "tlogin:" + auid ;
			socket = createSocket();
			sendLoginCommand(loginAuid);
			connected = true;
			readResponse();
			startIdlePing();
			sendFriendList();
			sendBlockList();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


    /**
     * 	IDLE PING
     * 
     * -Used for whe the bot is idle to not disconnect from the server.
     * -Calls the sendPing() method every 20 seconds that sends a message to
     * 		the server and gets a response.
     * 
     * @throws IOException
     */
	private void startIdlePing() throws IOException {
	       executorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					sendPing();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 20L, 20L, TimeUnit.SECONDS);
	}


	/**
	 * 	SEND PING MESSAGE
	 * 
	 * -Sends an empty message to the server (empty with /r/n) ad recieves a response.
	 * -used when the bot is idle to keep it from being kicked from the server.
	 * 
	 * @throws IOException
	 */
	private void sendPing() throws IOException {
		if(connected){
			writeToServer("\r\n");
		}
	}

	
	/**
	 * 	READ RESPONSE - Main Bot Loop Thread
	 * 
	 * -Opens an input stream reader and loops inside of a thread.
	 * -Checks for messages from the server with a loop inside a thread.
	 * -If the message contains msg - it calls methods cleanMSG(), 
	 * 		getSender(), checkCommands(), and also prints cleaned msg to console.
	 * -If the message doesnt contain msg it prints the info to the console.
	 * -Handles all output from server , calling appropriate methods to handle input.
	 * -Exits client if input from server reads kicking off.
	 * @throws IOException
	 */
	private void readResponse() throws IOException {
		
		Thread readThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					
					String line;
					inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
					while ((line = inputReader.readLine()) != null) {	
						
						//Message Recieve//
						if(line.trim().startsWith("msg:")){
							rawMessageIn = line;
							cleanMSG(rawMessageIn);
							getSender(rawMessageIn);
							Bot.print("[PM] PMS Message: ("+sender+") "+cleanMessageIn);
							System.out.println("[PM] PMS Message: ("+sender+") "+cleanMessageIn);
							checkCommands();
							
						//On connect//
						}else if(line.trim().startsWith("OK")){
							Bot.print("[PM] PMS Server Response: "+line);
							System.out.println("[PM] PMS Server Response: "+line);
							onConnect(line);
							
						//On connect fail//
						}else if(line.trim().startsWith("DENIED")){
							Bot.print("[PM] PMS Server Response: "+line);
							System.out.println("[PM] PMS Server Response: "+line);
							onConnectFail(line);
							
						//Online/Offline friends list Recieve//
						}else if(line.trim().startsWith("status:")){
							Bot.print("[PM] PMS Server Response: "+line);
							System.out.println("[PM] PMS Server Response: "+line);
							onStatusUpdate(line);
							
						//Idle Update Recieve//
						}else if(line.trim().startsWith("idleupdate:")){
							Bot.print("[PM] PMS Server Response: "+line);
							System.out.println("[PM] PMS Server Response: "+line);
							onIdleUpdate(line);

						//On PM Offline msg//
						}else if(line.trim().startsWith("msgoff:")){
							Bot.print("[PM] PMS Server Response: "+line);
							System.out.println("[PM] PMS Server Response: "+line);
							onPmOfflineMsg(line);
							
						//on friend online //
						}else if(line.trim().startsWith("wlonline:")){
							Bot.print("[PM] PMS Server Response: "+line);
							System.out.println("[PM] PMS Server Response: "+line);
							onFriendOnline(line);
							
						//On friend offline//
						}else if(line.trim().startsWith("wloffline:")){
							Bot.print("[PM] PMS Server Response: "+line);
							System.out.println("[PM] PMS Server Response: "+line);
							onFriendOffline(line);

						//Freinds List Recieve//
						}else if (line.trim().startsWith("wl:")){
							Bot.print("[PM] PMS Server Response: "+line);
							System.out.println("[PM] PMS Server Response: "+line);
							onFriendsListRCV(line);
							
						//Block List Recieve//
						}else if (line.trim().startsWith("block_list:")){
							Bot.print("[PM] PMS Server Response: "+line);
							System.out.println("[PM] PMS Server Response: "+line);
							onBlockListRCV(line);
							
						//Kicking From Server Revieve//	
						}else if(line.trim().equals("kickingoff")){
							Bot.print("[ERROR] PMS Server Response: "+line);
							System.out.println("[ERROR] PMS Server Response: "+line);
							Bot.closeFile();
							exit();
						
						//Other Output From Server Recieve//
						}else if(!line.trim().isEmpty()){	
							Bot.print("[PM] PMS Server Response: "+line);
							System.out.println("[PM] PMS Server Response: "+line);
						}
						
					}
				} catch (IOException e) {
					try {
						Bot.print("[PM] Problem in readResponse() "+e.getMessage());
					} catch (FileNotFoundException
							| UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.out.println("[PM] Problem in readResponse() "+e.getMessage());
				}
			}
		});
		readThread.start();
	}

	protected void onFriendOffline(String line) {
		// TODO Auto-generated method stub
		
	}


	protected void onFriendOnline(String line) {
		// TODO Auto-generated method stub
		
	}


	protected void onPmOfflineMsg(String line) {
		// TODO Auto-generated method stub
		
	}


	protected void onConnectFail(String line) {
		// TODO Auto-generated method stub
		
	}


	protected void onConnect(String line) {
		// TODO Auto-generated method stub
		
	}


	protected void onStatusUpdate(String line) {
		
	}


	protected void onIdleUpdate(String line) {
		
	}


	protected void onFriendsListRCV(String line) {
		
	}


	protected void onBlockListRCV(String line) {
		
	}


	private void exit() {
		System.exit(0);
	}
	
	/**
	 * 	CLEAN MESSAGE
	 * 	
	 * -Takes in the raw message from the server, 
	 * 		and using regex and string replacement, it assigns the 
	 * 		actual message from sender to cleanMessageIn.
	 * 
	 * @param rawMessageIn
	 */
	private void cleanMSG(String in) {
		
			String patt = "<.*?>";
			Pattern pattern =  Pattern.compile(patt);
			Matcher match = pattern.matcher(in);
			in = match.replaceAll("");
			
			String[] parts = in.split(":");
			String b = parts[parts.length-1];


			String patt2 = "â–“";
			Pattern pattern2 =  Pattern.compile(patt2);
			Matcher match2 = pattern2.matcher(b);
			b = match2.replaceAll("");
			
			String patt3 = "â–’";
			Pattern pattern3 =  Pattern.compile(patt3);
			Matcher match3 = pattern3.matcher(b);
			b = match3.replaceAll("");

			b = b.toLowerCase().trim();
			
			b = b.replace("&lt;", "<");
			b = b.replace("&gt;", ">");
			b = b.replace("&quot;", "\"");
			b = b.replace("&apos;", "'");
			b = b.replace("&amp;", "&");
			cleanMessageIn = b;
	}
	
	
	/**
	 * 	GET SENDER
	 * 
	 * -Takes in the raw message from the server and splits it assigning the 
	 * 		sender of the message to sender.
	 * 
	 * @param rawMessageIn
	 */
	private void getSender(String rawMessageIn) {
		String[] parts = rawMessageIn.split("msg:");
		String a = parts[1];
		parts = a.split(":");
		sender = parts[0];
	}
	
	
	/**
	 * 	CHECK COMMANDS
	 * 
	 * -Takes the clean message and checks for a match.
	 * -Responds to the sender.
	 * 
	 * @throws IOException
	 */
	private void checkCommands() throws IOException{
		if(cleanMessageIn.equals("hello") || cleanMessageIn.equals("hey") || cleanMessageIn.equals("yo")|| cleanMessageIn.equals("hi")){
			sendMessage(sender, "Hello "+sender+", Hope you are doing good love!!!");
		}
	}
	
	
	
	/**
	 * Send data to server.
	 * 
	 * @param data
	 * @throws IOException
	 */
	private static void writeToServer(String data) throws IOException{
		writeLock.lock();
		baos = new ByteArrayOutputStream();
		
		if (firstCommand) {
			baos.write(data.getBytes());
			baos.write(0x00);
			
		} else {
			baos.write(data.getBytes());
		}
		sockOutput = new DataOutputStream(socket.getOutputStream());
		channel = Channels.newChannel(sockOutput);
		channel.write(ByteBuffer.wrap(baos.toByteArray()));
		sockOutput.flush();
		writeLock.unlock();
	}
	
	/**
	 * 	SEND MESSAGE
	 * 
	 * -Sends a message to the server.
	 * 
	 * @param name
	 * @param msg
	 * @throws IOException
	 */
	private static void sendMessage(String name , String msg) throws IOException {
		String formatedMSG = "msg:" + name + ":<n7/><m v=\"1\">" + msg + "</m>\r\n";
		writeToServer(formatedMSG);
		Bot.print("[PM] PM Msg Sent : "+name+" : "+msg);
		System.out.println("[PM] PM Msg Sent : "+name+" : "+msg);
	}
	
	/**
	 * SEND LOGIN COMMAND
	 * 
	 * -Sends the first command to the server with AUID and logs in.
	 * 
	 * @param loginAuid
	 * @throws IOException
	 */
	private void sendLoginCommand(String loginAuid) throws IOException {
		writeToServer(loginAuid);
		System.out.println("[PM] Login sent to server : "+loginAuid);
		Bot.print("[PM] Login sent to server : "+loginAuid);
		firstCommand = false;
	}

	
	/**
	 * Send request for freind list
	 * @throws IOException
	 */
	private void sendFriendList() throws IOException {
		writeToServer("wl\r\n");
	}
	
	
	/**
	 * Send the request for the blocklist
	 * @throws IOException
	 */
	private void sendBlockList() throws IOException {
		writeToServer("getblock\r\n");
	}
	
	/**
	 * Block A User
	 * @throws IOException
	 */
	private void block(String user) throws IOException {
		String g = "block:"+user+"\r\n";
		writeToServer(g);
	}
	
	/**
	 * UnBlock A User
	 * @throws IOException
	 */
	private void unBlock(String user) throws IOException {
		String g = "unblock:"+user+"\r\n";
		writeToServer(g);
	}
	
	/**
	 * Remove a friend from list
	 * @throws IOException
	 */
	private void removeFriend(String user) throws IOException {
		String g = "wldelete:"+user+"\r\n";
		writeToServer(g);
	}
	
	/**
	 * Add a friend from list
	 * @throws IOException
	 */
	private void addFriend(String user) throws IOException {
		String g = "wladd:"+user+"\r\n";
		writeToServer(g);
	}
	
	
	
	/**
	 * CREATE SOCKET
	 * 
	 * -Creates a socket.
	 * 
	 * @return socket
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws SocketException
	 */
	private Socket createSocket() throws UnknownHostException, IOException,SocketException {
		socket = new Socket(host, port);
		socket.setKeepAlive(true);
		return socket;
	}

	/**
	 * 	GET AUID
	 * 
	 * -Gets the auid from the headers after opening the website and posting the params.
	 * 
	 * @param connection
	 * @return auid
	 */
	private String getAUID(HttpURLConnection connection) {
		CookieHandler.setDefault(new CookieManager(null,CookiePolicy.ACCEPT_ALL));
		List<String> cookies = connection.getHeaderFields().get(
				"Set-Cookie");
		String headerString = String.valueOf(cookies);
		String[] parts = headerString.split("auth.chatango.com=");
		String a = parts[1];
		parts = a.split(";");
		auid = parts[0];
		return auid;
	}

	
	/**
	 * CHECK RESPONSE CODE
	 * 
	 * -Checks the response code after posting params. 
	 * -The code will be 200 if it went ok.
	 * 
	 * @param connection
	 * @throws IOException
	 */
	private void checkResponseCode(HttpURLConnection connection)throws IOException {
		int code = connection.getResponseCode();
		if (code == 200) {
			String line;
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			while ((line = reader.readLine()) != null) {
				if (line.contains("<div id=errorbox>")) {
					String msg = reader.readLine().trim();
					reader.close();
					Bot.print("[ERROR] "+msg);
					System.out.println("[ERROR] "+msg);
				}
			}
			reader.close();
		} else {
			Bot.print("[ERROR] Invalid response code: " + code);
			System.out.println("[ERROR] Invalid response code: " + code);
		}
	}

	
	
	/**
	 * POST PARAMS
	 * 
	 * -Posts the params to the login website to get the headers that contain the auid.
	 * 
	 * @param params
	 * @param connection
	 * @throws IOException
	 */
	private void postParams(String params, HttpURLConnection connection)throws IOException {
		DataOutputStream output = new DataOutputStream(connection.getOutputStream());
		output.writeBytes(params);
		output.flush();
		output.close();
	}

	
	
	/**
	 * 	SET UP CONNECTION
	 * 
	 * -Open the connection to the url.
	 * -Sets properties ect.
	 * -Sets up connection to do output and input
	 * 
	 * @param params
	 * @return HttpURLConnection
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
	private HttpURLConnection setUpConnection(String params)throws MalformedURLException, IOException, ProtocolException {
		URL url2 = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.connect();
		return connection;
	}

	
	/**
	 * FORMAT PARAMS
	 * 
	 * -Formats the params to be posted to the login site.
	 * 
	 * @return params
	 * @throws UnsupportedEncodingException
	 */
	private String formatParams() throws UnsupportedEncodingException {
		String params = String.format(
				"user_id=%s&password=%s&storecookie=%s&checkerrors=%s",
				URLEncoder.encode(user_id, charset),
				URLEncoder.encode(password, charset),
				URLEncoder.encode(storecookie, charset),
				URLEncoder.encode(checkerrors, charset));
		return params;
	}

	
	/**
	 * Console Commands - commands entered into the bots GUI. 
	 * All console commands must start with /cmd: 
	 * followed by the command: 
	 * and then the single or multiple arguments all parts seperated by :
	 * 
	 * @param text
	 * @throws IOException 
	 */
	public static void consoleCommands(String text) throws IOException {
		
		//lower and trim text//
		text = text.toLowerCase().trim();
		
		//check if valid cmd - contains ://
		if(!text.contains(":")){
			Bot.print("[Console CMD] Error : invalid console cmd");
			System.out.println("[Console CMD] Error : invalid console cmd");
			return;//return//
		}
		
		//split text at : after checking if it contains ://
		String[] parts = text.split(":");
		
		//cmd and args//
		String conCmd = "";
		String conArgs = "";
		String conArgs2 = "";
		
		System.out.println("[Console CMD Debug] PM: "+Arrays.toString(parts)+parts.length);
		
		//check if valid cmd - lenght of cmd less than 3 parts//
		if(parts.length < 2){
			Bot.print("[Console CMD] Error : invalid console cmd");
			System.out.println("[Console CMD] Error : invalid console cmd");
			return;//return//
		}
		
		//if command is a no args command//
		if(parts.length == 2){
			conCmd = parts[1];//command//
		}
		
		//if command is a single args command//
		if(parts.length == 3){
			conCmd = parts[1];//command//
			conArgs = parts[2];//first arg//
		}
		
		//if command is a multi args command//
		if(parts.length == 4){
			conCmd = parts[1];//command//
			conArgs = parts[2];//first args//
			conArgs2 = parts[3];//second args//
		}
		
		////////////////////////////////////////////
		//Console commands/////////////////////////
		
		//add a friend//
		if(conCmd.trim().equals("addfriend")&& !conArgs.equals("")){
			
		}	
		//remove a friend//
		if(conCmd.trim().equals("removefriend")&& !conArgs.equals("")){
			
		}
		//block a user//
		if(conCmd.trim().equals("block")&& !conArgs.equals("")){
			
		}
		//unblock a user//
		if(conCmd.trim().equals("unblock")&& !conArgs.equals("")){
			
		}
		//get friend list//
		if(conCmd.trim().equals("getfriendlist")){
			Bot.print("[Console CMD] Friend List : " + freinds);
			System.out.println("[Console CMD] Friend List : " + freinds);
		}
		//get block list//
		if(conCmd.trim().equals("getblocklist")){
			Bot.print("[Console CMD] Block List : " + blocklist);
			System.out.println("[Console CMD] Block List : " + blocklist);
		}
		//Send a pm to a user//
		if(conCmd.trim().equals("pm")&& !conArgs.equals("") && !conArgs2.equals("")){
			sendMessage(conArgs , conArgs2);
		}
		if(conCmd.trim().equals("search")){
			writeToServer("r=500&la=%2D93%2E705600&lo=39%2E03310&ama=99&ami=17&s=B&t=15&f=0"+"\r\n");
		}
	}


}
