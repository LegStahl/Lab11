import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.io.*;
public class User{
	
	public static boolean onChat = true;
	
	public static String NAME;
	public static void main(String[] args){
		try{
			System.out.print("Hello! Welcome to the chat!");
			


			System.out.print("\nEnter your name: ");

			BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));

			NAME = fromUser.readLine();

			SendMessage sender = new SendMessage(args[0], Integer.parseInt(args[1]));

			sender.start();
		}catch(Exception mess){
			System.out.println(mess.getMessage());
		}
	}
	
}

class SendMessage extends Thread{
	
	public static int serverPort;


	private DatagramSocket clientSocket;

	public static InetAddress ipAddressSend;

	private byte[] sendData;

	private String sentence;

	private String address;
	
	private DatagramPacket packet;

	public SendMessage(String address, int serverPort){
		
		this.serverPort = serverPort;
		
		this.address = address;

	
	}

	public String messageSend() throws Exception {
		
		BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
		String sentence = fromUser.readLine();
		
		if(sentence.equals("end conversation")) {
			User.onChat = false;
		
		}
	
		sentence = User.NAME + ": " + sentence;
		return sentence;
	}

	public void run(){
		try{
		
		
		System.out.println("If you want to leave the chat, enter end conversation");
		
		clientSocket = new DatagramSocket();
		
		Receiver receiver = new Receiver(clientSocket);
		
		receiver.start();
		
		ipAddressSend = InetAddress.getByName(address);
			while(User.onChat){
				sentence = messageSend();
				
				sendData = new byte[1024];
	
				sendData = sentence.getBytes();
				
				packet = new DatagramPacket(sendData, sendData.length, ipAddressSend, serverPort);
				if(User.onChat)
					clientSocket.send(packet); 
	
			}
			System.out.println("You have ended conversation");
			//Thread.sleep(100);
		}catch(Exception mess){
			System.out.println(mess.getMessage());
			//clientSocket.close();
		}finally{
			
			clientSocket.close();
		}
	}
	
}

class Receiver extends Thread {
	
	private DatagramSocket client;
	
	private byte[] receiveData;

	private DatagramPacket packet;

	private String curPath;

	public Receiver(DatagramSocket client){
		super();
		
		this.client = client;
		
		
	}
	
	public void run() {
		try {
		int index = 0;
		curPath = new java.io.File(".").getCanonicalPath();
		receiveData = new byte[1024];
		DatagramPacket receive = new DatagramPacket(receiveData, receiveData.length);
		while(User.onChat) {
				receiveData = new byte[1024];
				
				client.receive(receive);
				String message =  new String(receive.getData(), 0,receive.getLength());
			
				if(message.startWith("@pwd") != -1 ){
					
					receiveData = new byte[1024];
					receiveData = curPath.getBytes();
					packet = new DatagramPacket(receiveData, receiveData.length, SendMessage.ipAddressSend, SendMessage.serverPort);
					receiveData = new byte[1024];
					client.send(packet);
 					//System.out.println("Current dir:" + currentPath);
				}else if(message.indexOf("@ls") != -1){
					
					File directoryPath = new File(curPath);
      					String allFiles = new String();
      					 String contents[] = directoryPath.list();
      					 System.out.println("List of files and directories in the specified directory:");
      					for(int i=0; i<contents.length; i++) {
         					allFiles = allFiles + "  " + contents[i];
					}
					receiveData = new byte[1024];
					receiveData = allFiles.getBytes();
					packet = new DatagramPacket(receiveData, receiveData.length, SendMessage.ipAddressSend, SendMessage.serverPort);
					client.send(packet);
				}
				else if((index = message.indexOf("@cd")) != -1){
					String change = new String();
					curPath = curPath + "\\";
					for(int i = index + 4; i < message.length(); i++){
						if(message.charAt(i) == ' '){
							break;
						}
						else{
							curPath = curPath + message.charAt(i);
						}
					}
					receiveData = new byte[1024];
					receiveData = curPath.getBytes();
					packet = new DatagramPacket(receiveData, receiveData.length, SendMessage.ipAddressSend, SendMessage.serverPort);
					client.send(packet);
					
				}
				else{

					System.out.println(message);
				}
			
		}
		
	
	}catch(Exception exc) {
		System.out.println(exc.getMessage());
		//client.close();
	}finally {
		client.close();

	}
	}
}
