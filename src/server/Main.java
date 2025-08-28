package server;

import server.contoller.FileController;
import server.storage.FileStorage;
import server.storage.HardDriveFileStorage;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        FileStorage storage = new HardDriveFileStorage();
        FileController controller = new FileController(storage);
        Server server = new Server(5432, controller);
        server.start();
    }
}