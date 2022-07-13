package NotifyService;

import CallOB.*;
import CallService.CallListener;
import DataTransmit.DataPacket;
import DataTransmit.SocketDataTransfer;
import DiscordEvents.NewChatCreated;
import DiscordEvents.NewMessageArrivedEvent;
import LocalData.SaveAndLoad;
import UserManagement.Request;
import UserManagement.*;
import gui.Main;
import gui.MainForm;
import gui.emojiset.Emojies;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Listener implements Runnable{
    Socket notifyService;
    String token;
    public SocketDataTransfer IO;
    SocketDataTransfer callIO;
    public Socket callSocket;
    ArrayList<NewMessageArrivedEvent> messageListeners = new ArrayList<>();
    public MainForm mainForm;
    public void addMessageListener(NewMessageArrivedEvent listener){
        messageListeners.add(listener);
    }
    private void fireMessageListeners(DiscordChat chat , Message message) throws IOException {
        for(NewMessageArrivedEvent listener : messageListeners) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        listener.handle(chat, message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
        System.out.println("end");
    }
    public NewChatCreated newChatListener;
    public Listener(Socket notifyService, String token,Socket callSocket,SocketDataTransfer callIO) {
        this.notifyService = notifyService;
        this.token = token;
        IO = new SocketDataTransfer(notifyService , false , null);
        this.callSocket = callSocket;
        this.callIO = callIO;
    }
    public static String red(String s){
        return "\u001B[31m"+s+"\u001B[0m";
    }
    public static String green(String s){
        return "\u001B[32m" +s+"\u001B[0m";
    }
    public String cyan(String s){
        return "\u001B[36m"+s+"\u001B[0m";
    }
    public static String yellow(String s){
        return "\u001B[33m"+s+"\u001B[0m";
    }
    @Override
    public void run() {
        IO.sendData(token);
        String isOK = IO.readString();
        if(isOK.equals("OK")){
            //System.out.println("Accepted");
            while(true){
                if(!SaveAndLoad.isRunning){
                    System.out.println("lmao");
                    return;
                }
                try {
                    DataPacket notification = IO.readPacket();
                    if(notification.getType().equals("String")){
                        //todo
                        System.out.println((String)notification.getData());
                        if(notification.getData().equals("NewDirectChatCreated")) {
                            newChatListener.onChatCreated();
                        }
                        IO.sendData("OK");
                    }

                    else{
                        switch (notification.getType()){
                            case "Call":
                                //todo :
                                CallOB.CallRequest request = (CallOB.CallRequest)notification.getData();
                                System.out.println("Incoming Call from : "+request.getUsername());
                                Main.incomingCallEvent.handle(request,IO);

                                break;
                            case "ServerCreated":
                                DiscordServer server = (DiscordServer)notification.getData();
                                IO.sendData("OK");
                                System.out.println("notified");
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        mainForm.addServer(server);

                                    }
                                });
                                break;

                            case "ChatMessage":
                                Message message = (Message)notification.getData();
                                IO.sendData("OK");
                                DataPacket notification2 = IO.readPacket();
                                DiscordChat chat = (DiscordChat)notification2.getData();
                                IO.sendData("OK");

                                if(message instanceof TextMessage){
                                    System.out.println(green(message.getFromUsername())+ " : "+yellow(((TextMessage) message).getContent())+"                       "+cyan(message.getDate()));
                                    String messageS = ((TextMessage) message).getContent();
                                    Pattern myPattern = Pattern.compile(":[a-z]{1,}:");
                                    Matcher m = myPattern.matcher(messageS);
                                    while(m.find()){
                                        messageS = messageS.replace(m.group(0),Emojies.getEmoji(m.group(0).replace(":","")));
                                    }
                                    ((TextMessage)message).setContent(messageS);
                                }
                                fireMessageListeners(chat,message);
                        }
                    }



                } catch (Exception ex){
                    //ex.printStackTrace();
                    break;
                }
            }
        }
        else{
            System.out.println(isOK);
        }
    }
}
