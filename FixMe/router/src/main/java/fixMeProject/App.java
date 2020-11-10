package fixMeProject;

import main.java.fixMeProject.Server;

public class App {

    public static final int brokerPort = 5000;
    public static final int marketPort = 5001;
    static private String brokerMessages = "";
	static private String marketMessages = "";

    public static void main(String[] args) {
        Server broker = new Server(brokerPort, "Broker");
        broker.start();
        Server market = new Server(marketPort, "Market");
        market.start();
        
        while (true) {
            try {
                brokerMessages = broker.getMessages();
                if(brokerMessages.isEmpty())
                {
                    System.out.println("Nothing to send!...");
                }
                else
                {
                    String[] arr = brokerMessages.split("\\|");
                    String temp = "56=" + market.getID();
                    
                    market.sendMessage(arr[0] + "|" + arr[1] + "|" + 
                    arr[2] + "|" + arr[3] + "|" + arr[4] + "|" + arr[5]+ 
                    "|" + temp + "|" + arr[7] + "|" + arr[8] + "|" + arr[9] + 
                    "|" + arr[10] + "|" + arr[11] + "|" + arr[12] + "|" + "|" 
                    + arr[13] + "|" + arr[14] + "|" + arr[15] + "|");
					brokerMessages = "";
                }
                
                System.out.println("Order being processed...");
                marketMessages = market.getMessages();
                
                if(marketMessages.isEmpty())
				{
                    broker.sendMessage(marketMessages);
                }
				else
				{
					System.out.println("Order processed and sent...");
					broker.sendMessage(marketMessages);
					marketMessages = "";
                }
            } catch(Exception e) {
				
            }
        }
    }
}
