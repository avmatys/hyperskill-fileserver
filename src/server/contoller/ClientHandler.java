package server.controller;

import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Base64;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final FileController controller;

    public ClientHandler(Socket socket, FileController controller) {
       this.socket = socket;
       this.controller = controller;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String command;
            while ((command = in.readLine()) != null) {
                String[] parts = command.split(" ", 3);
                command = parts[0].toUpperCase();

                if ("EXIT".equals(command)) {
                    server.Main.stop();
                    break; 
                }

                switch (command) {
                    case "PUT":
                        if (parts.length >= 2) {
                            byte[] data = Base64.getDecoder().decode(parts[2]);
                            out.println(controller.upload(parts[1], data)); 
                        } 
                        break;
                    case "GET":
                        if (parts.length >= 2) {
                            out.println(controller.download(parts[1]));
                        }
                        break;
                    case "DELETE":
                        if (parts.length >= 2) {
                            out.println(controller.delete(parts[1]));
                        } 
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
           System.err.println("Client handler error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
     }
}
