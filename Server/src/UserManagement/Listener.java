package UserManagement;

import UserManagement.TokenManager.Token;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Listener implements Runnable{
    HashSet<String> tokens = new HashSet<>();
    public static ServerSocket socket;
    public static boolean isRunning = true;
    ArrayList<Thread> threadPool;
    Listener(ServerSocket socket){
        this.socket = socket;
        threadPool = new ArrayList<>();
    }
    @Override
    public void run() {
        System.out.println("Listenning  ...");

        while(Listener.isRunning){
            try {
                Socket s = socket.accept();

                String token = Token.generateToken();
                System.out.println("\t\tNew connection arrived : "+s.getLocalAddress()+":"+s.getPort()+" | TOKEN : "+token);
                UserManager userManager = new UserManager(s,token);
                Thread thread = new Thread(userManager);
                threadPool.add(thread);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Stopped Listening");
    }

}
