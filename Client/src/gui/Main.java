package gui;

import DataTransmit.SocketDataTransfer;
import DiscordEvents.CallConnected;
import DiscordEvents.CallPacketArrived;
import DiscordEvents.NewIncomingCall;
import FileTransferProtocol.FileManager;
import NotifyService.Listener;
import UserManagement.*;
import com.sun.jna.platform.win32.User32;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.sound.sampled.LineUnavailableException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Scanner;


public class Main extends Application{
    private static String token;
    static SocketDataTransfer mainSocketIO;
    static Thread notifyThread = new Thread();
    public static String ip = "localhost";
    public static Scanner sc = new Scanner(System.in);
    public static Socket callService;
    static SocketDataTransfer callIO;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginForm.fxml"));
        Scene scene = null;
        try {
            Parent root = loader.load();
            scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setResizable(false);
        stage.setTitle("Datcord");
        stage.getIcons().add(new Image("file:discord.png"));
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);
        stage.centerOnScreen();
        stage.show();
    }
    public static CallConnected callConnectedEvent;
    public static CallPacketArrived listenForCallPackets;
    public static NotifyService.Listener listener;
    public static NewIncomingCall incomingCallEvent;
    public static Request request;
    public static void main(String[] args) throws LineUnavailableException, IOException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException {
        try {
            Audio.AudioHandler.startSpeaker();
        }
        catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Couldn't config speakers");
        }
        System.setProperty("java.net.preferIPv4Stack", "true");
        Socket socket = new Socket(ip , 8395);
        Socket notifyService = new Socket(ip , 8397);
        callService = new Socket(ip , 8399);
        callIO = new SocketDataTransfer(callService , false , null);
        System.out.println("Connected to server");
        request = new Request(socket,token);
        mainSocketIO = new SocketDataTransfer(socket , true,Request.symmetric);
        request.setMainIO(mainSocketIO);
        request.setCallIO(callIO);
        token = mainSocketIO.readString().split("=")[1];
        request.setToken(token);
        callIO.sendData(token);
        FileManager.setToken(token);
        System.out.println("TOKEN = "+token);
        listener = new Listener(notifyService,token,callService,callIO);
        notifyThread = new Thread(listener);
        notifyThread.start();
        request.setCallService(callService);
        //request.signIn("matin" , "12345678aA");
        launch(args);
    }


}
