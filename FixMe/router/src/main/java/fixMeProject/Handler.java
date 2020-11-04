package main.java.fixMeProject;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.io.BufferedReader;

public class Handler extends Thread{
    
    private SocketChannel socket;
	private List<String> messages;
	private String id;
	private String broker;
    private boolean Client;
    
    public Handler(SocketChannel socket, int clientListSize ,List<String> messages, int port, String id, String broker){
		this.socket = socket;
		this.messages = messages;
		this.id = id;
		this.Client = true;
		this.broker = broker;
		sendMessage(id + " ");
    }
    
    public void run() {
		try {
			while(this.Client){
				if ((socket != null) && (socket.isOpen()) && this.Client) {
					
					ByteBuffer buffer = ByteBuffer.allocate(1024);
					socket.read(buffer);
					String clientMsg =  new String(buffer.array()).trim();
					System.out.println("Message from : " + broker + " ID : " + this.id + "  \n[ " + clientMsg +"]");
					
					if (this.Client && !clientMsg.isEmpty()) {
						messages.add(clientMsg);
					}
					
					buffer.flip();
					buffer.clear();	 
				}
			}
		} catch (IOException e){
			System.out.println("Disconnected from " + broker + " ID : " + this.id);
			System.out.println("Server Running...");
		} 	
    }
    
    public void sendMessage(String message){
		try {
			if (this.Client) {
				ByteBuffer msgBuffer = ByteBuffer.allocate(message.length());
				msgBuffer.wrap(message.getBytes());
				socket.write(msgBuffer.wrap(message.getBytes())); 
			} else {
				System.out.println(getClass().getSimpleName() + "Closed : " + Client);
			}
		}
		catch (IOException e){
			System.out.println(" Market closed or no products available.....");
		}
	}

	public String getMessages() 
	{
		String ret = messages.get(0);
		updateMessages();
		return ret;
	}
	
	public void updateMessages()
	{
		messages.remove(0);
	}
}
