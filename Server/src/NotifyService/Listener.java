package NotifyService;

import DataTransmit.SocketDataTransfer;
import UserManagement.TokenManager.Token;
import UserManagement.TokenManager.TokenObj;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Listener implements Runnable{
    ServerSocket notifyServerSocket;

    public Listener(ServerSocket notifyServerSocket) {
        this.notifyServerSocket = notifyServerSocket;
    }
    @Override
    public void run() {
        try {
            while(true){
                Socket socket = notifyServerSocket.accept();
                System.out.println("\t\tNew notify connection arrived : "+socket.getLocalAddress()+":"+ socket.getPort());
                SocketDataTransfer socketDataTransfer = new SocketDataTransfer(socket , false ,  null);
                String token = socketDataTransfer.readString();
                socketDataTransfer.sendData("OK");
                NotifyBase.addNotifySocket(token, socket,socketDataTransfer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
