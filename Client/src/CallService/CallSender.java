package CallService;

import Audio.AudioHandler;
import DataTransmit.DataPacket;
import LocalData.SaveAndLoad;

import javax.sound.sampled.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

public class CallSender implements Runnable{
    private String token;
    private  Line.Info infoo;
    public byte[] buffer;
    static AudioInputStream ais;
    ObjectOutputStream out;
    String callID;
    boolean voice;
    public CallSender(ObjectOutputStream out, String token, String callID, boolean voice)  {
        this.out = out;
        this.token = token;
        this.callID = callID;
        this.voice = voice;
    }
    synchronized void send(CallPacket data , ObjectOutputStream out) throws IOException {
        out.writeObject(data);
    }
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
    @Override
    public void run() {
        if(!SaveAndLoad.isRunning){
            return;
        }
        byte[] data = new byte[128];
        CallPacket packet;
        screencapture4j.ScreenCapture screenCaputre = new screencapture4j.ScreenCapture(); // <yourWindowName> -> change to the title of you window, you want to capture!
        BufferedImage image = null;
        if(!voice){
            final CallPacket[] packet2 = new CallPacket[1];
            while(true){
                image = resize(screenCaputre.current_image(),720,405);
                try {
                    packet2[0] = new CallPacket(null , AudioHandler.imageToByteArray(image),token,callID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    send(packet2[0],out);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        else{
            while (true) {
                data = AudioHandler.getBytes();
                packet = new CallPacket(data, null, token, callID);
                try {
                    send(packet,out);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
