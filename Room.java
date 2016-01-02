package bot;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
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
public class Room {

	
	/**
	 * Server, port, and socket
	 */
	String host = "";
	private String uid = "";
	int port = 5228;
	static Socket socket;

	/**
	 * Default Room name
	 */
	String roomame = "digitalmasterminds69";

	
	/**
	 * Input and output - for reading and writing to and from server
	 */
	static ByteArrayOutputStream baos;
	static DataOutputStream sockOutput;
	static WritableByteChannel channel;
	BufferedReader inputReader;
	
	/**
	 * Write Lock - One Write or read at a time
	 */
	private final static Lock writeLock = new ReentrantLock();
	

	/**
	 * Used for the ping task when bot is idle
	 */
    private final ScheduledExecutorService executorService =
    		Executors.newSingleThreadScheduledExecutor();
    
    
    /**
     * default credentals
     */
	private String user_id = "mentalencryption";
	private String password = "YouKnowBetterThanToThinkThisIsThePassword=?";

	
	/**
	 * used in logging in as anon or with account 
	 * 0 = default , 1 = anon , 2 = account 
	 */
	int x = 0;
	
	/**
	 * First Command boolean
	 */
	static boolean firstCommand = true;
	
	/**
	 * Server weights
	 */
	static LinkedHashMap<String,Integer> _chatangoTagserver=new LinkedHashMap<String, Integer>();
	private int sv10 = 110;
	private int sv12=116;
	private int w12=75;
	private int sv8=101;
	private int sv6=104;
	private int sv4=110;
	private int sv2=95;
	

	/**
	 * Chat room info
	 */
	private static ArrayList<String> mods = new ArrayList<String>();
	private static String owner;
	private static ArrayList<String> participants = new ArrayList<String>();
	private static String count;
	static ArrayList<String> banlist = new ArrayList<String>();
	private boolean useBG = true;
	
	
	/**
	 * Font face, color, size
	 * Name color
	 */
	private static String fontFace = "Adobe Garamond Pro";
	private static String fontSize = "14";
	private static String fontColor = "fff";
	private static String nameColor = "093";
	
	/**
	 * Used when flood ban / warning
	 */
	static boolean quiet = false;
	
	
	/**
	 * Default constructor ***unused***
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Room() throws UnknownHostException, IOException {
		connect();
	}
	
	
	/**
	 * Constructor to login as anon
	 * @param roomS
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Room(String roomS) throws UnknownHostException, IOException{
		roomame = roomS;
		x = 1;
		connect();
	}

	
	/**
	 * Constructor to login with account
	 * @param roomS
	 * @param userS
	 * @param passS
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Room(String roomS, String userS, String passS) throws UnknownHostException, IOException{
		roomame = roomS;
		user_id = userS;
		password = passS;
		x = 2;
		connect();
	}

	/**
	 * Connect to chatroom
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void connect() throws UnknownHostException, IOException{
		
		//Tag server weights//
		_chatangoTagserver.put("5", w12);
		_chatangoTagserver.put("6", w12);
		_chatangoTagserver.put("7", w12);
		_chatangoTagserver.put("8", w12);
		_chatangoTagserver.put("16", w12);
		_chatangoTagserver.put("17", w12);
		_chatangoTagserver.put("18", w12);
		_chatangoTagserver.put("9", sv2);
		_chatangoTagserver.put("11", sv2);
		_chatangoTagserver.put("12", sv2);
		_chatangoTagserver.put("13", sv2);
		_chatangoTagserver.put("14", sv2);
		_chatangoTagserver.put("15", sv2);
		_chatangoTagserver.put("19", sv4);
		_chatangoTagserver.put("23", sv4);
		_chatangoTagserver.put("24", sv4);
		_chatangoTagserver.put("25", sv4);
		_chatangoTagserver.put("26", sv4);
		_chatangoTagserver.put("28", sv6);
		_chatangoTagserver.put("29", sv6);
		_chatangoTagserver.put("30", sv6);
		_chatangoTagserver.put("31", sv6);
		_chatangoTagserver.put("32", sv6);
		_chatangoTagserver.put("33", sv6);
		_chatangoTagserver.put("35", sv8);
		_chatangoTagserver.put("36", sv8);
		_chatangoTagserver.put("37", sv8);
		_chatangoTagserver.put("38", sv8);
		_chatangoTagserver.put("39", sv8);
		_chatangoTagserver.put("40", sv8);
		_chatangoTagserver.put("41", sv8);
		_chatangoTagserver.put("42", sv8);
		_chatangoTagserver.put("43", sv8);
		_chatangoTagserver.put("44", sv8);
		_chatangoTagserver.put("45", sv8);
		_chatangoTagserver.put("46", sv8);
		_chatangoTagserver.put("47", sv8);
		_chatangoTagserver.put("48", sv8);
		_chatangoTagserver.put("49", sv8);
		_chatangoTagserver.put("50", sv8);
		_chatangoTagserver.put("52", sv10);
		_chatangoTagserver.put("53", sv10);
		_chatangoTagserver.put("55", sv10);
		_chatangoTagserver.put("57", sv10);
		_chatangoTagserver.put("58", sv10);
		_chatangoTagserver.put("59", sv10);
		_chatangoTagserver.put("60", sv10);
		_chatangoTagserver.put("61", sv10);
		_chatangoTagserver.put("62", sv10);
		_chatangoTagserver.put("63", sv10);
		_chatangoTagserver.put("64", sv10);
		_chatangoTagserver.put("65", sv10);
		_chatangoTagserver.put("66", sv10);
		_chatangoTagserver.put("68", sv2);
		_chatangoTagserver.put("71", sv12);
		_chatangoTagserver.put("72", sv12);
		_chatangoTagserver.put("73", sv12);
		_chatangoTagserver.put("74", sv12);
		_chatangoTagserver.put("75", sv12);
		_chatangoTagserver.put("76", sv12);
		_chatangoTagserver.put("77", sv12);
		_chatangoTagserver.put("78", sv12);
		_chatangoTagserver.put("79", sv12);
		_chatangoTagserver.put("80", sv12);
		_chatangoTagserver.put("81", sv12);
		_chatangoTagserver.put("82", sv12);
		_chatangoTagserver.put("83", sv12);
		_chatangoTagserver.put("84", sv12);
		
		System.out.println("[Room]Tagserver Weights : "+_chatangoTagserver);

		
		//user id//
		uid = generateUID();
		
		//socket//
		host = "s"+getServerId(roomame)+".chatango.com";
		System.out.println("[Room] Host : "+host);
		Bot.print("[Room] Host : "+host);
		socket = createSocket(host, port);
		
		//start reading loop - response from server//
		readResponse();
		
		//Start login//
		String loginStr = "";
		
		//default//
		if(x == 1){
			loginStr = "bauth:"+roomame+":"+ uid;
		}
		//login as anon//
		if(x == 2){
			loginStr = "bauth:"+roomame+":"+ uid+":"+ user_id+":"+ password;
		}
		
		//Login with account//
		if(x == 0){
			loginStr = "bauth:"+roomame+":"+ uid+":"+ user_id+":"+ password;
		}
		
		//Send login command//
		sendLoginCommand(loginStr);
		
		//Start idle ping//
		startIdlePing();
		
		//Delay before posting msg to chatroom//
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Post login msg to chatroom//
		sendMsg("Hello, I've logged in successfully. ");

	}
	

	/**
	 * Read response from server - Loop
	 * @throws IOException
	 */
	private void readResponse() throws IOException {
		//Read Loop Thread//
		Thread readThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//Innput reader and channel/
					DataInputStream sockInput = new DataInputStream(socket.getInputStream());
					ReadableByteChannel readChannel = Channels.newChannel(sockInput);

			        while (true) {
			            // see if any message has been received//
			            ByteBuffer bufferA = ByteBuffer.allocate(10000000);
			            
			            @SuppressWarnings("unused")
						int count = 0;
			            
			            //empty message//
			            String message = "";
			            
			            //Read bytes//
			            while ((count = readChannel.read(bufferA)) > 0) {
			                // flip the byte buffer to start reading//
			                bufferA.flip();
			                //decode bytes and add each char-construct message//
			                message += Charset.defaultCharset().decode(bufferA);
			 
			            }
			            //check if message had data//
			            if (message.length() > 0 && !message.trim().isEmpty()) {
			            	
			            	///////////////////////////////////////
			            	//SERVER COMMANDS/////////////////////
			            	
			            	//on login//
			            	if(message.trim().toLowerCase().startsWith("ok:")){
			            		onConnectRcv(message);
			            	}
			            	
			            	//on login fail//
			            	if(message.trim().toLowerCase().startsWith("denied:")){
			            		onConnectFailRcv();
			            	}
			            	
			            	//On init//
			            	if(message.trim().toLowerCase().contains("inited")){
			            		onInitRcv(message);
			            	}
			            	
			            	//message//
			                if(message.trim().toLowerCase().startsWith("b:")){
				                String clean = cleanMSG(message);
				                String sender = getSender(message);
				                Bot.print("[Room message] "+sender+" : "+clean);
				                System.out.println("[Room message] "+sender+" : "+clean);
				                checkCmd(clean,sender);
			                }  
			                //group count
			                if(message.trim().toLowerCase().startsWith("n:")){
			                	onCountRcv(message);
			                }
			                //mod names
			                if(message.trim().toLowerCase().startsWith("mods:")){
			                	onModsRcv(message);
			                }
			                //on leave
			                if(message.trim().toLowerCase().startsWith("participant:0")){
			                	onLeaveRcv(message);
			                }
			                //on join
			                if(message.trim().toLowerCase().startsWith("participant:1")){
			                	onJoinRcv(message);
			                }
			                //group names
			                if(message.trim().toLowerCase().startsWith("g_participants:")){
			                	onRoomNamesRcv(message);
			                }
			                //banned
			                if(message.trim().toLowerCase().startsWith("blocked:")){
			                	onBanRcv(message);
			                }
			                //unbanned
			                if(message.trim().toLowerCase().startsWith("unblocked:")){
			                	onUnbanRcv(message);
			                }
			                //past msgs
			                if(message.trim().toLowerCase().startsWith("i:")){
			                	onPastMsgRcv(message);
			                }
			                //Ban List
			                if(message.trim().toLowerCase().startsWith("blocklist:")){
			                	onBlocklistRcv(message);
			                }
			                //message deleted
			                if(message.trim().toLowerCase().startsWith("delete:")){
			                	onMsgDeleteRcv(message);
			                }
			                //Flood Warning//
			                if(message.trim().toLowerCase().startsWith("show_fw:")){
			                	onFloodWarningRcv(message);
			                }
			                //Flood Ban//
			                if(message.trim().toLowerCase().startsWith("show_tb:")){
			                	onFloodBanRcv(message);
			                }
			                //Flood Ban repeat//
			                if(message.trim().toLowerCase().startsWith("tb:")){
			                	onFloodBanRepeatRcv(message);
			                }
			                //premium//
			                if(message.trim().toLowerCase().startsWith("premium:")){
			                	onPremiumRcv(message);
			                }
			                //on all messages delete//
			                if(message.trim().toLowerCase().startsWith("deleteall:")){
			                	onAllMsgDeleteRcv(message);
			                }
			                
			                //System.out.println("[Room] Room debug : "+message);
			                //Bot.print("[Room] Room debug : "+message);
			                
			                //Reset message//
			                message = "";
			                
			                
			            }
			        }
				} catch (IOException e) {
					e.printStackTrace();
				}
			}


		});
		readThread.start();
	}
	
	////////////////////////////////////////
	//SERVER COMMAND EVENTS////////////////
	
	
	/**
	 * Called When all of a users messages are deleted
	 * @param message
	 * @throws IOException
	 */
	protected void onAllMsgDeleteRcv(String message) throws IOException {
		System.out.println("[Room] All MSG Deleted : "+message);
		Bot.print("[Room] All MSG Deleted : "+message);
		sendMsg("All Clear ^.^");
	}

	
	/**
	 * On Premium 
	 * @param message
	 * @throws IOException
	 */
	protected void onPremiumRcv(String message) throws IOException {

		String bgTime = message.split(":")[2];

		Timestamp currentTimeMil = new Timestamp(System.currentTimeMillis());
		
		long g = Long.parseLong(bgTime.trim());
		Timestamp bgEnd = new Timestamp(g*1000);

		//test if user accounnt has time left on premium//
		if(bgEnd.getTime() > currentTimeMil.getTime() &&useBG ){
			System.out.println("[Room] Using premium : "+message);
			Bot.print("[Room] Using premium : "+message);
			setBG();//set bg//
			setMedia();//set media//
		}else{
			System.out.println("[Room] Not using premium : ");
			Bot.print("[Room] Not using premium : "+message);
		}
	}

	/**
	 * On Flood Ban Repeat
	 * @param message
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	protected void onFloodBanRepeatRcv(String message) throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("[Room] Flood Ban Repeat : "+message);
		Bot.print("[Room] Flood Ban Repeat : "+message);
		if(quiet == false){
			quiet = true;
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(120000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}finally{
						quiet = false;
						System.out.println("[Room] Flood Banned : Over ");
						try {
							Bot.print("[Room] Flood Banned : Over");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					
				}
			});
			t.start();
		}
	}

	/**
	 * On Flood ban
	 * @param message
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	protected void onFloodBanRcv(String message) throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("[Room] Flood Ban : "+message);
		Bot.print("[Room] Flood Ban : "+message);
		if(quiet == false){
			quiet = true;
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(500000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}finally{
						quiet = false;
						System.out.println("[Room] Flood Banned : Over ");
						try {
							Bot.print("[Room] Flood Banned : Over");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					
				}
			});
			t.start();
		}
	}

	/**
	 * On Flood Ban Warning
	 * @param message
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	protected void onFloodWarningRcv(String message) throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("[Room] Flood Warning : "+message);
		Bot.print("[Room] Flood Warning : "+message);
		if(quiet == false){
			quiet = true;
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(120000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}finally{
						quiet = false;
						System.out.println("[Room] Flood Warning : Over ");
						try {
							Bot.print("[Room] Flood Warning : Over");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					
				}
			});
			t.start();
		}
	}

	/**
	 * On Message delete
	 * @param message
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	protected void onMsgDeleteRcv(String message) throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("[Room] Message Deleted : "+message);
		Bot.print("[Room] Message Deleted : "+message);
	}

	/**
	 * On User Join
	 * @param message
	 * @throws IOException
	 */
	protected void onJoinRcv(String message) throws IOException {
		String user = message.split(":")[4];
		System.out.println("[Room] Joined : "+user);
		Bot.print("[Room] Joined : "+user);
		sendMsg("Welcome back "+user);
		participants.add(user);
	}
	
	/**
	 * On User Leave
	 * @param message
	 * @throws IOException
	 */
	private void onLeaveRcv(String message) throws IOException {
		if(message.contains(":")){
			String user = message.split(":")[4];
			System.out.println("[Room] Left : "+user);
			Bot.print("[Room] left : "+user);
			sendMsg("Bye "+user);
			participants.remove(user);
		}

	}
	
	/**
	 * On Block List Recive
	 * @param message
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	protected void onBlocklistRcv(String message) throws FileNotFoundException, UnsupportedEncodingException {
		message = message.replace("blocklist:", "");
		//check if list is empty//
		if(message.contains(";")&&message.contains(":")){
			String[] parts = message.split(";");
			for(int i = 0;i < parts.length;i++){
				banlist.add(parts[i].split(":")[2]);
			}
		}
		System.out.println("[Room] Block List : "+banlist);
		Bot.print("[Room] Block List : "+banlist);
	}

	/**
	 * On Init
	 * @param message
	 * @throws IOException
	 */
	protected void onInitRcv(String message) throws IOException {
		getPremium();
		getRoomUsers();
		getBlockList();
	}

	/**
	 * On Connect Fail
	 */
	protected void onConnectFailRcv() {
		System.out.println("[Room] Error : Connect failled");
	}
	
	/**
	 * On Login Fail
	 */
	protected void onLoginFail() {
		System.out.println("[Room] Error : login failled");
	}
	

	/**
	 * On Connect
	 * @param message
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	protected void onConnectRcv(String message) throws FileNotFoundException, UnsupportedEncodingException {
		//check if empty//
		if(message.contains(":")){
			//Get Owner//
			owner = message.split(":")[1];
			System.out.println("[Room] Owner : "+owner);
			Bot.print("[Room] Owner : "+owner);
		}
		//Get Mods//
		if(!message.contains("M:")){
			onLoginFail();
			return;
		}
		try{
			String[] msgPartsB = message.split("M:");
			String[] msgPartsB2 = msgPartsB[1].split(":");
			String[] modsFull = msgPartsB2[3].split(";");
			for(int m=0; m < modsFull.length;m++){
				mods.add(modsFull[m].split(",")[0]);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("[Room] Mods : "+mods);
		Bot.print("[Room] Mods : "+mods);
	}

	/**
	 * On Past Messages
	 * @param message
	 */
	protected void onPastMsgRcv(String message) {
		//Currently Unused//
	}


	/**
	 * On Unban
	 * @param message
	 * @throws IOException
	 */
	protected void onUnbanRcv(String message) throws IOException {
		String banned = message.split(":")[3];
		String banner = message.split(":")[4];
		banlist.remove(banned);
		System.out.println("[Room] Unban : "+banned+" By : "+banner);
		Bot.print("[Room] Unban : "+banned+" By : "+banner);
		sendMsg("UnBan : "+banned+" __ By :"+banner);
	}

	/**
	 * On Ban
	 * @param message
	 * @throws IOException
	 */
	protected void onBanRcv(String message) throws IOException {
		String banned = message.split(":")[3];
		String banner = message.split(":")[4];
		banlist.add(banned);
		System.out.println("[Room] Ban : "+banned+" By : "+banner);
		Bot.print("[Room] Ban : "+banned+" By : "+banner);
		sendMsg("Banned : "+banned+" __ By :"+banner);
	}

	/**
	 * A mod has been added or removed
	 * @param message
	 * @throws IOException
	 */
	protected void onModsRcv(String message) throws IOException {
		
		message = message.replace("mods:", "");
		if(!message.contains(":")){
			return;
		}
		String[] parts = message.split(":");
		
		ArrayList<String> j = new ArrayList<String>();
		
		for(int m=0; m < parts.length;m++){
			j.add(parts[m].split(",")[0]);
		}
		if(mods.size() < j.size()){
			mods = j;
			sendMsg("A mod has been removed ___ Mods : "+mods);
			System.out.println("[Room] Mod Removed : "+mods);
			Bot.print("[Room] Mod Removed : "+mods);
		}
		if(mods.size() > j.size()){
			mods= j;
			sendMsg("A mod has been added ___ Mods : "+mods);
			System.out.println("[Room] Mod added : "+mods);
			Bot.print("[Room] Mod added : "+mods);
		}
		

	}


	/**
	 * Chat room participants
	 * @param message
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	protected void onRoomNamesRcv(String message) throws FileNotFoundException, UnsupportedEncodingException {
		message = message.replace("g_participants:","");
		
		String[] parts = message.split(";");

		for(int i = 0;i<parts.length;i++){
			participants.add(parts[i].split(":")[3]);
		}
		System.out.println("[Room] Participants : "+participants);
		Bot.print("[Room] Participants : "+participants);
	}


	
	/**
	 * Chat room count
	 * @param message
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	protected void onCountRcv(String message) throws FileNotFoundException, UnsupportedEncodingException {	
		count = message.split("n:")[1];
		System.out.println("[Room message] Room Count : "+count);
		Bot.print("[Room message] Room Count : "+count);
	}
	

	//////////////////////////////
	//Bot Commands///////////////
	
	/**
	 * Commands 
	 * 
	 * @param clean
	 * @param sender
	 * @throws IOException
	 */
	private void checkCmd(String clean, String sender) throws IOException {
		
		//Check if ~ is used and return if not//
		if(!clean.startsWith("~")){
			return;
		}
		
		//Trim and lowercase message//
		String cleanB = clean.toLowerCase().trim();
		
		//Getting cmd and arguments//
		String cmd = "";
		String args = "";
		if(cleanB.contains(" ")){
			String[] msgArray = cleanB.split(" ");
			if(msgArray.length > 1){
				cmd = msgArray[0].trim();
				args = msgArray[1].trim();
			}
		}else{
			cmd = cleanB;
			args= "";
		}
		
		if(cmd.equals("~cmd")){
			sendMsg("My commands : ~cmd, ~hello/hi/hey/yo, ~mods, ~count, ~chatlist, ~hug ");
		}
		
		if(cmd.equals("~hello") || cmd.equals("~hey") || cmd.equals("~yo")|| cmd.equals("~hi")){
			String g = "Hello "+sender+", Hope you are doing good love!!!";
			sendMsg(g);
		}
		
		if(cmd.equals("~mods") ){
			sendMsg("The Owner is : "+owner+". The Mods are : "+mods);
		}
		
		if(cmd.equals("~count") ){
			sendMsg("The room count is : "+count);
		}
		
		if(cmd.equals("~chatlist") ){
			sendMsg("The people here are : "+participants);
		}
		
		if(cmd.equals("~hug") && !args.equals("")){
			sendMsg("* Hugs "+args+" *");
		}else if(cmd.equals("~hug")&& args.equals("")){
			sendMsg("* Hugs "+sender+" *");
		}
		
//		if(cmd.equals("~join") && !args.equals("")){
//			Bot.joinRoom(args, user_id, password);
//		}
	}
	
	
	/////////////////////////////
	//Message Utilities/////////
	
	/**
	 * Get message sender.
	 * 
	 * @param message
	 * @return
	 */
	protected String getSender(String message) {
		String[] parts = message.split("b:");
		String a = parts[1];
		parts = a.split(":");
		return parts[1];
	}

	/**
	 * Clean message
	 * 
	 * @param in
	 * @return
	 */
	private String cleanMSG(String in) {
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
		return b;
	}
    
	
	
	/////////////////////////////////
	//Connection Utilities//////////
	
    /**
     * Create a new Socket.
     * 
     * @param host2
     * @param port2
     * @return
     * @throws UnknownHostException
     * @throws IOException
     */
	private Socket createSocket(String host2, int port2) throws UnknownHostException, IOException {
		socket = new Socket(host2,port2);
		socket.setKeepAlive(true);
		return socket;	
	}
	
	/**
	 * Get the server Id
	 * @param name
	 * @return
	 */
    private static String getServerId(String name) {
    	
    	//replace  [^0-9a-z] with q
        name = name.toLowerCase().replace("-", "q").replace("_","q");
        
        
        
        //get the minimum number _ 5 or if name.length() _ whatever is smallest
        int mins = Math.min(5, name.length());
        
        //get a substring starting at 0 place and ending at mins 
        //example: 0 - 5 of string digitalmasterminds = digit
        String substr = name.substring(0, mins);
        
        //cast big int .intvalue() with string value 
        //of substr and radix of 36 to float
        float fnv = (float) new BigInteger(substr, 36).intValue();
        
        int lnv = 1000;
        //if name length larger than 6
        if(name.length() > 6) {
        	//basicly the same as above in one line
            lnv = new BigInteger(name.substring(6, 6 + Math.min(3, name.length() - 5)), 36).intValue();
            //get the max number _ 1000 or lnv _ whatever is largest
            lnv = Math.max(lnv, 1000);
        }
        
        //num = mod of fnv and lnv divided by lnv
        float num = (fnv % lnv) / lnv;

        
        //variable to iterate map
	    Iterator<Entry<String, Integer>> it = _chatangoTagserver.entrySet().iterator();
	    
	    //maxnum starting point
	    int maxnum=0;

	    //loop while map has data left
	    while (it.hasNext()) {
	    	//set entry
	        Map.Entry<String, Integer> pair = it.next();
	        //add up entry values
	        maxnum = maxnum + (int)pair.getValue();
	        
	    }  
	    
        float sumfreq = 0;//sum
        String sn = "";//serv num
        
        //map iterator
        Iterator<Entry<String, Integer>> it2 = _chatangoTagserver.entrySet().iterator();
        
        //loop map
        while(it2.hasNext()) {
        	
        	//set entry
        	Map.Entry<String, Integer> pair2 = it2.next();
        	
        	//add up pair value / maxnum
            sumfreq += ((float) pair2.getValue()) / maxnum;
            
            //if num <= sumfreq
            if(num <= sumfreq) {
            	//parse key
                sn = pair2.getKey();
                break;
            }
        }
        //return server number
        return sn;
    }


    
	/**
	 * GENERATE UID
	 * 
	 * -Generate a UID with 16 characters
	 * 
	 * @return id
	 */
	private String generateUID() {
		String id = "";
        Random random = new Random();
        while(id.length() < 16) {
            id += random.nextInt(10);
        }
		return id;
	}
		
	
	//////////////////////////////////
	//Send To Server Utilities///////
	
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
	 * Idle ping
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
	 * Send Ping.
	 * 
	 * @throws IOException
	 */
	private void sendPing() throws IOException {
		writeToServer("\r\n");
	}
	
	/**
	 * Send login command.
	 * 
	 * @param login
	 * @throws IOException
	 */
	private void sendLoginCommand(String login) throws IOException {
		writeToServer(login);
		Bot.print("[Room] Login sent to server : "+baos);
		System.out.println("[Room] Login sent to server : "+baos);
		firstCommand = false;
	}

	/**
	 * Post a message to the chat.
	 * 
	 * @param msg
	 * @throws IOException
	 */
	private static void sendMsg(String msg) throws IOException {
		if(quiet){
			return;
		}
		if(!quiet){
			msg = "bmsg:tl2r:"+"<n"+nameColor+"/><f x"+fontSize+fontColor+"=\""+fontFace+"\">"+msg+"\r\n";
			writeToServer(msg);
		}
	}

	/**
	 * Get Blocklist
	 * @throws IOException
	 */
	private void getBlockList() throws IOException {
		writeToServer("blocklist:block::next:500\r\n");
	}

	/**
	 * Get Premium
	 * @throws IOException
	 */
	private void getPremium() throws IOException {
		writeToServer("getpremium:1\r\n");
	}

	
	/**
	 * Set Media - Video/Audio Recording
	 * @throws IOException
	 */
	private void setMedia() throws IOException {
		writeToServer("msgmedia:1\r\n");
	}

	/**
	 * Get Room Users
	 * @throws IOException
	 */
	private void getRoomUsers() throws IOException {
		writeToServer("g_participants:start\r\n");
	}

	/**
	 * Set to use the BG
	 * @throws IOException
	 */
	private void setBG() throws IOException {
		writeToServer("msgbg:1\r\n");
	}
	
	/**
	 * Add Mod
	 * @param name
	 * @throws IOException
	 */
	private void addMod(String name) throws IOException{
		writeToServer("addmod:"+name+"\r\n");
	}
	
	/**
	 * Remove Mod
	 * @param name
	 * @throws IOException
	 */
	private void removeMod(String name) throws IOException{
		writeToServer("removemod:"+name+"\r\n");
	}
	
	/**
	 * Ban a User
	 * @param userID
	 * @param ip
	 * @param name
	 * @throws IOException
	 */
	private void ban(String userID,String ip, String name) throws IOException{
		writeToServer("block:"+userID+":"+ip+":"+name+"\r\n");
	}
	
	/**
	 * Unban a User
	 * @param userID
	 * @param ip
	 * @param name
	 * @throws IOException
	 */
	private void unBan(String userID,String ip, String name) throws IOException{
		writeToServer("removeblock:"+userID+":"+ip+":"+name+"\r\n");
	}
	
	/**
	 * Delete a message
	 * @param msgId
	 * @throws IOException
	 */
	private void deleteMsg(String msgId) throws IOException{
		writeToServer("delmsg:"+msgId+"\r\n");
	}
	
	/**
	 * Delete all a users messages
	 * @param userId
	 * @throws IOException
	 */
	private void deleteUserAllMsg(String userId) throws IOException{
		writeToServer("delallmsg:"+userId+"\r\n");
	}

	/**
	 * Flag a user
	 * @param msgId
	 * @throws IOException
	 */
	private void flag(String msgId) throws IOException{
		writeToServer("g_flag:"+msgId+"\r\n");
	}
	
	/**
	 * Flag a user
	 * @param msgId
	 * @throws IOException
	 */
	private void clearAllMsg() throws IOException{
		writeToServer("clearall:\r\n");
	}
	
	/**
	 * Send a message from the GUI or use a console command
	 * @param text
	 * @throws IOException
	 */
	public static void sendFieldText(String text) throws IOException {
		//If text starts with /cmd: - use a console command//
		if(text.toLowerCase().trim().startsWith("/cmd")){
			//Use console command//
			consoleCommands(text);
		}else{
			//If not a console cmd post msg in all chatrooms//
			sendMsg(text);
		}
	}

	
	/**
	 * Console Commands - commands entered into the bots GUI. 
	 * All console commands must start with /cmd: 
	 * followed by the command: 
	 * and then the single or multiple arguments all parts seperated by :
	 * 
	 * @param text
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private static void consoleCommands(String text) throws FileNotFoundException, UnsupportedEncodingException {
		
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
		
		System.out.println("[Console CMD Debug] Room: "+Arrays.toString(parts)+parts.length);
		
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
		
		//set quiet//
		if(conCmd.trim().equals("quiet") && !conArgs.equals("")){
			if(conArgs.equals("true")){
				quiet = true;
				Bot.print("[Console CMD] Quiet : " + quiet);
				System.out.println("[Console CMD] Quiet : " + quiet);
			}else if(conArgs.equals("false")){
				quiet = false;
				Bot.print("[Console CMD] Quiet : " + quiet);
				System.out.println("[Console CMD] Quiet : " + quiet);
			}
		}
		//set font color//
		if(conCmd.trim().equals("fontcolor") && !conArgs.equals("")){
			fontColor = conArgs;
		}
		//set font face//
		if(conCmd.trim().equals("fontface") && !conArgs.equals("")){
			fontFace = conArgs;
		}	
		//set fornt size//
		if(conCmd.trim().equals("fontsize") && !conArgs.equals("")){
			fontSize = conArgs;
		}	
		//set name color//
		if(conCmd.trim().equals("namecolor")&& !conArgs.equals("")){
			nameColor = conArgs;
		}	
		//add a mod//
		if(conCmd.trim().equals("addmod")&& !conArgs.equals("")){
			
		}	
		//remove a mod//
		if(conCmd.trim().equals("removemod")&& !conArgs.equals("")){
			
		}
		//ban a user//
		if(conCmd.trim().equals("ban")&& !conArgs.equals("")){
			
		}
		//unban a user//
		if(conCmd.trim().equals("unban")&& !conArgs.equals("")){
			
		}
		if(conCmd.trim().equals("join")&& !conArgs.equals("")){
			
		}
		if(conCmd.trim().equals("leave")&& !conArgs.equals("")){
			
		}
		if(conCmd.trim().equals("post")&& !conArgs.equals("") && !conArgs2.equals("")){
			
		}
		//get banlist//
		if(conCmd.trim().equals("getbanlist")){
			Bot.print("[Console CMD] BanList : " + banlist);
			System.out.println("[Console CMD] BanList : " + banlist);
		}
		//get mods//
		if(conCmd.trim().equals("getmods")){
			Bot.print("[Console CMD] Mods : " + mods);
			System.out.println("[Console CMD] Mods : " + mods);
		}
		//get owner//
		if(conCmd.trim().equals("getowner")){
			Bot.print("[Console CMD] Owner : " + owner);
			System.out.println("[Console CMD] Owner : " + owner);
		}
		//get count//
		if(conCmd.trim().equals("getcount")){
			Bot.print("[Console CMD] Count : " + count);
			System.out.println("[Console CMD] Count : " + count);
		}
		//get chatlist//
		if(conCmd.trim().equals("getchatlist")){
			Bot.print("[Console CMD] Chat List : " + participants);
			System.out.println("[Console CMD] Chat List : " + participants);
		}
	}


}
