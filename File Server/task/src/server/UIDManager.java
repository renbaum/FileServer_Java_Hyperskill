package server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UIDManager implements Serializable {
    transient String fileName;
    transient ReentrantReadWriteLock lock;
    
    int currentUID = 0;
    Map<String, Integer> mapFileToUID;
    Map<Integer, String> mapUIDToFile;
    
    public UIDManager() {
        mapFileToUID = new HashMap<>();
        mapUIDToFile = new HashMap<>();
    }
    
    public static UIDManager deserialize(String fileName) {
        UIDManager myClass = null;
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            myClass = (UIDManager) in.readObject();
            in.close();
            fileIn.close();
        }catch(Exception e){
            myClass = new UIDManager();
        }finally {
            myClass.fileName = fileName;
            myClass.createLock();        
        }
        return myClass;
    }
    
    private void createLock(){
        lock = new ReentrantReadWriteLock();
    }
        
    
     private void serialize() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(fileName);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(this);
        out.close();
        fileOut.close();
    }    
    
    int getNewUID(String fileName){
        lock.writeLock().lock();
        try {
            currentUID++;
            addUniqueID(fileName, currentUID);
            serialize();
            return currentUID;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    String getFileName(int uid) {
        lock.readLock().lock();
        try {
            return mapUIDToFile.get(uid);
        }finally {
            lock.readLock().unlock();
        }
    }
    
    int getUID(String fileName) {
        lock.readLock().lock();
        try {
            return mapFileToUID.get(fileName);
        }finally {
            lock.readLock().unlock();
        }
    }
    
    public boolean addUniqueID(String fileName, int uid){
        lock.writeLock().lock();
        try {
            mapFileToUID.put(fileName, uid);
            mapUIDToFile.put(uid, fileName);
            return true;
        }finally {
            lock.writeLock().unlock();
        }
    }

    public void removeUniqueID(String fileName){
        lock.writeLock().lock();
        try {
            int id = mapFileToUID.remove(fileName);
            mapUIDToFile.remove(id);
        }finally {
            lock.writeLock().unlock();
        }
    }
    
    public void removeUniqueID(int uid){
        lock.writeLock().lock();
        try {
            String file = mapUIDToFile.remove(uid);
            mapFileToUID.remove(file);
        }finally {
            lock.writeLock().unlock();
        }
    }

}
