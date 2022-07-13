package FileTransferProtocol;

import DiscordEvents.FileIOEvent;
import LocalData.SaveAndLoad;
import javafx.scene.shape.Arc;

import java.io.*;
import java.net.Socket;

public class FileTransfer implements Runnable{
    boolean download = false;
    private String filename;
    private Socket socket;
    private String destPath;
    private FileIOEvent event;
    private boolean exists;

    public void setFilename(String filename) {
        this.filename = filename;
    }
    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }
    private FileTransferProgressEvent listener;// = new FileTransferProgressEvent() {
    //    @Override
   //     public void updateProgress(int percent) {
        //    System.out.println(percent+"%");
     //   }
   // };
    public FileTransfer(boolean download, Socket socket, FileIOEvent event, boolean exists) {
        this.download = download;
        this.socket = socket;
        this.event = event;
        this.exists = exists;
    }
    private Arc arc;
    public void setListener(FileTransferProgressEvent listener,Arc arc) {
        this.listener = listener;
        this.arc = arc;
    }
    public void downloadFile(String destPath , Socket socket, Arc arc) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        int fileNamelength = dataInputStream.readInt();
        if(fileNamelength>0){
            byte[] fileNameBytes = new byte[fileNamelength];
            dataInputStream.readFully(fileNameBytes,0,fileNameBytes.length);
            String filename = new String(fileNameBytes);
            long filecontentLength = dataInputStream.readLong();
            System.out.println("Downloading File : "+filename+"("+filecontentLength+" bytes) to "+destPath);
            if(filecontentLength>0) {
                System.out.println(destPath);
                File lol = new File(destPath.replace(destPath.split("\\\\")[destPath.split("\\\\").length-1],""));
                if(lol.exists())
                    lol.mkdir();

                File myfile = new File(destPath);
                myfile.createNewFile();

                FileOutputStream fileOutputStream = new FileOutputStream(destPath);
                long nread = 0L;
                byte[] buf = new byte[8192];
                int n;
                while((n=dataInputStream.read(buf))>0){
                    fileOutputStream.write(buf,0,n);
                    nread+=n;
                    listener.updateProgress((int)(((double)nread*100)/filecontentLength),destPath,arc);
                }
                listener.updateProgress(100,destPath,arc);
                fileOutputStream.flush();
                fileOutputStream.close();
                dataInputStream.close();
                socket.close();
            }
        }
    }
    public int uploadFile(String filename ,Socket socket,Arc arc) throws IOException {
        int id;
        FileInputStream fileInputStream = new FileInputStream(filename);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        id = dataInputStream.readInt();
        byte[] fileNameBytes = filename.getBytes();
        dataOutputStream.writeInt(filename.length());
        dataOutputStream.write(fileNameBytes);
        long fileSize = new File(filename).length();
        dataOutputStream.writeLong(fileSize);
        System.out.println("UploadingFile Size : "+fileSize);
        long nread = 0L;
        byte[] buf = new byte[8192];
        int n;
        while((n=fileInputStream.read(buf))>0){
            dataOutputStream.write(buf,0,n);
            dataOutputStream.flush();
            nread+=n;
            if(listener!=null)
            listener.updateProgress((((double)nread*100)/fileSize),filename,arc);
        }
        if(listener!=null)
        listener.updateProgress(100,filename,arc);
        fileInputStream.close();
        dataOutputStream.close();

        socket.close();
        return id;
    }
    public int up(){
        try {
            return uploadFile(filename, socket, arc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileManager.goForNext(destPath,event,exists);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
    @Override
    public void run() {
        if(!SaveAndLoad.isRunning){
            return;
        }
        if(!exists) {
            if (download) {
                try {
                    downloadFile(destPath, socket, arc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    uploadFile(filename, socket, arc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            FileManager.goForNext(destPath,event,exists);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
