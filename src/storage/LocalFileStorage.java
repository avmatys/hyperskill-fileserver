package server.storage;

import java.util.*;

public class LocalFileStorage implements FileStorage {
    
    private static int MAX_SIZE = 10;
    private Map<String, FileData> storage;

    private static final Set<String> VALID_FILES = new HashSet<>(Arrays.asList(
            "file1", "file2", "file3", "file4", "file5",
            "file6", "file7", "file8", "file9", "file10"
    ));

    public LocalFileStorage(){
        this.storage = new HashMap<>(MAX_SIZE);
    }

    @Override 
    public boolean add(String filename, FileData data) {
        Objects.requireNonNull(filename, "Filename must be non null");
        Objects.requireNonNull(data, "FileData must be non null");
        if (this.storage.containsKey(filename) || this.storage.size() >= MAX_SIZE || !VALID_FILES.contains(filename))
            return false;
        this.storage.put(filename, data);
        return true;
    }

    @Override 
    public Optional<FileData> get(String filename) {
        Objects.requireNonNull(filename, "Filename must be non null");
        return Optional.ofNullable(this.storage.get(filename));
    }
    
    @Override
    public boolean delete(String filename) {
        Objects.requireNonNull(filename, "Filename must be non null");
        if (!this.storage.containsKey(filename))
            return false;
        this.storage.remove(filename);
        return true;
    }

    @Override 
    public boolean exists(String filename) {
        Objects.requireNonNull(filename, "Filename must be non null");
        return this.storage.containsKey(filename);
    }


}
