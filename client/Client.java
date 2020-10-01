package client;

import server.Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable {

    private final String jsonRequest;

    public Client(String jsonRequest) {
        this.jsonRequest = jsonRequest;
    }

    @Override
    public void run() {

        System.out.println("Client started!");

        try (Socket socket = new Socket(InetAddress.getByName(Server.ADDRESS), Server.PORT)) {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(jsonRequest);
            System.out.println("Sent: " + jsonRequest);
            String respond = inputStream.readUTF();
            System.out.println("Received: " + respond);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}