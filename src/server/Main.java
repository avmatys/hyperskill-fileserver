package server;

import java.util.Objects;
import java.util.Scanner;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

import server.controller.*;
import server.storage.*;

public class Main {

    private static final int PORT = 5432; 
    private static boolean RUN = true;

    public static void main(String[] args) {

        FileStorage storage = new HardDriveFileStorage();
        FileController controller = new FileController(storage);

        System.out.println("Server started!");
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (RUN) {
                Socket socket = server.accept();
                Thread client = new Thread(new ClientHandler(socket, controller));
                client.start();
                try {
                    client.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void stop() {
        RUN = false;
    }
}
