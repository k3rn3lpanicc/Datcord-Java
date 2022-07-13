package CallService;

import DataTransmit.SocketDataTransfer;
import LocalData.SaveAndLoad;
import gui.Main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CallListener implements Runnable{
    private String token;
    ObjectInputStream in;
    ObjectOutputStream out;
    private int port;
    SocketDataTransfer mainIO;
    Socket client = null;
    String callID;
    boolean voice;
    public CallListener(Socket so, String token, SocketDataTransfer mainIO, String callID, boolean voice){
        this.token = token;
        client = so;
        this.mainIO = mainIO;
        this.callID = callID;
        this.voice = voice;
    }
    @Override
    public void run() {
        out = mainIO.getOOUT();
        in = mainIO.getOIN();
        Thread thread = new Thread(new CallSender(out,token,callID,voice));
        thread.start();
        try {
            Object readed = in.readUnshared();
            if(readed!=null){
                if(Main.callConnectedEvent!=null)
                    Main.callConnectedEvent.handle(((CallPacket)readed).getCallID());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        while(true){
            if(!SaveAndLoad.isRunning){
                return;
            }
            try {
                Object readed = in.readUnshared();
                if(readed!=null) {
                    CallPacket packet = (CallPacket) readed;
                    Audio.AudioHandler.toSpeaker(packet.getVoiceData());
                    if(packet.getScreenImageDate()!=null && Main.listenForCallPackets!=null)
                        Main.listenForCallPackets.handle(packet);
                    //todo : put the picture to specified imageview*****
                }
                else{
                    System.out.println("Call Ended.");
                    return;
                }
            }
            catch (Exception ignored){
                System.out.println("Call Ended.");
                return;
            }
        }
    }
}
