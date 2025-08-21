package server.storage;

import java.util.Objects;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

public class HardDriveFileStorage implements FileStorage {
    
    private static final String PATH = "server/data/";
    private final Path dir;

    public HardDriveFileStorage() {
        this(PATH);
    }

    public HardDriveFileStorage(String path) {
        this.dir = Paths.get(path);
        if(Files.notExists(this.dir)) {
            try {
                Files.createDirectories(this.dir);
            } catch(IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Hard drive can't be instatiated due to the IO error");
            }
        }
    }

    @Override 
    public boolean add(String filename, byte[] data){
        Objects.requireNonNull(filename, "Filename must be non null");
        Objects.requireNonNull(data, "Data must be non null");
        
        Path file = this.dir.resolve(filename);
        if (Files.exists(file)) 
            return false;

        try { 
            Files.write(file, data, StandardOpenOption.CREATE_NEW);
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override 
    public byte[] get(String filename) {
        Objects.requireNonNull(filename, "Filename must be non null");
        
        Path file = this.dir.resolve(filename);
        if (!Files.exists(file)) 
            return new byte[0];

        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
    
    @Override
    public boolean delete(String filename) {
        Objects.requireNonNull(filename, "Filename must be non null");
        
        Path file = this.dir.resolve(filename);
        try {
            Files.deleteIfExists(file);
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override 
    public boolean exists(String filename) {
        Objects.requireNonNull(filename, "Filename must be non null");
        
        Path file = this.dir.resolve(filename);
        return Files.exists(file);
    }


}
