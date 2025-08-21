package server.controller;

import java.util.Objects;
import java.util.Base64;

public class FileResponse {
    
    private final int status;
    private final byte[] data;

    public FileResponse(int status, byte[] data) {
        Objects.requireNonNull(data, "Data should be non null");
        this.status = status;
        this.data = data;
    }

    public FileResponse(int status) {
        this(status, new byte[0]);
    }

    public int getStatus(){
        return this.status;
    }
    
    public byte[] getData() {
        return data;
    }
    
    @Override
    public String toString() {
        String res = String.valueOf(this.status);
        if (this.data != null && this.data.length > 0) {
             String base64Content = Base64.getEncoder().encodeToString(this.data);
             res = res + " " + base64Content;
        }
        return res;
    }

}
