package Caller;
import CallService.CallPacket;
import DataTransmit.SocketDataTransfer;
import UserManagement.TokenManager.Token;
import logger.Log4me;
import logger.LogType;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

public class Call {
    HashMap<String, SocketDataTransfer> users = new HashMap<>();
    HashSet<String> tokens = new HashSet<>();
    CallType type;
    public Call(CallType type) {
        this.type = type;
    }
    void addToCall(String token, SocketDataTransfer socketIO){
        users.put(token , socketIO);
        tokens.add(token);
        Thread thread = new Thread(new CallListener(socketIO, token));
        thread.start();
    }
    void broadcastToUsers(String token , CallPacket packet){
        for(String t : users.keySet()){
            if(!t.equals(token)){
                try {
                    users.get(t).sendData2(packet);
                }
                catch (Exception e){
                    users.remove(t);
                    tokens.remove(t);
                    Log4me.log(LogType.ERROR,t+" Disconnected From call");
                }
            }
        }
    }

    public void removeFromCall(String token) {
        users.remove(token);
        tokens.remove(token);
    }
}
