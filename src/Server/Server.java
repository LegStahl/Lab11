import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server{
	public static boolean onChat = true;
	public static String NAME;
	public static void main(String[] args) {
		try {
			System.out.print("Hello! Welcome to the chat!");
			System.out.print("\nEnter your name: ");
			
			BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
			NAME = fromUser.readLine();

			int serverPort = Integer.parseInt(args[0]);

			Receiver server = new Receiver(serverPort);
			
			server.start();
			
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}

class SendMessage extends Thread {
	private InetAddress ipAdrClient;
	
	private int clientPort;

	private int serverPort;

	private DatagramSocket socket;
	
	public SendMessage(InetAddress ipAdrClient, int clientPort, int serverPort, DatagramSocket socket) {
		super();

		this.ipAdrClient = ipAdrClient;

		this.clientPort = clientPort;

		this.serverPort = serverPort;

		this.socket = socket;
		
	}

	
	public String messageSend() throws Exception {
		BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
		String sentence = fromUser.readLine();
		
		if(sentence.equals("end conversation")) {
			Server.onChat = false;
		
		}
	
		sentence = Server.NAME + ": " + sentence;
		return sentence;
	}

	
	public void run()  {
		try {
		
	
		while(Server.onChat) {
			
				byte[] sendData = new byte[1024];
				String sentense = messageSend();
				
				sendData = sentense.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAdrClient, clientPort);
				if(Server.onChat)
					socket.send(sendPacket);
				
			
	
		}
		}
		catch(Exception mess) {
			System.out.println(mess.getMessage());
			socket.close();
		}
		finally {
			socket.close();
		}
	}
}

class Receiver extends Thread {

	private int serverPort;
	
	public Receiver(int myPort) {
		super();
		
		this.serverPort = myPort;
	}
	
	private DatagramSocket socket;
	
	private byte[] receiveP = new byte[1024];
	
	public void run() {
		try {

		System.out.println("If you want to leave the chat, enter end conversation");
		socket = new DatagramSocket(serverPort);

		System.out.println("We are waiting for user's connection");

		DatagramPacket receive = new DatagramPacket(receiveP, receiveP.length);

		socket.receive(receive);

		InetAddress sendAdr = receive.getAddress();

		int portClient = receive.getPort();
		
		SendMessage sender = new SendMessage(sendAdr, portClient, serverPort, socket);

		sender.start();

		System.out.println("The connection completed");

		String message = new String(receive.getData(), 0, receive.getLength());

		System.out.println(message);
		while(Server.onChat) {
			
				
				receive = new DatagramPacket(receiveP, receiveP.length);
				socket.receive(receive);
				message = new String(receive.getData(),0,receive.getLength());

				System.out.println(message);
			
		}
		System.out.println("You have ended conversation");
	
	}catch(Exception exc) {
		System.out.println(exc.getMessage());
		socket.close();
	}finally {
		socket.close();

	}
	}
}
