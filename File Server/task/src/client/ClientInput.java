package client;

import server.RequestMessage;
import server.ResponseMessage;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class ClientInput{
    Scanner sc = null;
    RequestMessage msg = null;
    Path root;


    public ClientInput(){
        sc = new Scanner(System.in);
        root =  Path.of(System.getProperty("user.dir"), "src", "client", "data");
        if(!doesFolderExist(root.toString())) createFolder(root.toString());
    }

    private boolean doesFolderExist(String folderName){
        return Files.exists(root.resolve(folderName));
    }

    private void createFolder(String folderName){
        File directory = new File(root.toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public void topQuestion(){
        msg = new RequestMessage();
        enterAction();
        if(msg.requestCode.equals("EXIT")) return;

        enterFile();
    }

    private void enterFile() {
        switch(msg.requestCode){
            case "PUT":
                getFileToSend();
                break;
            case "GET", "DELETE":
                getFileOrUID();
                break;
        }
    }

    private void getFileOrUID() {
        System.out.printf("Do you want to %s the file by name or by id (1 - name, 2 - id): ",
                msg.requestCode.toLowerCase());
        int choice = sc.nextInt();
        sc.nextLine();
        String filename = "";
        int UID = 0;
        switch(choice){
            case 1:
                System.out.print("Enter name of the file: ");
                filename = sc.nextLine();
                break;
            case 2:
                System.out.print("Enter id: ");
                UID = sc.nextInt();
                sc.nextLine();
                break;
        }
        msg.uniqueID = UID;
        msg.fileName = filename;
    }

    private void getFileToSend() {
        System.out.print("Enter name of the file: ");
        String fileName = sc.nextLine();
        System.out.print("Enter name of the file to be saved on server: ");
        String fileOnServer = sc.nextLine();
        if(fileOnServer.equals("")) fileOnServer = fileName;
        msg.fileName = fileOnServer;
        msg.readFromFile(root.resolve(fileName).toString());
    }

    private void enterAction(){
        System.out.print("Enter action (1 - get a file, 2 - create a file, 3 - delete a file): ");
        String action = sc.nextLine();
        switch(action){
            case "exit":
                msg.requestCode = "EXIT";
                break;
            case "1":
                msg.requestCode = "GET";
                break;
            case "2":
                msg.requestCode = "PUT";
                break;
            case "3":
                msg.requestCode = "DELETE";
                break;
        }

    }

    public void sendInput(Socket socket) {
        try {
/*            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(msg);
            out.flush();
            byte[] yourBytes = bos.toByteArray();

            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            dOut.writeInt(yourBytes.length);
            dOut.write(yourBytes);


 */
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(msg);
            System.out.println("The request was sent.");
/*
            DataInputStream dIn = new DataInputStream(socket.getInputStream());
            int length = dIn.readInt();
            ResponseMessage response = null;

            if (length > 0) {
                byte[] receivedBytes = new byte[length];
                dIn.readFully(receivedBytes, 0, receivedBytes.length);

                // Convert byte array to object
                ByteArrayInputStream bis = new ByteArrayInputStream(receivedBytes);
                ObjectInputStream in = new ObjectInputStream(bis);
                response = (ResponseMessage)in.readObject();
            }

 */
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ResponseMessage response = (ResponseMessage) in.readObject();
            switch(response.responseCode){
                case 200:
                    switch(msg.requestCode){
                        case "GET":
                            System.out.print("The file was downloaded! Specify a name for it: ");
                            String downloadedFileName = sc.nextLine();
                            Path downloadedFilePath = root.resolve(downloadedFileName);
                            response.writeToFile(downloadedFilePath.toString());
                            System.out.println("File saved on the hard drive!");
                            break;
                        case "PUT":
                            System.out.printf("Response says that file is saved! ID = %d\n", response.uniqueID);
                            break;
                        case "DELETE":
                            System.out.println("The response says that this file was deleted successfully!");
                    }
                    break;
                case 403:
                    System.out.println("The response says that creating the file was forbidden!");
                    break;
                case 404:
                    System.out.println("The response says that this file is not found!");
                    break;
                case 500:
                    break;
                default:
                    System.out.println("Unknown response code: " + response.responseCode);
                    break;
            }
        }catch(IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                socket.close();
            }catch(IOException e){}
        }
    }
}
