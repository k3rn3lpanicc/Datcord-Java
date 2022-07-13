package Audio;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AudioHandler {
    static AudioInputStream ais;
    static AudioFormat speakerFormat;
    static float rate = 44100.0f;
    static DataLine.Info info;
    static SourceDataLine sourceDataLine;
    static TargetDataLine line;
    static Robot robot;
    static Rectangle scsize;
    static ArrayList<byte[]> images = new ArrayList<>();
    public static void startSpeaker() throws LineUnavailableException {
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        speakerFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,44100,16,2,4,44100,true);
        info = new DataLine.Info(SourceDataLine.class, speakerFormat);
        DataLine.Info info2 = new DataLine.Info(TargetDataLine.class, speakerFormat);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(speakerFormat);
        sourceDataLine.start();
        line = (TargetDataLine) AudioSystem.getLine(info2);
        line.open(speakerFormat);
        line.start();
    }
    public static byte[] getBytes(){
        byte[] data = new byte[128];
        line.read(data, 0, data.length);
        return data;
    }
    public static void toSpeaker(byte[] sound){
        try
        {
            if(sound!=null)
            sourceDataLine.write(sound, 0, sound.length);
        } catch (Exception e) {
            System.out.println("Not working in speakers...");
            e.printStackTrace();
        }

    }
    public static byte[] imageToByteArray(BufferedImage image) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    public static BufferedImage byteArrayToImage(byte[] imageArray) throws IOException
    {
        return ImageIO.read(new ByteArrayInputStream(imageArray));
    }


}
