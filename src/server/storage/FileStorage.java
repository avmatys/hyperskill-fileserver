package server.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileStorage {
    
    boolean add(String filename, InputStream in, long size) throws IOException;
    boolean get(String filename, OutputStream out) throws IOException;
    boolean delete(String filename) throws IOException;
    boolean exists(String filename);
    long size(String filename);

}
