package Caller;

import CallOB.CallRequest;
import CallService.CallPacket;
import DataTransmit.DataPacket;
import DataTransmit.SocketDataTransfer;
import NotifyService.NotifyBase;
import UserManagement.TokenManager.Token;
import logger.Log4me;
import logger.LogType;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class CallClientHandler implements Runnable{
    Socket socket;
    SocketDataTransfer IO;
    String token;
    public CallClientHandler(Socket socket) {
        this.socket = socket;
        IO = new SocketDataTransfer(socket,false,null);
    }
    @Override
    public void run() {
        //get token from user
        String token = IO.readString();
        DataPacket packet = null;
        packet = IO.readPacket();
        if(packet==null){
            System.out.println("Client Disconnected from CallSocket");
            return;
        }
        HashMap<String, String> data = (HashMap<String, String>) packet.getData();
        String id = null;
        System.out.println(packet.getType());
        Log4me.log(LogType.WARNING,packet.getType());
        if(packet.getType().equals("JoinCall")){
            String usertoken = data.get("token");
            String callid = data.get("call");
            CallerService.addToCall(usertoken,IO,callid);
            Log4me.log(LogType.INFO , Token.getToken(usertoken).getUsername()+" Joined call");
            return;
        }
        if (data.get("call").equals("create")) { //todo :
            String callID = CallerService.addCall(CallType.directCall, token, IO);
            System.out.println("CallHandler@Server:~$ "+"Call Created > "+Token.getToken(token).getUsername()+" | CallID : "+callID);

            IO.sendData(callID);
            try {
                String callToToken = Token.getTokenByUsername(data.get("username")).getToken();
                CallRequest request = new CallRequest(Token.getToken(token).getUsername(), callID);

                try {
                    NotifyBase.notifyUser(callToToken, "Call", request);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
                System.out.println("User is not online to be called");
            }
        } else {
            if (data.get("accepted").equals("true")) {
                String addToCallID = data.get("call");
                String yarootoken = data.get("token");
                CallerService.addToCall(yarootoken, IO, addToCallID);
                System.out.println("CallHandler@Server:~$ "+Token.getToken(yarootoken).getUsername()+" Joined Call "+addToCallID);
            }
        }
    }

}
