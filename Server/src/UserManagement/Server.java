package UserManagement;

import Caller.CallType;
import Caller.CallerService;
import FileTransferProtocol.ServerListener;
import MainThread.Main;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;

public class Server implements Runnable{
    Thread ListenerThread = new Thread();
    Thread fileServerListenerThread = new Thread();
    int portNumber = 8395;
    int fileServicePortNumber = 8396;
    int notifyServicePortNumber = 8397;
    int callServerSocketPortNumber = 8399;
    ServerSocket socket = new ServerSocket(portNumber);
    ServerSocket fileServiceSocket = new ServerSocket(fileServicePortNumber);
    ServerSocket notifyServer = new ServerSocket(notifyServicePortNumber);
    Thread notifyListenerThread = new Thread();
    Thread callServiceThread = new Thread();
    ServerSocket callListenerSocket = new ServerSocket(callServerSocketPortNumber);



    public Server() throws IOException {

    }

    @Override
    public void run(){
        Listener listener = new Listener(socket);
        ServerListener listener2 = new ServerListener(fileServiceSocket);
        NotifyService.Listener listener3 = new NotifyService.Listener(notifyServer);
        Caller.CallServer listener4= new Caller.CallServer(callListenerSocket);


        System.out.println("commands : \n" +
                "1)Run\n" +
                "2)Stop\n" +
                "3)Exit");
        while(true) {
            String chosen = Main.sc.nextLine();
//            if (chosen.startsWith("send")) {
//                try {
//                    String username = chosen.split(" ")[1];
//                    String data = chosen.split(" ")[2];
//                    String result = (String)NotifyBase.notifyUserByUsername(username, data);
//                } catch (Exception ex){
//                    ex.printStackTrace();
//                }
//            }

            switch (chosen.toLowerCase()) {
                case "run":
                    if (!ListenerThread.isAlive()) {
                        ListenerThread = new Thread(listener);
                        Listener.isRunning = true;
                        ListenerThread.start();
                        fileServerListenerThread = new Thread(listener2);
                        fileServerListenerThread.start();
                        notifyListenerThread = new Thread(listener3);
                        notifyListenerThread.start();
                        callServiceThread = new Thread(listener4);
                        callServiceThread.start();
                        runCalls();

                    } else {
                        System.out.println("UserManagement.Server is running already");
                    }
                    break;
                case "stop":
                    if (ListenerThread.isAlive()) {
                        ListenerThread = new Thread(listener);
                        Listener.isRunning = false;
                        fileServerListenerThread = new Thread(listener2);
                        notifyListenerThread = new Thread(listener3);
                        callServiceThread = new Thread(listener4);
                    }
                    break;
                case "exit":
                    endProgram();
                    break;
            }

        }
    }

    private void runCalls() {
        try {
            ArrayList<ChatObject> chatObjects = Main.dataManagement.getAllChats();
            for(ChatObject chat : chatObjects){
                if(chat.getType().equals("voice")||chat.getType().equals("screen")){
                    CallerService.addCall(CallType.groupCall,chat.getId());
                    System.out.println("Started call for chat "+chat.getId());
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void endProgram(){
        Main.endProgram();
    }
}
