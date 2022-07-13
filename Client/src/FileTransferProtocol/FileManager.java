package FileTransferProtocol;

import DiscordEvents.DownloadFininshedEvent;
import DiscordEvents.DownloadOrUploadFileEvent;
import DiscordEvents.FileIOEvent;
import DiscordEvents.UploadFinishedEvent;
import gui.Main;
import gui.MainForm;
import javafx.scene.shape.Arc;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.*;

public class FileManager {
    private static String token;
    public static ArrayList<Thread> FileTransferThreadPool = new ArrayList<>();

    public static DownloadOrUploadFileEvent douListener;
    private static ArrayList<FileDownloadUploadRequest> requests = new ArrayList<>();
    public static void setDouListener(DownloadOrUploadFileEvent douListener) {
        FileManager.douListener = douListener;
    }

    public static FileDownloadUploadRequest currentRequest;
    public static void downloadFile(String fileName, String destPath, FileIOEvent Listener, FileTransferProgressEvent percentListener, Arc arc, boolean exists) throws IOException {
        //requests.add(new FileDownloadUploadRequest(true,fileName,destPath,Listener,percentListener));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    downloadFiles(fileName,destPath,new Socket(Main.ip,8396),percentListener,Listener,arc,exists);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(!exists)
                    douListener.handleIt(destPath,true);
            }
        }).start();
    }
    private static void downloadFiles(String file, String dest, Socket socket, FileTransferProgressEvent percentListener, FileIOEvent event, Arc arc, boolean exists) throws IOException {
        FileTransfer fileTransfer = new FileTransfer(true , socket,event,exists);
        fileTransfer.setListener(percentListener,arc);
        fileTransfer.setFilename(file);
        fileTransfer.setDestPath(dest);
        file = new String(file.getBytes(), Charset.forName("UTF-8"));
        new PrintStream(socket.getOutputStream()).println("download "+file);
        Thread downloadThread = new Thread(fileTransfer);
        FileTransferThreadPool.add(downloadThread);
        downloadThread.start();
    }
    public static int uploadFile(String filename,FileIOEvent listener,FileTransferProgressEvent percentListener,Arc arc) throws IOException {
        int result = -1;
        if(douListener!=null)
            douListener.handleIt(filename,false);
        try {
           result = uploadFiles(filename, new Socket(Main.ip, 8396), listener, percentListener, arc, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    private static int uploadFiles(String filename,Socket socket,FileIOEvent event,FileTransferProgressEvent percentListener,Arc arc,boolean exists) throws IOException {
        FileTransfer fileTransfer = new FileTransfer(false, socket,event, exists);
        fileTransfer.setListener(percentListener,arc);
        fileTransfer.setFilename(filename);
        new PrintStream(socket.getOutputStream()).println("upload "+ MainForm.getLast(filename));
        return fileTransfer.up();

        //Thread thread = new Thread(fileTransfer);
        //FileTransferThreadPool.add(thread);
        //thread.start();
    }
    public static void uploadProfilePictur(String filename,FileIOEvent event) throws IOException {
        douListener.handleIt(filename,false);
        Socket socket = new Socket(Main.ip , 8396);
        FileTransfer fileTransfer = new FileTransfer(false, socket,event, false);
        fileTransfer.setFilename(filename);
        new PrintStream(socket.getOutputStream()).println("profilepic " + token);
        Thread thread = new Thread(fileTransfer);
        FileTransferThreadPool.add(thread);
        thread.start();
    }
    public static synchronized void goForNext(String filename,FileIOEvent event,boolean exists) throws IOException {
        if(event instanceof DownloadFininshedEvent){
            ((DownloadFininshedEvent) event).fileDownloadFinishedHandler(filename);
        }
        else if(event instanceof UploadFinishedEvent){
            ((UploadFinishedEvent) event).uploadFinishedHandle(filename);
        }
    }
    public static void setToken(String token) {
        FileManager.token = token;
    }


}
