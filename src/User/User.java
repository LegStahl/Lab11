import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
public class User{
	
	public static boolean onChat = true;
	
	public static String NAME;
	public static void main(String[] args){
		try{
			System.out.print("Hello! Welcome to the chat!");

			System.out.print("\nEnter your name: ");

			BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));

			NAME = fromUser.readLine();

			SendMessage sender = new SendMessage(Integer.parseInt(args[0]), Integer.parseInt(args[1]));

			sender.start();
		}catch(Exception mess){
			System.out.println(mess.getMessage());
		}
	}
	
}

class SendMessage extends Thread{
	
	private int serverPort;

	private int myPort;

	private DatagramSocket clientSocket;

	private InetAddress ipAddressSend;

	private byte[] sendData;

	private String sentence;

	private DatagramPacket packet;

	public SendMessage(int myPort, int serverPort){
		
		this.serverPort = serverPort;
		
		this.myPort = myPort;

	
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
		
		clientSocket = new DatagramSocket(myPort);
		
		Receiver receiver = new Receiver(myPort, clientSocket);
		
		receiver.start();
		
		ipAddressSend = InetAddress.getByName("localhost");
			while(User.onChat){
				sentence = messageSend();
				
				sendData = new byte[1024];
	
				sendData = sentence.getBytes();
				
				packet = new DatagramPacket(sendData, sendData.length, ipAddressSend, serverPort);
				if(User.onChat)
					clientSocket.send(packet); 
	
			}
			System.out.println("You have ended conversation");
			Thread.sleep(100);
		}catch(Exception mess){
			System.out.println(mess.getMessage());
			clientSocket.close();
		}finally{
			
			clientSocket.close();
		}
	}
	
}

class Receiver extends Thread {
	
	private DatagramSocket client;
	
	private byte[] receiveData;

	private int portListen;

	public Receiver(int port, DatagramSocket client){
		super();
		
		this.client = client;
		
		portListen = port;
	}
	
	public void run() {
		try {
		receiveData = new byte[1024];
		DatagramPacket receive = new DatagramPacket(receiveData, receiveData.length);
		while(User.onChat) {
				receiveData = new byte[1024];
				
				client.receive(receive);
				String message =  new String(receive.getData(),0,receive.getLength());

				System.out.println(message);
			
		}
		
	
	}catch(Exception exc) {
		System.out.println(exc.getMessage());
		client.close();
	}finally {
		client.close();

	}
	}
}
