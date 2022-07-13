package NotifyService;

import DataTransmit.DataPacket;
import DataTransmit.SocketDataTransfer;
import UserManagement.TokenManager.Token;
import javafx.util.Pair;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
class Pairs{
    SocketDataTransfer socketDataTransfer;
    Socket socket;
    public Pairs(SocketDataTransfer socketDataTransfer, Socket socket) {
        this.socketDataTransfer = socketDataTransfer;
        this.socket = socket;
    }
    public SocketDataTransfer getSocketDataTransfer() {
        return socketDataTransfer;
    }
    public void setSocketDataTransfer(SocketDataTransfer socketDataTransfer) {
        this.socketDataTransfer = socketDataTransfer;
    }
    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
public class NotifyBase {
    static HashMap<String , Pairs> users = new HashMap<>();
    static synchronized void addNotifySocket(String token,Socket socket,SocketDataTransfer socketDataTransfer){
        Pairs pairs = new Pairs(socketDataTransfer , socket);
        users.put(token , pairs);
    }
    public static Object notifyUser(String token,String type , Object data) throws IOException, ClassNotFoundException {
        Socket socket = users.get(token).getSocket();
        SocketDataTransfer socketDataTransfer = users.get(token).getSocketDataTransfer();
        DataPacket dataPacket = new DataPacket(type , data);
        socketDataTransfer.sendData(dataPacket);
        Object result = socketDataTransfer.readData();
        return result;
    }
    public static Object notifyUser(String token , String data) throws IOException, ClassNotFoundException {
        System.out.println("token searching for : "+token);
        Socket socket = users.get(token).getSocket();
        SocketDataTransfer socketDataTransfer = users.get(token).socketDataTransfer;
        DataPacket dataPacket = new DataPacket("String" , data);
        socketDataTransfer.sendData(dataPacket);
        Object result = socketDataTransfer.readData();
        return result;
    }
    public static Object notifyUserByUsername(String usrname , String data) throws IOException, ClassNotFoundException {
        String token = Token.getTokenByUsername(usrname).getToken();
        if(token!=null){
            return notifyUser(token , data);
        }
        return null;
    }
    public static Object notifyUserByUsername(String usrname,String type , Object data) throws IOException, ClassNotFoundException {
        String token = Token.getTokenByUsername(usrname).getToken();
        if(token!=null){
         return notifyUser(token ,type, data);
        }
        return null;
    }

}
