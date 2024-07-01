package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.concurrent.TimeUnit;

public class Main {

        public static void main(String[] args) {
                 try {
                         TimeUnit.MILLISECONDS.sleep(300);
                 } catch (InterruptedException ignored) { }

                String address = "127.0.0.1";
                int port = 23456;

                try {
                        Socket socket = new Socket(InetAddress.getByName(address), port);
//                        DataInputStream input = new DataInputStream(socket.getInputStream());
//                        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                        System.out.println("Client started!");

                        ClientInput client = new ClientInput();
                        client.topQuestion();
                        client.sendInput(socket);

                }catch(IOException e){
                        e.printStackTrace();
                }catch (Exception e) {
                        e.printStackTrace();
                }
        }
}
