package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerWorker implements Runnable {

    private CellDatabaseController controller;
    private Socket clientSocket;
    private final Server server;

    public ServerWorker(Server server, CellDatabaseController controller, Socket clientSocket) {
        this.server = server;
        this.controller = controller;
        this.clientSocket = clientSocket;
    }
    @Override
    public void run() {

            try(DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream())) {
                String jsonRequest = inputStream.readUTF();
                Response response = processRequest(jsonRequest);
                String jsonResponse = new Gson().toJson(response);
                outputStream.writeUTF(jsonResponse);
            }
            catch(IOException e) {
                e.printStackTrace();
            }
    }

    private Response processRequest(String jsonRequest) {

        Response response;
        String requestType = getRequestType(jsonRequest);

        switch (requestType) {
            case "exit":
                response = new Response();
                response.setResponse("OK");
                server.stop();
                break;
            case "get":
                response = controller.getCell(jsonRequest);
                break;
            case "set":
                response = controller.setCell(jsonRequest);
                break;
            case "delete":
                response = controller.clearCell(jsonRequest);
                break;
            default: {
                response = new Response();
                response.setResponse("ERROR");
                response.setReason("Wrong request type");
            }
        }
        return response;
    }

    private String getRequestType(String jsonRequest) {
        JsonObject object = JsonParser.parseString(jsonRequest).getAsJsonObject();
        return object.get("type").getAsString();
    }
}
