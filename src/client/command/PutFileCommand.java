package client.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;

public class PutFileCommand implements Command {

    private final PrintWriter out;
    private final BufferedReader in;
    private final Scanner scanner;

    public PutFileCommand(PrintWriter out, BufferedReader in, Scanner scanner) {
        this.out = out;
        this.in = in;
        this.scanner = scanner;
    }

    @Override
    public void execute() throws IOException {
        System.out.print("Enter filename: ");
        String filename = scanner.nextLine().trim();
        System.out.print("Enter file content: ");
        String content = scanner.nextLine();

        String base64ContentForPut = Base64.getEncoder().encodeToString(content.getBytes());
        out.println("PUT " + filename + " " + base64ContentForPut);
        System.out.println("The request was sent.");
        String serverResponse = in.readLine();

        int putStatusCode = Integer.parseInt(serverResponse);
        switch (putStatusCode) {
            case 200:
                System.out.println("The response says that file was successfully created!");
                break;
            case 403:
                System.out.println("The response says that creating of file was forbidden!");
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
