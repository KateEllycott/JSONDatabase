package server;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        CellDatabase database = new CellDatabase();
        CellDatabaseController controller = new CellDatabaseController(database);
        controller.initialiseDatabase();
        Thread server = new Thread(new Server(controller));
        server.run();
    }
}

