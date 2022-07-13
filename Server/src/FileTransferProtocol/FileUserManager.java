package FileTransferProtocol;

import MainThread.Main;
import UserManagement.TokenManager.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUserManager implements Runnable{
    Socket user;
    InputStreamReader in;
    BufferedReader bf;
    PrintWriter pr;

    public FileUserManager(Socket user) {
        this.user = user;
    }

    private String readData() {
        try {
            return bf.readLine();
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }
    private void sendData(String data){
        pr.println(data);
        pr.flush();
    }
    @Override
    public void run() {
        try {
            in = new InputStreamReader(user.getInputStream());
            bf = new BufferedReader(in);
            pr = new PrintWriter(user.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String request = readData();
        if(request==null){
            System.out.println("FileManager@Server:~$ FILE CONNECTION DISCONNECTED!!");
            return;
        }
        System.out.println("FileManager@Server:~$ FileRequest : "+request);
        if(request.startsWith("upload")){
            String fileName = request.substring(7);
            FileManager.downloadFile("downloads\\"+fileName,user, false);
        }
        else if(request.startsWith("download ")){
            String fileName = request.replace("download ","");
            if(Files.exists(Paths.get(fileName))) {
                System.out.println("uploading "+fileName);
                FileManager.uploadFile(fileName, user, false);
            }
            else{
                try {
                    Files.copy(Paths.get("ProfilePics\\default.jpg" ), Paths.get(fileName));
                    FileManager.uploadFile(fileName,user,false);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        else if(request.startsWith("profilepic ")){
            String token = request.split(" ")[1];
            String username = Main.dataManagement.getToken(token).getUsername();
            String des = "ProfilePics\\"+username+".jpg"; //todo : change it to store based on id
            FileManager.downloadFile(des,user,true);
        }

    }
}
