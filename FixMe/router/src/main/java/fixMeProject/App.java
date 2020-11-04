package fixMeProject;

import main.java.fixMeProject.Server;

public class App {

    public static final int brokerPort = 5000;
    public static final int marketPort = 5001;

    public static void main(String[] args) {
        Server broker= new Server(brokerPort, "Broker");
        broker.start();
    }
}
