package FileTransferProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerListener implements Runnable{
    public static ServerSocket socket;
    ArrayList<Thread> threadPool;
    public static boolean isRunning = true;
    public ServerListener(ServerSocket socket){
        ServerListener.socket = socket;
        threadPool = new ArrayList<>();
    }
    @Override
    public void run() {
        while(isRunning){
            try {
                Socket s = socket.accept();
                System.out.println("\t\tNew File connection arrived : "+s.getLocalAddress()+":"+s.getPort());
                FileUserManager userManager = new FileUserManager(s);
                Thread thread = new Thread(userManager);
                threadPool.add(thread);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
