package router.src.main.java.com.fixme;

import java.net.Socket;

public class App {

    public static final int brokerPort = 5000;
    public static final int marketPort = 5001;	
	static private String brokerMessages = "";
	static private String marketMessages = "";
   public static void main(String[] args) {

    Server broker = new Server(brokerPort, "BROKER");
    broker.start();
   }
}
