package main.java.fixMeProject;

import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.io.BufferedReader;

public class Server extends Thread{

    private List<Handler> clientList;
    public ServerSocketChannel server = null;

    protected int brokerID = 100000;//limit  499 999
    protected int marketID = 500000;//limit is 999 999
    public String componentType;
    public int port;
    SocketChannel sc;
    private ArrayList<String> messages = new ArrayList<String>();
    public Handler socketHandler = null;
    String ID = "";
    
    public Server(int recievedPort, String cType)
    {
        this.port = recievedPort;
        this.componentType = cType;
		clientList = new ArrayList<Handler>();
    }

    private String setConnectionID(String componentType)
	{
		if(componentType.equalsIgnoreCase("broker"))
		{
			this.brokerID++;
			if(brokerID > 500000)
		    {
				System.out.println(componentType + "YOU SOMEHOW EXHAUSTED THE UNIQUE 6 DIGIT POSSIPLE IDS");
				System.out.println(componentType + " DISCONNECTING FROM SERVER");
				System.exit(0);
			}
			return brokerID+"";
		}
		else
		{
			this.marketID++;
			if(marketID > 1000000)
		    {
				System.out.println(componentType + "YOU SOMEHOW EXHAUSTED THE UNIQUE 6 DIGIT POSSIPLE IDS");
				System.out.println(componentType + " DISCONNECTING FROM SERVER");
				System.exit(0);
			}
			return marketID + "";
        }
    }

    public void startServer()
    {
        try {
            server = ServerSocketChannel.open().bind(new InetSocketAddress("127.0.0.1", port));
            System.out.println("Server listerning on port: " + port);
            String ID = setConnectionID(componentType);
            this.ID = ID;

            while(true)
            {
                sc = server.accept();
                socketHandler = new Handler(sc, clientList.size() ,messages, port, ID,  componentType);
                System.out.println("New " + componentType + "[ Connection Accepted ] " + " Broker ID : " + ID + "\n");
                clientList.add(socketHandler);
                socketHandler.start();
            }
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println("Disconnected from the server");
        }
    }

    public void sendMessage(String str) 
	{
		socketHandler.sendMessage(str);
	}

	public String getID()
	{
		return ID;
	}

	public String getMessages() {
		return socketHandler.getMessages();
	}

    @Override
    public void run()
    {
        startServer();
    }
}
