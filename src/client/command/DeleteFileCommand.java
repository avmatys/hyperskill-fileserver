package client.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
        String[] input = CommandUtil.getTypeAndValue(scanner, "delete");
        if (input == null) return;

        out.writeUTF("DELETE");
        out.writeInt(Integer.parseInt(input[0]));
        out.writeUTF(input[1]);
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
                System.out.println("No response");
                break;
        }
    }
}
