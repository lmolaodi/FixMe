package main.java.fixMeProject;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class App {

   private static BufferedReader input = null;
   protected SocketChannel client;
   protected ArrayList<String> messages = new ArrayList<>();
   public static String ID ="";
   public static final String[] products = {"The Gold Leaf Bread", "Roquefort and Almond Sourdough bread", "Brioche", "Baguette", "Brown Bread", "White Bread"};
   public static final int[] inventory = {10, 20, 30, 40,50, 60};
   public static int orderID = 0;

   public static void main(String[] args) throws Exception{
	   
	InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 5000);
    Selector selector = Selector.open();
	SocketChannel sc = SocketChannel.open();
	
    sc.configureBlocking(false);
    sc.connect(addr);
	sc.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);

	input = new BufferedReader(new InputStreamReader(System.in));
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

public static String setFixNotation(int price, int quantity, int buyOrSell)
{
	String fixNotation = "";
	ZonedDateTime time= ZonedDateTime.now(ZoneOffset.UTC);
	fixNotation = "35=D|49="+ ID +"|56=100001|52="+ time +"|55=D|54="+ buyOrSell +"|60=1|38="+ quantity +"|40=1|44="+ price +"|39=1";
	fixNotation = "8=FIX.4|9="+ fixNotation.getBytes().length +"|11="+ orderID +"|21=1|"+ fixNotation +"|10="+ 
	getChecksum(ByteBuffer.wrap(fixNotation.getBytes()), fixNotation.length()) +"|";
	return fixNotation;
}

public static String getChecksum(ByteBuffer a, int b)
{
	int checksum = 0;
	for (int i = 0; i < b; i++) {
		checksum = checksum + a.get(i);
	}
	checksum = checksum % 256;
	if(checksum < 10)
	{
		return "00" + checksum;
	}
	else if(checksum < 100)
	{
		return "0" + checksum;
	}
	else
	{
		return checksum % 256 + "";
	}
}

public static Boolean processReadySet(Set readySet) throws Exception {
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
	if (key.isReadable())
	{
		SocketChannel sc = (SocketChannel) key.channel();
		ByteBuffer bb = ByteBuffer.allocate(1024);
		sc.read(bb);
		String result = new String(bb.array()).trim();
		if(ID.isEmpty())
		{
			ID = result;
			System.out.println("Assigned ID: " + "[ " + ID +" ]");
			outPutOptions(sc,bb );
		}
		else
		{
			System.out.println("Message received from Server: " + "[ " + result +" ]");
			updateInventory(result);
		}
		outPutOptions(sc,bb );
	}
	return false;
}

public static void updateInventory(String msg)
{
	String[] arr = msg.split(" ");
	String[] array = arr[1].split("\\|");
	String quantity = array[11].split("=")[1];
	String buyOrSell = array[9].split("=")[1];

	int varb = 0;
	varb = Integer.parseInt(quantity);
	int var = 0;
	var = Integer.parseInt(array[2].split("=")[1]);

	if(arr[0].equals("accepted"))
	{
		inventory[var] = inventory[var] + varb;
	}
}

public static void printProducts(){
	
	System.out.println("List available to trade");

	for(int i = 0; i< products.length; i++)
	{
		System.out.println("index" + i + " : [ " + products[i] + " ]");
	}
}

public static void outPutOptions( SocketChannel sc,ByteBuffer bb ){
	try{
		System.out.println("Please select option [ 'BUY' OR 'SELL']\n");
		String msg = input.readLine();
		int quantity = 0;
		int price = 0;
		String item = "";

		while(true)
		{
			if(msg.equalsIgnoreCase("buy") || msg.equalsIgnoreCase("sell"))
			{
				break;
			}
			System.out.println("Please select option [ 'BUY' OR 'SELL' ]");
			msg = input.readLine();
		}
		
		printProducts();
		System.out.println("[ Enter the index number of the product you would like to buy or sell ]");
		item = getInstrument();
		int index = getIndex(item);
		orderID = index;

		while(true)
		{
			if(quantity > 0)
			{
				break;
			}
			quantity = getQuantity(msg, index);
		}
		
		while(true)
		{
			if(price > 0)
			{
				break;
			}
			price = getPrice();
		}
		
		if(msg.equalsIgnoreCase("buy"))
		{
			msg = item + "#" + setFixNotation(price, quantity,1);
		}
		else
		{
			msg = item + "#" + setFixNotation(price, quantity,2);
		}

		bb = ByteBuffer.wrap(msg.getBytes());
		sc.write(bb);
	} catch(IOException e)
	{
		System.out.println("Failed");
	}
}

public static int getIndex( String item){
	
	int var = 0;
	for(int i = 0; i< products.length; i++)
	{
		if(item.equals(products[i]))
		{
			var = i;
			break;
		}
	}
	return var;
}

public static String getInstrument(){
	try
	{
		int ret = 6;
		while(true)
		{
			System.out.println("Enter Product Index [0 - 5]");
			ret = Integer.parseInt(input.readLine());
			if(ret < 6)
			{
				break;
			}
		}
		return products[ret];
	} catch(IOException e)
	{
		System.out.println("ERROR : Requested an integer, received something else");
	}
	return "";
}

public static int getPrice(){
	try
	{
		System.out.println("At what price [1 - 10000]");
		int ret = Integer.parseInt(input.readLine());
		return ret;
	} catch(IOException e)
	{
		System.out.println("ERROR: Requested an integer, received something else");
	}
	return 0;
}

public static int getQuantity(String cmd, int index){
	try
	{
		System.out.println("Quantity of bread [1 - 180]");
		int ret = Integer.parseInt(input.readLine());
		if(cmd.equalsIgnoreCase("buy"))
		{
			return ret;
		}
		else
		{
			int available = inventory[index];
			while(true)
			{
				if(ret < available)
				{
					break;
				}
				System.out.println("You do not have have that much quantity");
				System.out.println("Quantity of bread [1 - 180]");
				ret = Integer.parseInt(input.readLine());
			}
			return ret;
		}	
	} catch(IOException e)
	{
		System.out.println("");
	}
	return 0;
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
