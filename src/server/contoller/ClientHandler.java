package server.contoller;

import server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final FileController controller;
    private final Server server;

    public ClientHandler(Socket socket, FileController controller, Server server) {
       this.socket = socket;
       this.controller = controller;
       this.server = server;
    }

    @Override
    public void run()  {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            String command = in.readUTF();
            if ("EXIT".equals(command)) {
                this.server.shutdown();
                return;
            }

            switch (command) {
                case "PUT" : 
                    this.handleUpload(in, out);
                    break;
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
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
     }

    private void handleUpload(DataInputStream in, DataOutputStream out) throws IOException {
        try {
            String filename = in.readUTF();
            long size = in.readLong();
            if (controller.existsByFilename(filename)) {
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
        }
    }

    private void handleDelete(DataInputStream in, DataOutputStream out) throws IOException {
        try {
            int type = in.readInt();
            String value = in.readUTF();
            if (type != 1 && type != 2) {
                out.writeInt(400);
                return;
            }
            String filename = type == 1 ? value : controller.getFilenameById(value);
            if (filename == null || !controller.existsByFilename(filename)) {
                out.writeInt(404);
                return;
            }
            if (controller.delete(filename)) {
                out.writeInt(200);
                return;
            }
            out.writeInt(500);
        } catch (IOException e) {
            out.writeInt(500);
        }
    }

    private void handleDownload(DataInputStream in, DataOutputStream out) throws IOException {
        try {
            int type = in.readInt();
            if (type != 1 && type != 2) {
                out.writeInt(400);
                return;
            }
            String value = in.readUTF();
            System.out.println(value);
            String filename = type == 1 ? value : controller.getFilenameById(value);
            System.out.println(filename);
            if (filename == null || !controller.existsByFilename(filename)) {
                out.writeInt(404);
                return;
            }
            long size = controller.size(filename);
            Runnable action = controller.download(filename, out);
            if (action == null) {
                out.writeInt(500);
            } else {
                out.writeInt(200);
                out.writeLong(size);
                action.run();
            }
        } catch(IOException e) {
            out.writeInt(500);
        }
    }

}
