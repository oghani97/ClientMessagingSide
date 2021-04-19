import javax.swing.*;

public class ClientTest {

    public static void main(String[] args){

        Client c;
        // need to pass in IP for constructor
        // do not have dedicated server and so will use a local host IP address

        c = new Client("127.0.0.1");
        c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        c.startRunning();



    }
}
