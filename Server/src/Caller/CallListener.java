package Caller;

import CallService.CallPacket;
import DataTransmit.DataPacket;
import DataTransmit.SocketDataTransfer;
import UserManagement.ResponseType;
import UserManagement.ServerResponse;
import UserManagement.TokenManager.Token;
import logger.Log4me;
import logger.LogType;

import java.io.IOException;
import java.util.HashMap;

public class CallListener implements Runnable {
    SocketDataTransfer IO;
    String token;
    public CallListener(SocketDataTransfer IO,String token) {
        this.IO = IO;
        this.token = token;
    }
    @Override
    public void run() {
        while(true){
            Object readed = IO.readData();
            if(readed instanceof DataPacket){
                DataPacket packet = (DataPacket)readed;
                if(packet.getType().equals("call_disconnect")){
                    HashMap<String , String> data = (HashMap<String, String>)((DataPacket) readed).getData();
                    String callID = data.get("callID");
                    if(CallerService.removeFromCall(token,callID)){
                        Log4me.log(LogType.ERROR, token+" left the call");
                        IO.sendData("OK");
                    }
                    else{
                        IO.sendData("CALL_DOESNT_EXIST");
                    }
                    return;
                }
            }
            else {
                CallPacket packet = (CallPacket) readed;

                CallerService.broadCast(token, packet);
            }
        }
    }
}
