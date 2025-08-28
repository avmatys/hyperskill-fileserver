package server.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class HardDriveFileStorage implements FileStorage {
    
    private static final String PATH = "src/server/data/";
    private final Path dir;

    public HardDriveFileStorage() throws IOException {
        this(PATH);
    }

    public HardDriveFileStorage(String path) throws IOException {
        this.dir = Paths.get(path);
        if (Files.notExists(this.dir)) 
            Files.createDirectories(this.dir);
        //Check dir src/server/data - show list of files
        System.out.println("Current files in " + this.dir + ":");
        try {
            Files.list(this.dir)
                    .filter(Files::isRegularFile)
                    .forEach(p -> System.out.println(" - " + p.getFileName()));
        } catch (IOException e) {
            System.err.println("Could not list files: " + e.getMessage());
        }
    }

    @Override
    public long size(String filename) {
        Objects.requireNonNull(filename, "Filename must be non null");
        Path file = this.dir.resolve(filename);
        try {
            return Files.size(file);
        } catch (IOException e) {
            return 0;
        }
   }

    @Override 
    public boolean add(String filename, InputStream in, long size) throws IOException {
        Objects.requireNonNull(filename, "Filename must be non null");
        Objects.requireNonNull(in, "Data In stream must be non null");
        Path file = this.dir.resolve(filename);
        if (Files.exists(file)) 
            return false;
        try (OutputStream fout = Files.newOutputStream(file, StandardOpenOption.CREATE_NEW)) {
            byte[] buffer = new byte[4096];
            long remaining = size;
            while (remaining > 0) {
                int read = in.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                if (read == -1) break;
                fout.write(buffer, 0, read);
                remaining -= read;
            }
            return true;
        }
    }

    @Override 
    public boolean get(String filename, OutputStream out) throws IOException {
        Objects.requireNonNull(filename, "Filename must be non null");
        Objects.requireNonNull(out, "Data Out stream must be non null");
        Path file = this.dir.resolve(filename);
        if (Files.notExists(file)) 
            return false;
        try (InputStream fin = Files.newInputStream(file)){
            byte[] buffer = new byte[4096];
            int read;
            while((read = fin.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
            return true;
        }
    }
    
    @Override
    public boolean delete(String filename) throws IOException {
        Objects.requireNonNull(filename, "Filename must be non null");
        Path file = this.dir.resolve(filename);
        Files.deleteIfExists(file);
        return true;
    }

    @Override 
    public boolean exists(String filename) {
        Objects.requireNonNull(filename, "Filename must be non null");
        Path file = this.dir.resolve(filename);
        return Files.exists(file);
    }


}
