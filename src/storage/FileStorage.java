package storage;

import java.util.Optional;

public interface FileStorage {
    
    boolean add(String filename, FileData data);
    Optional<FileData> get(String filename);
    boolean delete(String filename);
    boolean exists(String filename);

}
