package server;


import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestMessage extends Message implements Serializable {

    public String fileName;
    public String requestCode;

    public RequestMessage() {
        //super();
        this.fileName = "";
        this.requestCode = "";
    }

    public RequestMessage(String requestCode, int uniqueID, String fileName) {
        super(uniqueID);
        this.fileName = fileName;
        this.requestCode = requestCode;
    }
}

