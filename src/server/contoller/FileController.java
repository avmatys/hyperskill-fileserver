package server.controller;

import server.storage.FileStorage;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class FileController {
    
    private final static String DEFAULT_METADATA = "src/server/data/metadata.json";
    private final static String DEFAULT_LOCK = "src/server/data/lock";
    
    private final FileStorage storage;
    private final Path metadataFile;
    private final Path lockDir;
    private final ObjectMapper mapper;
    private final Metadata metadata;

    public FileController(FileStorage storage) throws IOException {
        this.storage = storage;
        this.mapper = new ObjectMapper();
        this.metadata = new Metadata();
        this.metadataFile = Paths.get(DEFAULT_METADATA);
        this.lockDir = Paths.get(DEFAULT_LOCK);
        this.loadMetadata();
        this.initLock();
    }

    private static class Metadata {
        public Map<String, String> idToFilename = new ConcurrentHashMap<>();
        public Map<String, String> filenameToId = new ConcurrentHashMap<>();
    }

    private synchronized void loadMetadata() throws IOException {
        if(Files.notExists(metadataFile)) return;
        Metadata loaded = mapper.readValue(metadataFile.toFile(), Metadata.class);
        metadata.idToFilename.putAll(loaded.idToFilename);
        metadata.filenameToId.putAll(loaded.filenameToId);
    }

    private synchronized void saveMetadata() throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(metadataFile.toFile(),metadata);
    }
    
    private synchronized void initLock() throws IOException {
        if(Files.exists(this.lockDir)) return;
        Files.createDirectories(this.lockDir);
    }

    private Path getLockFile(String filename) {
        return this.lockDir.resolve(filename + ".lock");
    }

    private boolean acquireLock(String filename) {
        Path lockFile = this.getLockFile(filename);
        try {
            Files.createFile(lockFile);
            return true;
        } catch(IOException e) {
            return false;
        }
    }

    private void releaseLock(String filename) {
        Path file = this.getLockFile(filename);
        try {
            Files.deleteIfExists(file); 
            return true;
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getIdByFilename(String filename) {
        Objects.requireNonNull(filename, "Filename can't be null");
        return metadata.filenameToId.getOrDefault(filename, null);
    }

    public boolean exists(String id) {
        return metadata.idToFilename.containsKey(id);
    }

    public String upload(String filename, long size, InputStream in) throws IOException {
        if (!acquireLock(filename)) return null;
        try {
            if (metadata.filenameToId.containsKey(filename)) return null;
            String id = UUID.randomUUID().toString();
            if (storage.add(id, size, in)) {
                metadata.idToFilename.put(id, filename);
                metadata.filenameToId.put(filename, id);
                saveMetadata();
                return id;
            }
            return null;
        } finally {
            releaseLock(filename);
        }
    }

    public boolean delete(String id) throws IOException {
        if (!acquireLock(id)) return false;
        try {
            if (!this.storage.exists(id)) return false;
            return this.storage.delete(id);
        } finally {
            releaseLock(id);
        }
    }

    public Runnable download(String id, OutputStream out) throws IOException {
        if (!acquireLock(id)) return null;
        if (!this.storage.exists(filename)) return null;
        return () -> {
            try {
                this.storage.download(id, out);
            } catch(IOException) {
                throw new RuntimeException("File can't be downloaded");
            } finally {
                releaseLock(id);
            }
        }
    }

}
