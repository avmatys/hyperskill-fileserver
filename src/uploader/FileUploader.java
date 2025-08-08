package uploader;

import storage.FileData;
import storage.FileStorage;

public class FileUploader {
    
    private FileStorage storage;

    public FileUploader(FileStorage storage) {
        this.storage = storage;
    }

    public boolean upload(String filename) {
        FileData data = new FileData(filename, "", 0, new byte[0]);
        return this.storage.add(filename, data);
    }

    public boolean delete(String filename) {
        return this.storage.delete(filename);
    }

    public boolean exists(String filename) {
        return this.storage.exists(filename);
    }

}
