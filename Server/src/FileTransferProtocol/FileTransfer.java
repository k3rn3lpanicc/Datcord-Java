package FileTransferProtocol;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class FileTransfer implements Runnable{
    boolean download = false;
    private String filename;
    private Socket socket;
    private String destPath;
    private int id;
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }
    boolean profileItis;
    public FileTransfer(boolean download, Socket socket, boolean profileItis) {
        this.download = download;
        this.socket = socket;
        this.profileItis = profileItis;
    }
    public void downloadFile(String destPath , Socket socket) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        new DataOutputStream(socket.getOutputStream()).writeInt(id);

        int fileNamelength = dataInputStream.readInt();
        if(fileNamelength>0){
            byte[] fileNameBytes = new byte[fileNamelength];
            dataInputStream.readFully(fileNameBytes,0,fileNameBytes.length);
            String filename = new String(fileNameBytes);
            long filecontentLength = dataInputStream.readLong();
            System.out.println("Downloading File : "+filename+"("+filecontentLength+" bytes)");
            if(filecontentLength>0) {
                FileOutputStream fileOutputStream = new FileOutputStream(destPath);
                long nread = 0L;
                byte[] buf = new byte[8192];
                int n;
                while((n=dataInputStream.read(buf))>0){
                    fileOutputStream.write(buf,0,n);
                    nread+=n;
                }
                fileOutputStream.close();
                dataInputStream.close();
                fileOutputStream.flush();
                socket.close();
            }
        }
    }
    public void uploadFile(String filename ,Socket socket) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filename);
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        filename = new String(filename.getBytes(), Charset.forName("UTF-8"));
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
        }
        fileInputStream.close();
        dataOutputStream.close();
        socket.close();
    }

    private String getfullPath(String filename) { //todo : this method should return the full path of that file, from the database record
        return filename;
    }

    @Override
    public void run() {
        if(download){
            try {
                downloadFile(destPath,socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                uploadFile(filename , socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}