import UserManagement.Request;
import gui.runableApp;

import java.net.Socket;

public class DiscordApp {
    public static String[] args;
    public static void run(Socket socket1, Socket socket2, Socket socket3, Request request, String token, Socket callService){
        runableApp app = new ConsoleApp(socket1,socket2,socket3,request,token,callService);
        app.start(args);
    }
}
