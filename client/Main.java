package client;

public class Main {

    public static void main(String[] args) {

        String jsonRequest = RequestParser.commandLineRequestToJson(args);
        Client client = new Client(jsonRequest);
        client.run();
    }
}
