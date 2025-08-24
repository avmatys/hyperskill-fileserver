package server.controller;

import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final FileController controller;

    public ClientHandler(Socket socket, FileController controller) {
       this.socket = socket;
       this.controller = controller;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            String command = in.readUTF();
            if ("EXIT".equals(command)) {
                server.Main.stop();
                return;
            }

            switch (command) {
                case "PUT" : 
                    this.handleUpload(in, out);
                    break
                case "GET" : 
                    this.handleDownload(in, out);
                    break;
                case "DELETE" : 
                    this.handleDelete(in, out);
                    break;
                default:
                    out.writeInt(400);
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

    private void handleUpload(DataInputStream in, DataOutputStream out) {
        try {
            String filename = in.readUTF();
            long size = in.readLong();
            if (controller.existsByFilename(filename) {
                out.writeInt(403);
                return;
            }
            String id = controller.upload(filename, size, in);
            if (id != null) {
                out.writeInt(200);
                out.writeUTF(id);
                return;
            }
            out.writeInt(500);
        } catch (IOException e) {
            out.writeInt(500);
            throw new RuntimeException("IO exception during PUT operation");
        }
    }

    private void handleDelete(DataInputStream in, DataOutputStream out) {
        try {
            int type = in.readInt();
            String value = in.readUTF();
            if (type != 1 && type != 2) {
                out.writeInt(400);
                return;
            }
            if (!(type == 1 ? controller.existsByFilename(value) : controller.existsById(value))) {
                out.write(404);
                return;
            }
            if (type == 1 ? controller.deleteByFilename(value) : controller.deleteById(value)) {
                out.writeInt(200);
                return;
            }
            out.writeInt(500);
        } catch (IOException e) {
            out.write(500);
            throw new RuntimeException("IO exception during DELETE operation");
        }
    }


}
