package server.contoller;

import server.util.JsonUtil;
import server.storage.FileStorage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class FileController {
    
    private final static String DEFAULT_METADATA = "src/server/data/metadata.json";
    private final static String DEFAULT_LOCK = "src/server/data/lock";
    
    private final ConcurrentHashMap<String, ReentrantLock> fileLocks = new ConcurrentHashMap<>();
    private final Path metadataFile = Paths.get(DEFAULT_METADATA);
    private final Path lockDir = Paths.get(DEFAULT_LOCK);
    private final Metadata metadata = new Metadata();

    private final FileStorage storage;
    private final AtomicLong nextId;


    public FileController(FileStorage storage) throws IOException {
        this.storage = storage;
        this.loadMetadata();
        this.nextId = new AtomicLong(this.metadata.latestId);
        this.initLock();
        registerShutdownHook();
    }

    public static class Metadata {
        public Map<String, String> idToFilename = new ConcurrentHashMap<>();
        public Map<String, String> filenameToId = new ConcurrentHashMap<>();
        public long latestId = 0; // new field
    }

    private synchronized void loadMetadata() throws IOException {
        if (Files.notExists(metadataFile)) return;
        String content = Files.readString(metadataFile);
        Metadata loaded = JsonUtil.fromJson(content);
        metadata.idToFilename.putAll(loaded.idToFilename);
        metadata.filenameToId.putAll(loaded.filenameToId);
        metadata.latestId = loaded.latestId;
    }

    private synchronized void saveMetadata() throws IOException {
        metadata.latestId = nextId.get(); // persist the latest ID
        String json = JsonUtil.toJson(metadata);
        Files.writeString(metadataFile, json);
    }
    
    private synchronized void initLock() throws IOException {
        if(Files.exists(this.lockDir)) return;
        Files.createDirectories(this.lockDir);
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.walk(lockDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                System.err.println("Failed to clean up lock files: " + e.getMessage());
            }
        }));
    }

    private FileLock acquireFileLock(String filename) {
        Path lockFile = this.lockDir.resolve(filename + ".lock");
        try {
            RandomAccessFile file = new RandomAccessFile(lockFile.toFile(), "rw");
            FileChannel channel = file.getChannel();
            return channel.tryLock(); // non-blocking lock attempt
        } catch (IOException e) {
            return null;
        }
    }

    private void releaseFileLock(FileLock lock) {
        if (lock == null) return;
        try {
            lock.release();
            lock.channel().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFilenameById(String id) {
        Objects.requireNonNull(id, "ID can't be null");
        return metadata.idToFilename.getOrDefault(id, null);
    }

    public boolean existsByFilename(String filename) {
        return metadata.filenameToId.containsKey(filename);
    }

    public String upload(String filename, long size, InputStream in) throws IOException {
        ReentrantLock lock = fileLocks.computeIfAbsent(filename, k -> new ReentrantLock());
        lock.lock();
        try {
            if (metadata.filenameToId.containsKey(filename)) {
                return null;
            }
            FileLock diskLock = acquireFileLock(filename);
            if (diskLock == null) {
                return null;
            }
            try {
                long idNum = nextId.incrementAndGet();
                String id = String.valueOf(idNum);
                if (storage.add(filename, in, size)) {
                    metadata.idToFilename.put(id, filename);
                    metadata.filenameToId.put(filename, id);
                    saveMetadata();
                    return id;
                }
                return null;
            } finally {
                releaseFileLock(diskLock);
            }
        } finally {
            lock.unlock();
            if (lock.getQueueLength() == 0) {
                fileLocks.remove(filename, lock);
            }
        }
    }

    public boolean delete(String filename) throws IOException {
        ReentrantLock lock = fileLocks.computeIfAbsent(filename, k -> new ReentrantLock());
        lock.lock();
        try {
            if (!this.storage.exists(filename)) {
                return false;
            }
            FileLock diskLock = acquireFileLock(filename);
            if (diskLock == null) {
                return false;
            }
            try {
                if (this.storage.delete(filename)) {
                    String id = metadata.filenameToId.remove(filename);
                    if (id != null) {
                        metadata.idToFilename.remove(id);
                        saveMetadata();
                    }
                    return true;
                }
                return false;
            } finally {
                releaseFileLock(diskLock);
            }
        } finally {
            lock.unlock();
            if (lock.getQueueLength() == 0) {
                fileLocks.remove(filename, lock);
            }
        }
    }

    public Runnable download(String filename, OutputStream out) throws IOException {
        ReentrantLock lock = fileLocks.computeIfAbsent(filename, k -> new ReentrantLock());
        lock.lock();
        try {
            if (!this.storage.exists(filename)) {
                return null; // File doesn't exist, return early
            }
            FileLock diskLock = acquireFileLock(filename);
            if (diskLock == null) {
                return null;
            }
            return () -> {
                try {
                    this.storage.get(filename, out);
                } catch(IOException e) {
                    throw new RuntimeException("File can't be downloaded", e);
                } finally {
                    // Release the on-disk lock after the download is complete
                    releaseFileLock(diskLock);
                }
            };
        } finally {
            lock.unlock();
            if (lock.getQueueLength() == 0) {
                fileLocks.remove(filename, lock);
            }
        }
    }
    
    public long size(String id) {
        return storage.size(id);
    }

}
