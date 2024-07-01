package server;

import java.io.Serializable;

public class ResponseMessage extends Message implements Serializable {
    public int responseCode;

    public ResponseMessage(int responseCode,int uniqueID) {
        super(uniqueID);
        this.responseCode = responseCode;

    }
}
