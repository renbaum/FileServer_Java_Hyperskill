package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static server.Storage.getInstance;

public class Worker implements Runnable{
    Socket socket;
    private RequestMessage requestMessage;
    ObjectOutputStream out;

    public Worker(Socket s, RequestMessage reqMsg) throws IOException {
        this.socket = s;
        this.requestMessage = reqMsg;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
        }catch(IOException e){

        }
    }

    @Override
    public void run() {
        try {
            switch(requestMessage.requestCode){
                case "GET":
                    handleGetRequest();
                    break;
                case "PUT":
                    handlePutRequest();
                    break;
                case "DELETE":
                    handleDeleteRequest();
                    break;
                default:
                    handleDefaultRequest();
                    break;
            }
            socket.close();
        }catch (IOException e){

        }
    }

    private void handleDefaultRequest() {
        try {
            out.writeObject(new ResponseMessage(403, 0));
        }catch(IOException e){

        }
    }

    private void handleDeleteRequest() {
        ResponseMessage resp = new ResponseMessage(200, 0);
        try {
            if (!getInstance().remove(requestMessage)) {
                resp.responseCode = 404;
            }
            out.writeObject(resp);
        }catch(IOException e){

        }
    }

    private void handlePutRequest() {
        ResponseMessage resp = new ResponseMessage(200, 0);
        try {
            if (!getInstance().add(requestMessage, resp)) {
                resp.responseCode = 403;
            }
            out.writeObject(resp);
        } catch (IOException e) {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGetRequest() {
        ResponseMessage resp = new ResponseMessage(200, 0);
        try {
            if(!Storage.getInstance().get(requestMessage, resp)){
                resp.responseCode = 404;
            }
            out.writeObject(resp);
        } catch (IOException e) {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
