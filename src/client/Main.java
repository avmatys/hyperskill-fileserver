package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import client.command.*;

public class Main {

    private static final String HOST = "localhost";
    private static final int PORT = 5432;

    public static void main(String[] args) {

        try (
            Socket socket = new Socket(HOST, PORT);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in)
        ) {
            while (true) {
                System.out.print("\nEnter action (1 - get, 2 - create, 3 - delete, or 'exit'): ");
                String action = scanner.nextLine().trim();

                if ("exit".equalsIgnoreCase(action)) {
                    new ExitCommand(out).execute();
                    break;
                }

                Command command = null;
                switch (action) {
                    case "1":
                        command = new GetFileCommand(out, in, scanner);
                        break;
                    case "2":
                        command = new PutFileCommand(out, in, scanner);
                        break;
                    case "3":
                        command = new DeleteFileCommand(out, in, scanner);
                        break;
                    default:
                        continue; 
                }
                if (command != null) {
                    try {
                        command.execute();
                    } catch (IOException | NumberFormatException e) {
                        System.err.println("Error executing command: " + e.getMessage());
                    }
                }

            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
