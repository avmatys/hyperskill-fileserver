package server;

import server.contoller.ClientHandler;
import server.contoller.FileController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private final FileController controller;
    private final ExecutorService executor;
    private volatile boolean running = true;
    private ServerSocket serverSocket;

    public Server(int port, FileController controller) {
        this.port = port;
        this.controller = controller;
        this.executor = Executors.newFixedThreadPool(10);
    }

    public void start() {
        System.out.println("Server started!");
        try {
            serverSocket = new ServerSocket(port);
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    executor.submit(new ClientHandler(socket, controller, this));
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Accept failed: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server socket creation failed: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        if (!running) return;
        System.out.println("Shutting down server...");
        this.running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            executor.shutdownNow();
        } catch (IOException e) {
            System.err.println("Error during server shutdown: " + e.getMessage());
        }
    }
}