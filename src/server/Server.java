import java.io.*;
import java.net.*;

public class Server {

    private static final int PORT = 5432;

    public static void main(String[] args) {
        System.out.println("Server started!");
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                try (
                    Socket socket = server.accept(); 
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream())
                ) {
                    String msg = input.readUTF(); 
                    System.out.println("Received: ", msg);
                    String data = "All files were sent!";
                    output.writeUTF("All files were sent!");
                    System.out.println("Sent: " + data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
