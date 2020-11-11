package fixMeProject;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.CharBuffer;

public class App 
{
    private static BufferedReader input = null;
	static String message = null;
    public static final String[] products = {"The Gold Leaf Bread", "Roquefort and Almond Sourdough bread", "Brioche", "Baguette", "Brown Bread", "White Bread"};
	public static final int[] inventory = {30, 60, 90, 120,150, 180};
    public static String ID ="";

    public static void main( String[] args ) throws Exception {

        InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 5001);
        Selector selector = Selector.open();
        SocketChannel sc = SocketChannel.open();

        sc.configureBlocking(false);
        sc.connect(addr);
        sc.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);

        input = new BufferedReader(new InputStreamReader(System.in));
        printInstruments();
        
        while (true) {
            if (selector.select() > 0) {
                Boolean Status = processReadySet(selector.selectedKeys());
                if (Status) {
                    break;
                }
            }
        }
        sc.close();
    }

    public static void printInstruments()
	{
		System.out.println("List vailabe to trade\n");
		for(int i = 0; i< products.length;i++)
		{
			System.out.println("index" + i +" : [ " + products[i] + " ]");
		}
	}

    public static Boolean processReadySet(Set readySet)
            throws Exception {
        SelectionKey key = null;
        Iterator iterator = null;
	
        iterator = readySet.iterator();
        while (iterator.hasNext()) {
            key = (SelectionKey) iterator.next();
            iterator.remove();
        }
        if (key.isConnectable()) {
            Boolean connected = processConnect(key);
            if (!connected) {
                return true;
            }
        }
       if (key.isReadable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer bb = ByteBuffer.allocate(1024);
            sc.read(bb);
            String result = new String(bb.array()).trim();
           
			if(ID.isEmpty())
			{
				ID = result;
				System.out.println("Assigned ID: " + "[ " + ID +" ]");
			}
			else
			{
				 System.out.println("Handling request");
				  handleRequest(key,result);
			}
        }
		
        return false;
    }
	
	public static String processOrder(String instrument, String fixmsg )
	{
		int var = 0;
		for(int i = 0; i< products.length;i++)
		{
			if(instrument.equals(products[i]))
		    {
				var = i;
				break;
			}
		}

		String[] arr = fixmsg.split("\\|");
		String quantity = arr[11].split("=")[1];
		String buyOrSell = arr[9].split("=")[1];
		int varb = 0;
		varb = Integer.parseInt(quantity);
		if(buyOrSell.equals("1"))
	    {
		    inventory[var] = inventory[var] - varb;
			if(inventory[var] > 0)
			{
				return "accepted";
			}
			else
			{
				return "rejected";
			}
		}
		else
		{
		   inventory[var] = inventory[var] + varb;
			
			return "accepted";
		}	
	}
	
	public static void handleRequest( SelectionKey key, String ret)
	{
		try
		{
			String[] arr = ret.split("#");
			System.out.println("Message received from Server: " + "[ " + arr[1] + " ]");

			String processOrder = processOrder(arr[0], arr[1]);
			String msg = processOrder + " " + arr[1];

			System.out.println("Handling request");

			SocketChannel sc = (SocketChannel) key.channel();
			ByteBuffer bb = ByteBuffer.wrap(msg.getBytes());
			sc.write(bb);
			System.out.println("request sent");
		}
		catch(IOException e)
		{
			 System.out.println("request could not be handles");
		}	
	}
	
    public static Boolean processConnect(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            while (sc.isConnectionPending()) {
                sc.finishConnect();
            }
        } catch (IOException e) {
            key.cancel();
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
