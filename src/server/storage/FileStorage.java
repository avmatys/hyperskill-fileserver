package server.storage;

import java.util.Optional;

public interface FileStorage {
    
    boolean add(String filename, byte[] data);
    byte[] get(String filename);
    boolean delete(String filename);
    boolean exists(String filename);

}
