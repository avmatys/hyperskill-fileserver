package client.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class GetFileCommand implements Command {

    private static final String PATH = "src/client/data";
    private final DataOutputStream out;
    private final DataInputStream in;
    private final Scanner scanner;
    private final Path dir;

    public GetFileCommand(DataOutputStream out, DataInputStream in, Scanner scanner) throws IOException {
        this.out = out;
        this.in = in;
        this.scanner = scanner;
        this.dir = Paths.get(PATH);
        if (Files.notExists(this.dir))
            Files.createDirectories(this.dir);
    }

    @Override
    public void execute() throws IOException {
        String[] input = CommandUtil.getTypeAndValue(scanner, "get");
        if (input == null) return;

        out.writeUTF("GET");
        out.writeInt(Integer.parseInt(input[0]));
        out.writeUTF(input[1]);
        out.flush();

        System.out.println("The request was sent.");

        int statusCode = in.readInt();
        switch (statusCode) {
            case 200:
                System.out.print("\nThe file was downloaded! Specify a name for it:");
                String saveFilename = scanner.nextLine().trim();
                long saveSize = in.readLong();
                Path savePath = this.dir.resolve(saveFilename);
                try(OutputStream fout = Files.newOutputStream(savePath)) {
                    byte[] buffer = new byte[4096];
                    long remaining = saveSize;
                    while (remaining > 0) {
                        int bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                        if (bytesRead == -1) break;
                        fout.write(buffer, 0, bytesRead);
                        remaining -= bytesRead;
                    }
                }
                System.out.println("File saved of the hard drive!");
                break;
            case 404:
                System.out.println("The response says that this file is not found!");
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
