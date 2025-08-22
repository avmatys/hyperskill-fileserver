package client.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Scanner;


public class GetFileCommand implements Command {

    private static final string PATH = "src/client/download";
    private final DataOutputStream out;
    private final DataInputStream in;
    private final Scanner scanner;

    public GetFileCommand(DataOutputStream out, DataInputStream in, Scanner scanner) {
        this.out = out;
        this.in = in;
        this.scanner = scanner;
        this.initDir();
    }

    private void initDir() {
        this.dir = Paths.get(PATH);
        if (Files.notExists(this.dir)) {
            Files.createDirectories(this.dir);
        } catch(IOExceptione) {
            e.printStackTrace();
            throw new RuntimeException("Client download folder can't be opened due to the IO exception");
        }
    }

    @Override
    public void execute() throws IOException {
        System.out.print("Do you want to get the file by name of by id (1 - name, 2 - id): ");
        String type = scanner.nextLine().trim();
        if (!"1".equals(type) && !"2".equals(type))
            return;

        System.out.print("\nEnter " + "1".equals(type) ? "name" : "id");
        String filename = scanner.nextLine().trim();
        out.writeUTF("GET");
        out.writeUTF(type);
        out.writeUTF(filename);
        out.flush();
        System.out.println("The request was sent.");
        
        int statusCode = in.readInt();
        switch (getStatusCode) {
            case 200:
                System.out.print("\nThe file was downloaded!Specify a name for it:");
                String saveFilename = Scanner.nextLine().trim();
                long saveSize = in.readLong();
                Path savePath = this.dir.resolve(saveFilename);
                try(OutputStream fout = Files.newOutputStream(savePath)) {
                    byte[] buffer = new byte[4096];
                    long remaining = saveSize;
                    while (remaining > 0) {
                        int bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                        if (byresRead == -1) break;
                        fout.write(buffer, 0, bytesRead);
                        remaining -= bytesRead;
                    }
                }
                System.out.println("File save of the hard drive!");
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
