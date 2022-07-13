package DataTransmit;

import EncryptionModel.Symmetric;
import UserManagement.ServerResponse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;

public class SocketDataTransfer {
    ObjectInputStream OIN;
    ObjectOutputStream OOUT;
    Socket user;
    boolean useCrypto;
    String secretKey;
    Symmetric symmetric;
    public SocketDataTransfer(Socket userSocket, boolean useCrypto,Symmetric symmetric) {
        this.user = userSocket;
        this.useCrypto = useCrypto;
        try {
            OOUT = new ObjectOutputStream(userSocket.getOutputStream());
            OIN = new ObjectInputStream(user.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.symmetric = symmetric;

    }

    public String readString() {
        try {
            DataPacket dp = readPacket();
            if(dp.getType().equals("String")){
                String encoded = (String)(dp).getData();
                return useCrypto?symmetric.decrypt(encoded):encoded;
            }
            return null;
        }catch (Exception ex){
            //ex.printStackTrace();
            return null;
        }
    }
    public ServerResponse getResponse(){
        return (ServerResponse)readData();
    }
    public Object readData(){
        try {
            return OIN.readObject();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendData(String data) {
        String encodedMessage = useCrypto?symmetric.encrypt(data):data;
        Object oo = encodedMessage;
        DataPacket dataPacket = new DataPacket("String" , oo);
        sendData(dataPacket);
    }
    public void sendData(Object object)  {
        try {
            OOUT.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            OOUT.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public DataPacket readPacket(){
        return (DataPacket) readData();
    }

    public ObjectInputStream getOIN() {
        return OIN;
    }

    public ObjectOutputStream getOOUT() {
        return OOUT;
    }
}
