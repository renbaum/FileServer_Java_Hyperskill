package server;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Message implements Serializable {
    public int uniqueID = 0;
    byte[] fileContent = null;

    public Message() {
        this.uniqueID = 0;
        fileContent = null;
    }

    public Message(int uniqueID) {
        this.uniqueID = uniqueID;
    }

    public boolean readFromFile(String filePath){
        Path path = Paths.get(filePath);
        try {
            fileContent = Files.readAllBytes(path);
            return true;
        } catch (IOException e) {
            //e.printStackTrace();
            return false;
        }
    }
    public boolean writeToFile(String filePath) {
        Path path = Paths.get(filePath);
        try {
            if(fileContent == null) return false;
            Files.write(path, fileContent);
            return true;
        } catch (IOException e) {
            
        }
        return false;
    }
}
