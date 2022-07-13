package Caller;

import CallOB.CallRequest;
import DataTransmit.DataPacket;
import DataTransmit.SocketDataTransfer;
import NotifyService.NotifyBase;
import UserManagement.Server;
import UserManagement.TokenManager.Token;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class CallServer implements Runnable{
    ServerSocket serverSocket ;

    public CallServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while(true){
            try {
                Socket yaroo = serverSocket.accept();
                Thread thread = new Thread(new CallClientHandler(yaroo));
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
