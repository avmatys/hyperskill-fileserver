package client.command;

import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.util.Scanner;

public class PutFileCommand implements Command {

    private static final String PATH = "src/client/data";
    private final DataOutputStream out;
    private final DataInputStream in;
    private final Scanner scanner;
    private final Path dir;

    public PutFileCommand(DataOutputStream out, DataInputStream in, Scanner scanner) {
        this.out = out;
        this.in = in;
        this.scanner = scanner;
        this.initDir();
    }

    private void initDir() {
        this.dir = Paths.get(PATH);
        if (Files.notExists(this.dir)) {
            Files.createDirectories(this.dir);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Client folder can'be opened due to the IO exception");
        }
    }

    @Override
    public void execute() throws IOException {
        System.out.print("Enter name of the file: ");
        String filename = scanner.nextLine().trim();

        Path file = this.dir.resolve(filename);
        if (!Files.exists(file)) return;

        System.out.print("\nEnter name of the file to be saved on server: ");
        String serverFilename = scanner.nextLine().trim();
        serverFilename = serverFilename.isEmpty() ? filename : serverFilename;
        
        long fileSize = Files.size(file);
        out.writeUTF("PUT");
        out.writeUTF(serverFilename);
        out.writeLong(fileSize);
        
        try (InputStream fis = Files.newInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        out.flush();

        System.out.println("The request was sent.");

        int statusCode = in.readInt();
        switch (statusCode) {
            case 200:
                int fileId = in.readInt();
                System.out.println("The response says that file is saved! ID = " + fileId);
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
