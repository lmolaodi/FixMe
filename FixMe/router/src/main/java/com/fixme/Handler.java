package router.src.main.java.com.fixme;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class Handler extends Thread {

    private SocketChannel socket;
    private int size;
    private ArrayList<String> messages;
    private int port;
    private String iD;
    private String product;
    private boolean client;

    public Handler(SocketChannel socket, int size, ArrayList<String> messages, int port, String iD, String product) {

        this.socket = socket;
        this.messages = messages;
        this.iD = iD;
        this.client = true;
        this.product = product;

        sendMessage(iD + " ");
    }

    private void sendMessage(String string) {
        try {
            if (this.client) {

                ByteBuffer msgBuffer = ByteBuffer.allocate(string.length());
                ByteBuffer.wrap(string.getBytes());
                socket.write(ByteBuffer.wrap(string.getBytes()));

            } else {

                System.out.println(getClass().getSimpleName() + "Closed : " + client);

            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(" No market avalaible");
        }
    }
}