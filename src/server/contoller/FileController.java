package server.controller;

import server.storage.FileStorage;

public class FileController {
    
    private FileStorage storage;

    public FileController(FileStorage storage) {
        this.storage = storage;
    }

    public FileResponse upload(String filename, byte[] data) {
        if (this.storage.exists(filename)) {
            return new FileResponse(403);
        }
        if (this.storage.add(filename, data)) {
            return new FileResponse(200);
        }
        return new FileResponse(500);
    }

    public FileResponse delete(String filename) {
        if (!this.storage.exists(filename)) {
            return new FileResponse(404);
        }
        if (this.storage.delete(filename)) {
            return new FileResponse(200);
        }
        return new FileResponse(500);
    }

    public FileResponse download(String filename) {
        if (!this.storage.exists(filename)) {
            return new FileResponse(404);
        }
        byte[] data = this.storage.get(filename);
        if (data.length > 0) {
            return new FileResponse(200, data);
        }
        return new FileResponse(500);
    }

}
