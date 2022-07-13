import LocalData.SaveAndLoad;
import UserManagement.*;
import gui.MainForm;
import gui.runableApp;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ConsoleApp implements runableApp {
    public String[] args;
    static String mainMenuText = "┌───────────────────────────────────────────────────────────────┐\n" +
            "│MAIN MENU  /           DISCORD  ->   DATCORD                   │\n" +
            "├───────────────────────────────────────────────────────────────┤\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "│                                                               │\n" +
            "└───────────────────────────────────────────────────────────────┘";
    public static void setMenuItem(int index , String item){
        String toWrite = "["+(index+1)+"] "+yellow(item);
        char[][] arr = new char[mainMenuText.split("\n").length][65];
        for(int i = 0; i<mainMenuText.split("\n").length;i++){
            String mmm = mainMenuText.split("\n")[i];
            if(i>=3 && i < arr.length-1){
                arr[i] = mmm.substring(0,mmm.length()-1).toCharArray();
            }
            else{
                arr[i] = mmm.toCharArray();
            }

        }
        for(int i = 0 ; i<toWrite.length();i++) {
            arr[index + 3][10+i] = toWrite.charAt(i);
        }
        mainMenuText = "";
        for(int i = 0; i<arr.length;i++){
            mainMenuText+= String.valueOf(arr[i])+kk((i==index+3)?9:0)+(i>=3 && i< arr.length-1?"│":"")+"\n";
        }
    }
    static String kk(int i){
        String res = "";
        for (int k = 0 ;k<i;k++)
            res+=" ";
        return res;
    }
    public static String red(String s){
        return "\u001B[31m"+s+"\u001B[0m";
    }
    public static String green(String s){
        return "\u001B[32m" +s+"\u001B[0m";
    }
    public String cyan(String s){
        return "\u001B[36m"+s+"\u001B[0m";
    }
    public static String yellow(String s){
        return "\u001B[33m"+s+"\u001B[0m";
    }
    String token;
    Socket socket1;
    Socket socket2;
    Socket socket3;
    Socket socket4;
    Request request;
    public ConsoleApp(Socket socket1, Socket socket2, Socket socket3, Request request, String token, Socket callService) {
        this.socket1 = socket1;
        this.socket2 = socket2;
        this.socket3 = socket3;
        this.request = request;
        this.socket4 = callService;
    }
    UserObject me;
    @Override
    public void start(String[] args) {
        try {
            firstMenu();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void firstMenu() throws IOException, ClassNotFoundException {
        System.out.println("[1] Login\n" +
                "[2] Sign Up\n" +
                "[3] Exit");
        String chosen = Main.sc.nextLine();
        switch (chosen){
            case "1":
                System.out.print("root@Client:~$ >"+green("Enter Username : "));
                String username = Main.sc.nextLine();
                System.out.print("root@Client:~$ >"+ yellow("Enter Password : "));
                String password = Main.sc.nextLine();
                if(request.signIn(username , password).getType()== ResponseType.OK){
                    UserObject info = request.getMyInformation();
                    me = info;
                    System.out.print(info.getUsername()+"@Client:~$ "+green("Logged inside!")+"\n"+green("―――――――USER―――――――")+"\n\t\tUsername : "+green(info.getUsername())+"\n\t\tEmail : "+green(info.getEmail())+"\n\t\tPhone number : "+green(info.getPhoneNumber())+"\n"+green("――――――――――――――――")+"\n");
                    setMenuItem(0," │ Join A Chat");
                    setMenuItem(1," │ Create Server");
                    setMenuItem(2," │ Delete Server");
                    setMenuItem(3," │ Add Member To Server");
                    setMenuItem(4," │ Remove Member From Server");
                    setMenuItem(5," │ Call");
                    setMenuItem(6," │ Create Text Channel");
                    setMenuItem(7," │ Create Voice Channel");
                    setMenuItem(8," │ Set Status");
                    setMenuItem(9,"│ Upload Profile Picture");
                    setMenuItem(10,"│ see List of FriendShip Requests");
                    setMenuItem(11,"│ Request a Friend");
                    setMenuItem(12,"│ Servers list");
                    setMenuItem(13,"│ Accept or reject a friendShip request");
                    setMenuItem(14,"│ My Information");
                    setMenuItem(15,"│ LogOut");
                    MainMenu();
                }
                else{
                    System.out.println("root@Client:~$ "+red("Couldn't sign in"));
                }
                break;
            case "2":
                break;
            case "3":
                System.out.println(green("Farewell"));
                SaveAndLoad.setIsRunning(false);
                socket1.close();
                socket2.close();
                socket3.close();
                return;
        }
        start(args);
    }
    private ArrayList<DiscordChat> directChats(ArrayList<DiscordChat> chats){
        ArrayList<DiscordChat> result = new ArrayList<>();
        for(DiscordChat chat : chats){
            if(chat.getType().equals("direct")){
                result.add(chat);
            }
        }
        return result;
    }
    private void MainMenu() throws IOException, ClassNotFoundException {


        System.out.println(mainMenuText);
        System.out.print(cyan("Enter your selection : > "));
        String chosen = Main.sc.nextLine();
        switch (chosen){
            case "help":
                MainMenu();
                break;
            case "1":
                System.out.println("[1] Direct Chat\n[2] Server chat\n[3] return");
                ArrayList<DiscordChat> chats = request.getListOfDirectChats();
                String s = Main.sc.nextLine();
                ArrayList<DiscordChat> directChats = new ArrayList<>();

                switch (s){

                    case "1":
                        directChats = directChats(chats);
                        showChats(directChats);
                        System.out.println("Choose : ");
                        String chh = Main.sc.nextLine();
                        int chosindex = Integer.parseInt(chh);
                        DiscordChat chatToMove = directChats.get(chosindex-1);
                        System.out.println(green("Selected chat : ")+yellow(chatToMove.toString()));
                        System.out.println(red("\t\tLoading chat history ..."));
                        ArrayList<Message> chatMessaged = request.getMessages(chatToMove);
                        printMessages(chatMessaged);
                        System.out.println(yellow("Type your messages below (enter -1 to exit from chat)"));
                        System.out.println(">");
                        while(true){
                            String myMessage = Main.sc.nextLine();
                            if(myMessage.equals("-1")){
                                break;
                            }
                            else{
                                request.sendMessage(chatToMove.getId() , "" , myMessage);
                            }
                        }
                    break;
                    case "2":

                        ArrayList<DiscordChat> serverChats = serverChats(chats);
                        showChats(serverChats);
                        System.out.println("Choose : ");
                        chh = Main.sc.nextLine();
                        chosindex = Integer.parseInt(chh);
                        chatToMove = serverChats.get(chosindex-1);
                        System.out.println(green("Selected chat : ")+yellow(chatToMove.toString()));
                        System.out.println(red("\t\tLoading chat history ..."));
                        chatMessaged = request.getMessages(chatToMove);
                        printMessages(chatMessaged);
                        System.out.println(yellow("Type your messages below (enter -1 to exit from chat)"));
                        System.out.println(">");
                        while(true){
                            String myMessage = Main.sc.nextLine();
                            if(myMessage.equals("-1")){
                                break;
                            }
                            else{
                                request.sendMessage(chatToMove.getId() , "" , myMessage);
                            }
                        }
                        break;
                }
                break;
            case "13":
                ArrayList<DiscordServer> servers = request.getServers();
                int cnt = 1;
                for(DiscordServer server : servers){ //todo : replace ownerID with owner username here
                    String t = yellow("["+cnt+"]")+"\t"+green("Name : ")+server.getName()+"\t\t\t"+green("Owner : ")+server.getOwnerId()+"\t\t\t"+green("Members : ")+server.getMembers();
                    System.out.println(t);
                    cnt++;
                }
                break;
            case "15":
                System.out.print("\n"+green("―――――――USER―――――――")+"\n\t\tUsername : "+green(me.getUsername())+"\n\t\tEmail : "+green(me.getEmail())+"\n\t\tPhone number : "+green(me.getPhoneNumber())+"\n"+green(me.getStatus().toString())+"\n"+green("――――――――――――――――")+"\n");
                break;
            case "12":
                System.out.print(me.getUsername()+"@Client:~$ "+green("Enter the username to send fr request : "));
                String usernameToReq = Main.sc.nextLine();
                ResponseType tt = request.friendshipRequest(usernameToReq).getType();
                if(tt==ResponseType.OK)
                    System.out.println(me.getUsername()+"@Client:~$ "+green("FriendShip request sent!"));
                else
                    System.out.println(red(tt.toString()));
                break;
            case "11":
                System.out.println(yellow(request.getFriendshipRequests().toString()));
                break;
            case "6":
                System.out.print(me.getUsername()+"@Client:~$ "+green("Enter the username to call to : "));
                String username = Main.sc.nextLine();
                if (request.callUser(token)) {
                    System.out.println("Call started");
                } else {
                    System.out.println("Rejected");
                }
                break;
            case "2":
                System.out.print(me.getUsername()+"@Client:~$ "+green("Enter the ServerName : "));
                String serverName = Main.sc.nextLine();
                ResponseType type = request.createServer(serverName).getType();
                if(type == ResponseType.OK){
                    System.out.print(me.getUsername()+"@Client:~$ "+green("Server Created!")+"\n");
                }
                else {
                    System.out.println(red(type.toString()));
                }
                break;
            case "4":
                System.out.print(me.getUsername()+"@Client:~$ "+green("Enter the ServerName : "));
                String server = Main.sc.nextLine();
                System.out.print(me.getUsername()+"@Client:~$ "+green("Enter the Member's Username to add : "));
                String memberUsername = Main.sc.nextLine();
                if(request.addMemberToServer(server , memberUsername).getType() == ResponseType.OK){
                    System.out.print(me.getUsername()+"@Client:~$ "+green("Member Added!")+"\n");
                }
                else{

                }
                break;
            case "3":
                System.out.print(yellow(me.getUsername()+"@Client:~$ ")+green("Enter the serverName you want to delete : "));
                String getServerName = Main.sc.nextLine();
                ResponseType responseType = request.deleteServer(getServerName);
                if(responseType==ResponseType.OK){
                    System.out.println(green("Server Deleted Successfully!"));
                }
                else{
                    System.out.println(red(responseType.toString()));
                    System.out.println(red("        List of Servers you own : "));
                    ArrayList<DiscordServer> myServers = request.getMyServers();
                    int cntt = 1;
                    for(DiscordServer server1 : myServers){
                        System.out.println("            ["+(cntt++)+"] "+cyan(server1.getName()));
                    }
                }
                break;
            case "7":
                System.out.print(me.getUsername()+"@Client:~$ "+green("Enter the ServerName : "));
                String serverName2 = Main.sc.nextLine();
                System.out.print(me.getUsername()+"@Client:~$ "+green("Enter Channel name you want to create : "));
                String channelName = Main.sc.nextLine();
                ResponseType ty = request.createChat(channelName,"text" ,  serverName2 , "").getType();
                if(ty==ResponseType.OK){
                    System.out.println(me.getUsername()+"@Client:~$ "+green("TextChannel created"));
                }
                else{
                    System.out.println(red(ty.toString()));
                }
                break;
            case "8":
                System.out.print(me.getUsername()+"@Client:~$ "+green("Enter the ServerName : "));
                String serverName3 = Main.sc.nextLine();
                System.out.print(me.getUsername()+"@Client:~$ "+green("Enter Voice Channel name you want to create : "));
                String channelName2 = Main.sc.nextLine();
                ResponseType ty2 = request.createChat(channelName2,"voice" ,  serverName3 , "").getType();
                if(ty2==ResponseType.OK){
                    System.out.println(me.getUsername()+"@Client:~$ "+green("VoiceChannel created"));
                }
                else{
                    System.out.println(red(ty2.toString()));
                }

                break;
            case "9":
                System.out.print(me.getUsername()+"@Client:~$ "+green("Select your status [1 for Idle , 2 for Do_not_Disturb and 3 for Invisible : "));
                String ind = Main.sc.nextLine();
                String chosen2 = UserStatus.values()[Integer.parseInt(ind)].toString();
                ResponseType t = request.changeStatus(chosen2).getType();
                if(t == ResponseType.OK){
                    System.out.println(me.getUsername()+"@Client:~$ "+green("Status Changed!"));
                    me.setStatus(UserStatus.valueOf(chosen2));
                }
                else{
                    System.out.println(red(t.toString()));
                }
                break;
            case "10":
                System.out.print(me.getUsername()+"@Client:~$ "+green("Enter your filename : "));
                String fileName = Main.sc.nextLine();
                if(request.setPicture(fileName, new MainForm())){
                    System.out.println(me.getUsername()+"@Client:~$ "+green("Profile Picture Updated"));
                }
                else{
                    System.out.println(red("Couldn't change the profile picture / maybe the filename is incorrect"));
                }
                break;

            case "0":
                me = null;
                return;
        }
        MainMenu();
    }

    private void printMessages(ArrayList<Message> chatMessaged) {
        System.out.println("-------------------------------------------");
        for(Message message : chatMessaged){
            if(message instanceof TextMessage){
                TextMessage tt = (TextMessage)message;
                System.out.println(green(tt.getFromUsername())+ " : "+yellow(tt.getContent())+"                       "+cyan(tt.getDate()));
            }
        }
        System.out.println("-------------------------------------------");
    }

    private void showChats(ArrayList<DiscordChat> chats) {
        int cnt = 1;
        for(DiscordChat chat : chats) {
            if(chat.getType().equals("direct")){
                String otherGuy = chat.getMembers().replace(me.getId()+",", "").replace(me.getId()+"" , "").replace("," , "");
                UserObject otherOne = request.getInformationByID(otherGuy);
                System.out.println("[" + (cnt++) + "] " + otherOne.getUsername());
            }
            else{
                System.out.println("[" + (cnt++) + "] " + chat.getName());
            }

        }
    }

    private ArrayList<DiscordChat> serverChats(ArrayList<DiscordChat> chats) {
        ArrayList<DiscordChat> result = new ArrayList<>();
        for(DiscordChat chat : chats)
            if(!chat.getType().equals("direct"))
                result.add(chat);
        return result;
    }
}
