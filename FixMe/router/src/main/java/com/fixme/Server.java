package router.src.main.java.com.fixme;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    public int port;
    public String product;
    SocketChannel socket;
    String ID = "";
    private ArrayList<String> messages = new ArrayList<String>();
    private List<Handler> clientList;

    protected int brokerID = 100000;// limit 499 999
    protected int marketID = 500000;// limit 999 999

    public Server(int brokerport, String string) {
        this.port = brokerport;
        this.product = string;
    }

    protected void startServer() {
        try {
            System.out.println("====Starting Server====");
            ServerSocketChannel server = ServerSocketChannel.open().bind(new InetSocketAddress("127.0.0.1", port));
            System.out.println("[SERVER LISTENING ON PORT " + port + "]");
            String ID = setConnectionID(product);
            this.ID = ID;
            while (true) {
                try {
                    socket = server.accept();
                    Handler socketHandler = new Handler(socket, clientList.size(), messages, port, ID, product);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private String setConnectionID(String product2) {
        if (product2.equalsIgnoreCase("broker")) {
            this.brokerID++;
            if (brokerID > 500000) {
                System.out.println(product2 + "Invalid ID exceeded max ID length");
                System.out.println(product2 + " DISCONNECTING FROM SERVER");
                System.exit(0);
            }
            return brokerID + "";
        } else {
            this.marketID++;
            if (marketID > 1000000) {
                System.out.println(product2 + "YOU SOMEHOW EXHAUSTED THE UNIQUE 6 DIGIT POSSIPLE IDS");
                System.out.println(product2 + " DISCONNECTING FROM SERVER");
                System.exit(0);
            }
            return marketID + "";
        }
    }
}