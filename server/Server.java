package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server implements Runnable {

    private ServerSocket serverSocket = null;
    public static final int PORT = 23456;
    public static final String ADDRESS = "127.0.0.1";
    private final CellDatabaseController controller;
    private volatile  boolean isStopped = false;

    protected ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public Server(CellDatabaseController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {

        openServerSocket();

        while(!isStopped) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            }
            catch (IOException e) {
                if(isStopped()) {
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            threadPool.execute(new ServerWorker(this, controller, clientSocket));
        }
    }

    private void openServerSocket() {

        try {
            this.serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port", e);
        }
    }

    public synchronized void stop() {

        isStopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    public synchronized boolean isStopped() {
        return isStopped;
    }
}