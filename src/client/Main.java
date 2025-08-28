package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import client.command.Command;
import client.command.DeleteFileCommand;
import client.command.ExitCommand;
import client.command.GetFileCommand;
import client.command.PutFileCommand;

public class Main {

    private static final String HOST = "localhost";
    private static final int PORT = 5432;
    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 100;

    public static void main(String[] args) {
        System.out.println("Client started!");
        Socket socket = null;
        try {
            int retries = 0;
            while (retries < MAX_RETRIES) {
                try {
                    socket = new Socket(HOST, PORT);
                    break;
                } catch (IOException e) {
                    System.err.println("Connection attempt " + (retries + 1) + " failed. Retrying...");
                    retries++;
                    Thread.sleep(RETRY_DELAY_MS);
                }
            }
            if (socket == null || !socket.isConnected()) {
                System.err.println("Could not connect to the server after multiple retries. Exiting.");
                return;
            }

            try (
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

                    Command command;
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
                            System.err.println("Invalid action. Please choose 1, 2, 3, or exit.");
                            continue;
                    }
                    command.execute();
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}