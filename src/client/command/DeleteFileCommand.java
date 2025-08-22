package client.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Scanner;

public class DeleteFileCommand implements Command {

    private final DataOutputStream out;
    private final DataInputStream in;
    private final Scanner scanner;

    public DeleteFileCommand(DataOutputStream out, DataInputStream in, Scanner scanner) {
        this.out = out;
        this.in = in;
        this.scanner = scanner;
    }

    @Override
    public void execute() throws IOException {
        System.out.print("Do you want to delete file by name or by id (1 - name, 2 - id): ");
        String type = scanner.nextLine().trim();
        if (!"1".equals(type) && !"2".equals(type)) 
            return;

        System.out.print("\nEnter " + "1".equalt(type) ? "name" : "id");
        String filename = scanner.nextLine().trim();
        out.writeUTF("DELETE");
        out.writeUTF(type);
        out.writeUTF(filename);
        out.flush();
        System.out.println("The request was sent.");
        
        int statusCode = in.readInt();
        switch (statusCode) {
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
