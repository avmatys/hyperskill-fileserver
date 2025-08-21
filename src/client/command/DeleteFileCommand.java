package client.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;

public class DeleteFileCommand implements Command {

    private final PrintWriter out;
    private final BufferedReader in;
    private final Scanner scanner;

    public DeleteFileCommand(PrintWriter out, BufferedReader in, Scanner scanner) {
        this.out = out;
        this.in = in;
        this.scanner = scanner;
    }

    @Override
    public void execute() throws IOException {
        System.out.print("Enter filename: ");
        String filename = scanner.nextLine().trim();
        out.println("DELETE " + filename);
        System.out.println("The request was sent.");
        String serverResponse = in.readLine();

        int deleteStatusCode = Integer.parseInt(serverResponse);
        switch (deleteStatusCode) {
            case 200:
                System.out.println("The response says that file was successfully deleted!");
                break;
            case 404:
                System.out.println("The response says that file was not found!");
                break;
            case 500:
                System.out.println("The response says that internal server error happened!");
                break;
            default:
                System.out.println("The response says that " + serverResponse);
                break;
        }
    }
}
