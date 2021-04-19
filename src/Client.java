import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

    // build constructor

    public Client(String host) {
        super("Client side");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendData(e.getActionCommand());
                        userText.setText("");
                    }
                }
        );

        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(400, 200);
        setVisible(true);
    }

    // method to connect to the server

    public void startRunning() {
        try {

            connectToServer();
            setupStreams();
            whileChatting();

        } catch (EOFException eofException) {
            showMessage("\n Client terminated connection");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            closeDown();
        }
    }

    //connect to desired server

    private void connectToServer() throws IOException {

        showMessage("Attempting connection... \n");

        // need ip address of server and port number

        connection = new Socket(InetAddress.getByName(serverIP), 6789);
        showMessage("Connected to:" + connection.getInetAddress().getHostName());
    }

    //set up streams

    private void setupStreams() throws IOException {

        //setup output stream

        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();

        //setup input stream

        input = new ObjectInputStream(connection.getInputStream());

        showMessage("\n streams are now setup \n");
    }


    private void showMessage(final String text) {

        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {

                        //adds message to chat windows and updates
                        chatWindow.append(text);
                    }
                }
        );
    }

    //while chatting with server

    private void whileChatting() throws IOException {

        ableToType(true);
        do {
            try {
                message = (String) input.readObject();

            } catch(ClassNotFoundException classNotFoundException) {
                showMessage("\n unknown object...");
            }

        } while (!message.equals("SERVER - END"));
    }

    // close down streams and sockets

    private void closeDown(){
        showMessage("\n closing down...");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();

        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    // deliver messages to server

    private void sendData(String message){
        try{

            output.writeObject("CLIENT - " + message);
            output.flush();
            showMessage("\n CLIENT - " +  message);

        }catch(IOException ioException){
            chatWindow.append("\n error... something went wrong");
        }
    }

    private void ableToType(final boolean b){

        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        userText.setEditable(b);
                    }
                }
        );

    }

}