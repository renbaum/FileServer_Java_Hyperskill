package server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Storage{
    Path root;
    private List<String> fileNames = new ArrayList<String>();
    private UIDManager uid;
    private static Storage instance = null;

    public static Storage getInstance(){
        if(instance == null){
            instance = new Storage();
        }
        return instance;
    }

    private Storage(){
        root =  Path.of(System.getProperty("user.dir"), "src", "server", "data");
        Path pathUID = Path.of(System.getProperty("user.dir"), "src", "server");
        if(!doesFolderExist(root.toString())) createFolder(root.toString());
        uid = UIDManager.deserialize(pathUID.resolve("data.uid").toString());
    }

    private boolean doesFileExist(String fileName){
        return Files.exists(root.resolve(fileName));
    }

    private boolean doesFolderExist(String folderName){
        return Files.exists(root.resolve(folderName));
    }

    private void createFolder(String folderName){
        File directory = new File(root.toString());
        if (!directory.exists()) {
            boolean result = directory.mkdirs();
        }
    }
    private boolean createFile(RequestMessage msg, ResponseMessage resp){
        Path path = root.resolve(msg.fileName);
        resp.uniqueID = uid.getNewUID(msg.fileName);

        return msg.writeToFile(path.toString());
    }

    public boolean add(RequestMessage msg, ResponseMessage resp) {
        if(!getFileName(msg)) return false;
        if(doesFileExist(msg.fileName)){
            return false;
        }
        return createFile(msg, resp);
    }

    public boolean remove(RequestMessage msg) {
        try {
            if(!getFileName(msg)) return false;
            Files.delete(root.resolve(msg.fileName));
            uid.removeUniqueID(msg.fileName);
        }catch (IOException e){
            return false;
        }
        return true;
    }

    public boolean get(RequestMessage req, ResponseMessage msg) {
        if(!getFileName(req)) return false;
        if(!doesFileExist(req.fileName)) return false;
        Path path = root.resolve(req.fileName);
        if(!msg.readFromFile(path.toString())){
            msg.responseCode = 404;
            return false;
        }
        return true;
    }

    private boolean getFileName(RequestMessage msg){
        if(!msg.fileName.isEmpty()) return true;
        if(msg.uniqueID <= 0) return false;
        String fileName = uid.getFileName(msg.uniqueID);
        if(fileName == null || fileName.isEmpty()) return false;
        msg.fileName = fileName;
        return true;
    }
}
