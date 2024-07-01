package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        String address = "127.0.0.1";
        int port = 23456;
        boolean exit = false;
        Storage.getInstance();

        try {

            ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(address));
            System.out.println("Server started!");
            ExecutorService executor = Executors.newSingleThreadExecutor();

            while(!exit) {
                Socket socket = server.accept();
                RequestMessage request = null;
/*                DataInputStream dIn = new DataInputStream(socket.getInputStream());
                int length = dIn.readInt();
                if(length > 0) {
                    byte[] receivedBytes = new byte[length];
                    dIn.readFully(receivedBytes, 0, receivedBytes.length);
                    ByteArrayInputStream bais = new ByteArrayInputStream(receivedBytes);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    request = (RequestMessage) ois.readObject();
                }

 */
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                request = (RequestMessage) ois.readObject();

                switch(request.requestCode){
                    case "EXIT":
                        exit = true;
                        /*
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(bos);
                        out.writeObject(new ResponseMessage(500, 0));
                        out.flush();
                        byte[] bytes = bos.toByteArray();

                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                        dos.writeInt(bytes.length);
                        dos.write(bytes);

                         */
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(new ResponseMessage(500, 0));
                        oos.close();
                        //out.close();
                        socket.close();
                        continue;
                    default:
                        executor.execute(new Worker(socket, request));
                        break;
                }
            }
            executor.shutdown();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


/*



        Scanner sc = new Scanner(System.in);
        boolean exit = false;
        Storage storage = new Storage();

        while (!exit) {
            try{
                String input = sc.nextLine();
                String[] tokens = input.split(" ");
                switch (tokens[0]) {
                    case "add":
                        storage.add(tokens[1]);
                        break;
                    case "get":
                        storage.get(tokens[1]);
                        break;
                    case "delete":
                        storage.remove(tokens[1]);
                        break;
                    case "exit":
                        exit = true;
                        break;
                }
            }catch(Exception e){
                    System.out.println(e.getMessage());
            }
        }
    }

 */
}