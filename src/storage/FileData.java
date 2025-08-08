package storage;

import java.util.Objects;

public class FileData {
    
    private String originalName;
    private String contentType;
    private byte[] data;
    private long size;

    public FileData(String originalName, String contentType, long size, byte[] data) {
        Objects.requireNonNull(originalName, "Name must be non null");
        Objects.requireNonNull(contentType, "Content type must be non null");
        Objects.requireNonNull(data, "Data must be non null");

        this.originalName = originalName;
        this.contentType = contentType;
        this.data = data.clone();
        this.size = size;
    }

}
