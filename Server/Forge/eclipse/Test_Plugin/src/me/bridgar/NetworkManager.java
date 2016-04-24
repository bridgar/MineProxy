package me.bridgar;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.entity.Player;

public class NetworkManager extends Thread{
	private Socket socket;
	private ChestFactory chest;
	private Queue<PlayerRequest> input;
	
	public class PlayerRequest {
		public byte[] bytes;
		public Player player;
		
		public PlayerRequest(byte[] bytes, Player player) {
			this.bytes = bytes;
			this.player = player;
		}
	}
	
	public NetworkManager(ChestFactory chest) {
		this.chest = chest;
		input = new ConcurrentLinkedQueue<PlayerRequest>();
	}
	
	public void queueBytes(byte[] bytes, Player player) {
		PlayerRequest pr = new PlayerRequest(bytes, player);
		input.add(pr);
	}
	
	public void run(){
		
		try {
			socket = new Socket("Localhost", 80);
			
			while(true) {
				
				//Sleep when there's nothin in the input
				if(input.isEmpty()) {
					try {
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					PlayerRequest pr = input.poll();

					byte[] bytes = pr.bytes;
					Player player = pr.player;
					
					OutputStream os = socket.getOutputStream();
					os.write(bytes);
					
					byte[] response = new byte[8196];
					InputStream is = socket.getInputStream();
					for (;is.read(response) != -1;) {
						chest.sendBytes(response, player);
					}
					
				}
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
