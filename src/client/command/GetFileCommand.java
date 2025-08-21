package client.command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;


public class GetFileCommand implements Command {

    private final PrintWriter out;
    private final BufferedReader in;
    private final Scanner scanner;

    public GetFileCommand(PrintWriter out, BufferedReader in, Scanner scanner) {
        this.out = out;
        this.in = in;
        this.scanner = scanner;
    }

    @Override
    public void execute() throws IOException {
        System.out.print("Enter filename: ");
        String filename = scanner.nextLine().trim();
        out.println("GET " + filename);
        System.out.println("The request was sent.");
        String serverResponse = in.readLine();

        String[] getParts = serverResponse.split(" ", 2);
        int getStatusCode = Integer.parseInt(getParts[0]);

        switch (getStatusCode) {
            case 200:
                String base64Content = getParts.length > 1 ? getParts[1] : "";
                try {
                    byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
                    String decodedContent = new String(decodedBytes);
                    System.out.println("The content of the file is:" + decodedContent);
                } catch (IllegalArgumentException e) {
                    System.out.println("ERROR: Server sent invalid Base64 content.");
                }
                break;
            case 404:
                System.out.println("The response says that the file was not found!");
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
