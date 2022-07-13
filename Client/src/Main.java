import DataTransmit.SocketDataTransfer;
import LocalData.SaveAndLoad;
import NotifyService.Listener;
import FileTransferProtocol.FileManager;
import UserManagement.Request;
import UserManagement.UserStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.sound.sampled.LineUnavailableException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.io.IOException;
import java.net.Socket;


public class Main {
    private static String token;
    static SocketDataTransfer mainSocketIO;
    static Thread notifyThread = new Thread();
    static String ip = "localhost";
    public static Scanner sc = new Scanner(System.in);
    public static Socket callService;
    static SocketDataTransfer callIO;
    public static void main(String[] args) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, ClassNotFoundException, LineUnavailableException {
        Audio.AudioHandler.startSpeaker();
        System.setProperty("java.net.preferIPv4Stack", "true");
        Socket socket = new Socket(ip , 8395);
        Socket fileService = new Socket(ip , 8396);
        Socket notifyService = new Socket(ip , 8397);
        callService = new Socket(ip , 8399);
        callIO = new SocketDataTransfer(callService , false , null);
        System.out.println("Connected to server");
        Request request = new Request(socket,token);
        mainSocketIO = new SocketDataTransfer(socket , true,Request.symmetric);
        request.setMainIO(mainSocketIO);
        request.setCallIO(callIO);
        token = mainSocketIO.readString().split("=")[1];
        request.setToken(token);
        callIO.sendData(token);
        FileManager.setToken(token);
        System.out.println("TOKEN = "+token);
        NotifyService.Listener listener = new Listener(notifyService,token,callService,callIO);
        notifyThread = new Thread(listener);
        notifyThread.start();
        DiscordApp.run(socket , fileService , notifyService,request,token,callService);
    }
}
