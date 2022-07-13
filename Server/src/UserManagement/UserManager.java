package UserManagement;

import CallOB.CallRequest;
import CallOB.CallResponse;
import Caller.Call;
import Caller.CallType;
import Caller.CallerService;
import DataTransmit.DataPacket;
import DataTransmit.SocketDataTransfer;
import EncryptionModel.Asymmetric;
import EncryptionModel.Symmetric;
import HashModel.Hasher;
import MailSender.VerificationCodeMailer;
import MainThread.DBManager;
import MainThread.DataManagement;
import MainThread.Main;
import NotifyService.NotifyBase;
import UserManagement.TokenManager.Token;
import UserManagement.TokenManager.TokenObj;
import logger.LogType;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class UserManager implements Runnable{
    private Symmetric symmetric;
    Socket user;
    private String token;
    private String userName;
    private String password;
    private boolean signedIn = false;
    private String verifyCode = "";
    Asymmetric asymmetric;
    String userPublicKey;
    String userSymmetricKey;
    SocketDataTransfer dataTransfer;
    public UserManager(Socket user,String token) {
        this.user = user;
        this.token = token;
        symmetric = new Symmetric();
        asymmetric = new Asymmetric();
        symmetric.generateKey();
    }

    private void generateVerificationCode(){
        Random rn = new Random(LocalDateTime.now().getSecond());
        String res = "";
        for(int i = 0; i<5; i++){
            res+= String.valueOf((Math.abs(rn.nextInt())%10));
        }
        verifyCode = res;
    }

    private void signUp(DataPacket request){
        HashMap<String,String> data = (HashMap<String, String>) request.getData();
        String username = data.get("username");
        String passwordHash = null;
        try {
            passwordHash = Hasher.getHash(data.get("password"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String email = data.get("email");
        try {
            generateVerificationCode();
            DBManager.signUp(username,passwordHash,email,verifyCode);
            System.out.println("Verification Code : "+verifyCode);
            VerificationCodeMailer.sendCode("DatCord Client Application\nVerification code : "+verifyCode+"\n\n\nDatcord@k3rn3lanic." , email);
            dataTransfer.sendData(new ServerResponse("" , ResponseType.OK));
            this.userName = username;
            this.password = passwordHash;
        }
        catch (Exception e){
            e.printStackTrace();
            dataTransfer.sendData(new ServerResponse("" , ResponseType.ERROR));
        }
    }
    private void changeStatus(String newStatus,String userName) throws IOException, ClassNotFoundException {
       DBManager.changeProperty("users" , "status" , userName,newStatus);
    }

    private void signIn(DataPacket requst){
        HashMap<String,String> data = (HashMap<String, String>) requst.getData();
        String tokens = data.get("token");
        if(!token.equals(tokens)){
            dataTransfer.sendData(new ServerResponse("TOKEN changed" , ResponseType.TOKEN_CHANGED));
        }else{
            String userName = data.get("username");
            String password = null;
            try {
                password = Hasher.getHash(data.get("password"));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            if(!(userName.isEmpty()||password.isEmpty())){
                try {
                    ServerResponse response = new ServerResponse("login" , ResponseType.OK);
                    if(DBManager.userExists(userName , password)){
                        this.userName = userName;
                        this.password = password;
                        signedIn = true;
                        long tick = 0;
                        Date k = new Date();
                        TokenObj tokenObj = new TokenObj(token , userName , String.valueOf(new Date().getTime()),userPublicKey , userSymmetricKey);
                        Main.dataManagement.setToken(tokenObj);
                        System.out.println(tokenObj.getToken()+" is setting");
                        dataTransfer.sendData(response);
                    }
                    else{
                        response.setData("ERROR couldn't find user");
                        response.setType(ResponseType.USER_NOT_FOUND);
                        dataTransfer.sendData(response);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }
    private void changeStatus(DataPacket dataPacket) throws IOException, ClassNotFoundException {
        ServerResponse response = new ServerResponse("",null);
        String userName;
        String status;
        HashMap<String,String> data = (HashMap<String, String>) dataPacket.getData();
        userName = Token.getToken(data.get("token")).getUsername();
        status = data.get("status");
        if(signedIn) {
            changeStatus(status, userName);
            response.setType(ResponseType.OK);
            dataTransfer.sendData(response);
        }
        else{
            response.setType(ResponseType.ACCESS_DENIED);
            dataTransfer.sendData(response);
        }
    }
    private String mdi(String s){
        if(s.length()==1){
            return "0"+s;
        }
        return s;
    }
    public String getCurrentDate(){
        LocalDateTime date = LocalDateTime.now();
        return mdi(""+date.getYear())+"-"+mdi(""+date.getMonthValue())+"-"+mdi(""+date.getDayOfMonth())+" / "+mdi(""+date.getHour())+":"+mdi(""+date.getMinute())+":"+mdi(""+date.getSecond());
    }
    @Override
    public void run() {
        //Key Exchange part :
        try {
            user.getOutputStream().write(asymmetric.getPublic().getEncoded());
            byte[] publicKeyBytes = new byte[2048];
            user.getInputStream().read(publicKeyBytes);
            userPublicKey = Base64.getEncoder().encodeToString(publicKeyBytes); //don't touch this
            asymmetric.setUserKey(byteToPublicKey(publicKeyBytes));
            String secretKey = Base64.getEncoder().encodeToString(symmetric.getKey().getEncoded());
            userSymmetricKey = secretKey;
            String secretKeyLocked = asymmetric.encrypt(secretKey);
            user.getOutputStream().write(secretKeyLocked.getBytes());
            dataTransfer = new SocketDataTransfer(user,true,symmetric);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataTransfer.sendData("TOKEN="+token);
        DataPacket dataPacket = null;
        while(true){
            try {
                dataPacket = dataTransfer.readPacket();
            }
            catch (NullPointerException ex){
                System.out.println("Disconnected from "+ user.getInetAddress()+":"+user.getPort());
                Token.removeUsername(token);
                return;
            }
            if(dataPacket==null){
                System.out.println("Disconnected from "+ user.getInetAddress()+":"+user.getPort());
                Token.removeUsername(token);
                return;
            }
            String request = dataPacket.getType();
            logger.Log4me.logFrom(LogType.INFO , "Request > "+request+((dataPacket.getData() instanceof HashMap)?" || Parameters : "+((HashMap<String,String>)dataPacket.getData()).toString():""),"UserManager");
            //System.out.println("UserManager@Server:~$ Request > "+request+((dataPacket.getData() instanceof HashMap)?" || Parameters : "+((HashMap<String,String>)dataPacket.getData()).toString():""));
            if(request.equals("change_status")){
                try {
                    changeStatus(dataPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if(request.equals("MyServerList")){
                HashMap<String,String> data = (HashMap<String, String>) dataPacket.getData();
                String token = data.get("token");
                if(token.equals(this.token)){
                    try {
                        UserObject user = Main.dataManagement.getUserbyUsername(Token.getToken(token).getUsername());
                        String userID = String.valueOf(user.getId());
                        ArrayList<DiscordServer> servers = Main.dataManagement.getServersMemberOwn(userID);
                        dataTransfer.sendData(servers);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                else{
                    dataTransfer.sendData(new ServerResponse("" , ResponseType.TOKEN_INVALID));
                }
            }
            if(request.equals("ServerList")){
                HashMap<String,String> data = (HashMap<String, String>) dataPacket.getData();
                String token = data.get("token");
                if(token.equals(this.token)){
                    try {
                        UserObject user = Main.dataManagement.getUserbyUsername(Token.getToken(token).getUsername());
                        String userID = String.valueOf(user.getId());
                        ArrayList<DiscordServer> servers = Main.dataManagement.getServersMemberisIn(userID);
                        dataTransfer.sendData(servers);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                else{
                    dataTransfer.sendData(new ServerResponse("" , ResponseType.TOKEN_INVALID));
                }
            }
            if(request.equals("request_friendShip")){
                HashMap<String,String> data = (HashMap<String, String>) dataPacket.getData();
                String touserName = data.get("toUsername");
                String fromUserName = Token.getToken(data.get("fromToken")).getUsername();
                try {
                    DBManager.addFriendShipRequest(fromUserName , touserName);
                    dataTransfer.sendData(new ServerResponse("" , ResponseType.OK));
                } catch (Exception e) {
                    dataTransfer.sendData(new ServerResponse("" , ResponseType.USER_NOT_FOUND));
                }
            }
            if(request.equals("signout")){ //todo : add this request to client request file
                String token = (String) dataPacket.getData();
                if(token.equals(this.token)){
                    userName = "";
                    password = "";
                    token = "";
                    ServerResponse response = new ServerResponse("" , ResponseType.OK);
                    dataTransfer.sendData(response);
                }
                else{
                    dataPacket.setData(new ServerResponse("" , ResponseType.TOKEN_INVALID));
                }
                return;
            }
            if(request.equals("ChatsList")){
                HashMap<String , String> data = (HashMap<String, String>)dataPacket.getData();
                String token = data.get("token");
                if (token.equals(this.token)) {
                    ArrayList<DiscordChat> chats = null;
                    try {
                        chats = Main.dataManagement.getChatsOfUser(Token.getToken(token).getUsername());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    dataTransfer.sendData(chats);
                }
                else{
                    dataTransfer.sendData(new ServerResponse("" , ResponseType.TOKEN_INVALID));
                }
            }
            if(request.equals("MessageList")){
                HashMap<String , String> data = (HashMap<String, String>)dataPacket.getData();
                String token = data.get("token");
                if (token.equals(this.token)) {
                    ArrayList<Message> messages = null;
                    String chat_id = data.get("chat_id");

                    try {
                        messages = Main.dataManagement.getMessages(chat_id);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    dataTransfer.sendData(messages);
                }
                else{
                    dataTransfer.sendData(new ServerResponse("" , ResponseType.TOKEN_INVALID));
                }
            }

            if(request.equals("addMemberToServer")){
                HashMap<String , String> data = (HashMap<String, String>)dataPacket.getData();
                String serverName = data.get("servername");
                String token = data.get("token");
                String tobeadeduser = data.get("memberUsername");

                if(!token.equals(this.token)){
                    dataTransfer.sendData(new ServerResponse("",ResponseType.TOKEN_INVALID));
                }
                else {
                    try {
                        //todo : check if member already exists in the server
                        //todo : check if the server actually exists
                        if(Main.dataManagement.userExistsinServer(serverName , tobeadeduser)){
                            dataTransfer.sendData(new ServerResponse("",ResponseType.USER_ALREADY_EXIST));
                        }
                        else if(DBManager.getUserByUserName(tobeadeduser)==null){
                            logger.Log4me.log(LogType.ERROR , "The user with username of ["+tobeadeduser+"] does not exist in database");
                            dataTransfer.sendData(new ServerResponse("",ResponseType.USER_NOT_FOUND));
                            continue;
                        }
                        else {
                            Main.dataManagement.addMemberToFuckingServer(serverName, String.valueOf(DBManager.getUserByUserName(tobeadeduser).getId()), Token.getToken(token).getUsername());
                            dataTransfer.sendData(new ServerResponse("",ResponseType.OK));
                        }
                    } catch (SQLException throwables) {
                        logger.Log4me.logFrom(LogType.ERROR,throwables.getMessage(),"UserManager-addMembertoServer");
                        throwables.printStackTrace();
                    }
                }
            }
            if(request.equals("DeleteServer")){
                HashMap<String , String> data = (HashMap<String , String>)dataPacket.getData();
                if(data.get("token").equals(this.token)) {
                    String serverName = data.get("serverName");
                    try {
                        if(Main.dataManagement.deleteServer(token , serverName)){
                            dataTransfer.sendData(new ServerResponse("", ResponseType.OK));
                        }
                        else{
                            dataTransfer.sendData(new ServerResponse("" , ResponseType.SERVER_NOT_FOUND));

                        }

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        dataTransfer.sendData(new ServerResponse("" , ResponseType.SERVER_NOT_FOUND));
                    }
                }
                else{
                    dataTransfer.sendData(new ServerResponse("" , ResponseType.TOKEN_INVALID));
                }

            }
            if(request.equals("SetPin")){
                HashMap<String , String> data = (HashMap<String , String>)dataPacket.getData();
                String chatID = data.get("chat_id");
                String token = data.get("token");
                String messageId = data.get("messageId");
                String username = Token.getToken(token).getUsername();
                ChatObject chat = null;
                try {
                    chat = Main.dataManagement.getChatByID(chatID);
                    if(chat.getServerID().equals("-1")){
                        Main.dataManagement.setPin(chatID,messageId);
                        dataTransfer.sendData(new ServerResponse("" , ResponseType.OK));
                        continue;
                    }

                    ServerAccess userAccess = Main.dataManagement.getUserAccess(username,chat.getServerID());
                    if(userAccess.isAbilityToPin()){
                        Main.dataManagement.setPin(chatID,messageId);
                        dataTransfer.sendData(new ServerResponse("" , ResponseType.OK));
                    }
                    else{
                        dataTransfer.sendData(new ServerResponse("" , ResponseType.ACCESS_DENIED));
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
            if(request.equals("react")){
                HashMap<String , String> data = (HashMap<String , String>)dataPacket.getData();
                String token = data.get("token");
                String chat_id = data.get("chat_id");
                String message_id = data.get("message_id");
                String reaction = data.get("reaction");
                try {
                    ChatObject chat = Main.dataManagement.getChatByID(chat_id);
                    if(chat==null){
                        //todo : chat not found
                        dataTransfer.sendData(new ServerResponse("" , ResponseType.ERROR));
                    }
                    else {
                        String from_id = Main.dataManagement.getUserbyUsername(Token.getToken(token).getUsername()).getId()+"";
                        Main.dataManagement.react(chat_id,message_id,from_id,reaction);
                        dataTransfer.sendData(new ServerResponse("" , ResponseType.OK));
                    }
                } catch (SQLException throwables) {
                    //todo : chatnot found
                    dataTransfer.sendData(new ServerResponse("" , ResponseType.ERROR));
                    throwables.printStackTrace();
                }

            }
            if(request.equals("getReactions")){
                HashMap<String , String> data = (HashMap<String , String>)dataPacket.getData();
                String chat_id = data.get("chat_id");
                String message_id = data.get("message_id");
                try {
                    ChatObject chat = Main.dataManagement.getChatByID(chat_id);
                    if(chat==null){
                        //todo : chat not found
                        dataTransfer.sendData(new ArrayList<Reaction>());
                    }
                    else {
                        String from_id = Main.dataManagement.getUserbyUsername(Token.getToken(token).getUsername()).getId()+"";
                        dataTransfer.sendData(Main.dataManagement.getReactions(chat_id,message_id));;
                    }
                } catch (SQLException throwables) {
                    //todo : chatnot found
                    dataTransfer.sendData(new ArrayList<Reaction>());
                    throwables.printStackTrace();
                }

            }
            if(request.equals("unreact")){
                HashMap<String , String> data = (HashMap<String , String>)dataPacket.getData();
                String token = data.get("token");
                String chat_id = data.get("chat_id");
                String message_id = data.get("message_id");
                String reaction = data.get("reaction");
                try {
                    ChatObject chat = Main.dataManagement.getChatByID(chat_id);
                    if(chat==null){
                        //todo : chat not found
                        dataTransfer.sendData(new ServerResponse("" , ResponseType.ERROR));
                    }
                    else {
                        String from_id = Main.dataManagement.getUserbyUsername(Token.getToken(token).getUsername()).getId()+"";
                        Main.dataManagement.unreact(chat_id,message_id,from_id,reaction);
                        dataTransfer.sendData(new ServerResponse("" , ResponseType.OK));
                    }
                } catch (SQLException throwables) {
                    //todo : chatnot found
                    dataTransfer.sendData(new ServerResponse("" , ResponseType.ERROR));
                    throwables.printStackTrace();
                }
            }
            if(request.equals("getChatInfo")){
                HashMap<String , String> data = (HashMap<String , String>)dataPacket.getData();
                String chatID = data.get("chat_id");
                String token = data.get("token");
                try {
                    ChatObject chat = Main.dataManagement.getChatByID(chatID);
                    DiscordChat chatTMP = new DiscordChat();
                    chatTMP.setServerId(chat.getServerID());
                    chatTMP.setType(chat.getType());
                    chatTMP.setMembers(chat.getMembers());
                    chatTMP.setName(chat.getName());
                    chatTMP.setId(chat.getId());
                    chatTMP.setPinnedMessage(chat.getPinnedMessage());
                    dataTransfer.sendData(chatTMP);
                    System.out.println("sent");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
            if(request.equals("sendMessage")){
                HashMap<String , String> data = (HashMap<String , String>)dataPacket.getData();
                String chatID = data.get("chat_id");
                String type = data.get("contentType");
                String token = data.get("token");
                String replyto = data.get("replied_to_id");
                if(type.equals("text")){
                    String message = data.get("message");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Main.dataManagement.sendMessage(chatID , String.valueOf(DBManager.getUserByUserName(Token.getToken(token).getUsername()).getId()) , type , message , getCurrentDate() , replyto,"");
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
                else{
                    String fileid = data.get("fileId");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Main.dataManagement.sendMessage(chatID , String.valueOf(DBManager.getUserByUserName(Token.getToken(token).getUsername()).getId()) , type , "" , getCurrentDate() , replyto,fileid);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    //todo : handle file situation with clint here. make a request, upload the file , after that add that file to files database and then get its id and put it as fileid in table
                }
            }
            if(request.equals("getProfilePicture")){

                HashMap<String , String> data = (HashMap<String , String>)dataPacket.getData();
                String id =data.get("ID");


            }
            if(request.equals("getInformation")){
                HashMap<String , String> data = (HashMap<String , String>)dataPacket.getData();
                if(data.get("token").equals(this.token)) {
                    try {
                        UserObject object = DBManager.getUserByUserName(Token.getToken(token.trim()).getUsername());
                        dataTransfer.sendData(new DataPacket("UserObject" , object));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        System.out.println(data);
                        System.out.println(Token.getToken(token).getUsername());

                    }
                }
                else{
                    dataTransfer.sendData(new DataPacket("UserObject" , new UserObject()));
                }
            }
            if(request.equals("getSomeonesInformation")){
                HashMap<String , String> data = (HashMap<String , String>)dataPacket.getData();
                if(data.get("token").equals(this.token)) {
                    String userID = data.get("id");
                    userID.replace("," , "");
                    try {
                        UserObject object = Main.dataManagement.getUserById(userID);//DBManager.getUserByUserName(Token.getToken(token).getUsername());
                        dataTransfer.sendData(new DataPacket("UserObject" , object));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                else{
                    dataTransfer.sendData(new DataPacket("UserObject" , new UserObject()));
                }

            }
            if(request.equals("getServerChats")){
                HashMap<String , String> data = (HashMap<String , String>)dataPacket.getData();
                if(data.get("token").equals(this.token)) {
                    String ServerID = data.get("serverID");
                    ServerID.replace("," , "");
                    try {
                        ArrayList<DiscordChat> object = Main.dataManagement.getServerchats(ServerID);//DBManager.getUserByUserName(Token.getToken(token).getUsername());
                        dataTransfer.sendData(new DataPacket("ChatObjects" , object));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                else{
                    dataTransfer.sendData(new DataPacket("UserObject" , new UserObject()));
                }

            }
            if(request.equals("getSomeonesInformationU")){
                HashMap<String , String> data = (HashMap<String , String>)dataPacket.getData();
                if(data.get("token").equals(this.token)) {
                    String userID = data.get("username");
                    userID.replace("," , "");
                    try {
                        UserObject object = Main.dataManagement.getUserbyUsername(userID);//DBManager.getUserByUserName(Token.getToken(token).getUsername());
                        dataTransfer.sendData(new DataPacket("UserObject" , object));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                else{
                    dataTransfer.sendData(new DataPacket("UserObject" , new UserObject()));
                }

            }
            if(request.equals("createChat")){
                HashMap<String , String> data = (HashMap<String , String>)dataPacket.getData();
                String name = data.get("chatName");
                String type = data.get("chat_type");
                String ServerName = data.get("ServerName");
                String members = data.get("members");
                String serverID = null;
                try {
                    serverID = Main.dataManagement.getServerByname(ServerName).getId();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                try {
                    if(Main.dataManagement.getServerByID(serverID)==null){
                        logger.Log4me.log(LogType.WARNING , "Creating chat but the server does not exist Creating direct chat instead");
                        ChatObject chat = new ChatObject("" , name , type , serverID, members);
                        Main.dataManagement.createChat(chat);
                        dataTransfer.sendData(new ServerResponse("" , ResponseType.OK));
                        continue;
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                String token = data.get("token");
                if(members.equals("")){
                    logger.Log4me.log(LogType.WARNING , "members are none! we will add all the members of the server to this chat");
                    try {
                        members = Main.dataManagement.getServerByID(serverID).getMembers();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                if(!token.equals(this.token)){
                    logger.Log4me.log(LogType.ERROR , "the token of this user has been changed");
                    dataTransfer.sendData(new ServerResponse("" , ResponseType.TOKEN_CHANGED));
                    continue;
                }
                try {
                    if(Main.dataManagement.getChatByName(name)!=null){
                        logger.Log4me.log(LogType.ERROR , "the chat already exists");
                        dataTransfer.sendData(new ServerResponse("",ResponseType.CHAT_ALREADY_EXISTS));
                        continue;
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                //todo must add a functionality to add a user to a chat

                ChatObject chat = new ChatObject("" , name , type , serverID, members);
                Main.dataManagement.createChat(chat);
                dataTransfer.sendData(new ServerResponse("" , ResponseType.OK));

            }
            if(request.equals("changeAccessCode")){
                HashMap<String , String> data = (HashMap<String, String>)dataPacket.getData();
                String serverID = data.get("serverid");
                String token = data.get("token");
                String usernameTobeChanged = data.get("usertochange");
                String newAccessCode = data.get("accessCode");
                try {
                    DiscordServer server = Main.dataManagement.getServerByID(serverID);
                    if(server!=null) {
                        System.out.println(Main.dataManagement.getUserbyUsername(Token.getToken(token).getUsername()).getId());
                        System.out.println(server.getOwnerId());
                        if (server.getOwnerId().trim().equals(Main.dataManagement.getUserbyUsername(Token.getToken(token).getUsername()).getId() + "")) {
                            if (newAccessCode.length() != 8 || !newAccessCode.replace("0", "").replace("1", "").isEmpty()) {
                                dataTransfer.sendData(new ServerResponse("AccessCode invalid", ResponseType.ERROR));
                            } else {
                                Main.dataManagement.changeAccessCode(usernameTobeChanged, serverID, newAccessCode);
                                dataTransfer.sendData(new ServerResponse("", ResponseType.OK));
                            }
                        } else {
                            dataTransfer.sendData(new ServerResponse("", ResponseType.ACCESS_DENIED));
                            //todo : access denied
                        }
                    }
                    else {
                        dataTransfer.sendData(new ServerResponse("",ResponseType.SERVER_NOT_FOUND));
                    }
                } catch (SQLException throwables) {
                    dataTransfer.sendData(new ServerResponse("",ResponseType.SERVER_NOT_FOUND));
                    //todo : server does not exist ...
                    throwables.printStackTrace();
                }
            }

            if(request.equals("getUserAccessCode")){
                HashMap<String , String> data = (HashMap<String, String>)dataPacket.getData();
                String token = data.get("token");
                String serverid = data.get("serverid");
                String usertoChange = data.get("usertochange");
                try {
                    DiscordServer server = Main.dataManagement.getServerByID(serverid);
                    if(server!=null){
                        UserObject user = Main.dataManagement.getUserbyUsername(usertoChange);
                        if(user==null){
                            //todo : usernot found
                            dataTransfer.sendData(new ServerResponse("",ResponseType.USER_NOT_FOUND));
                        }
                        else{
                            dataTransfer.sendData(Main.dataManagement.getUserAccess(usertoChange,serverid));
                        }
                    }
                    else{
                        dataTransfer.sendData(new ServerResponse("",ResponseType.SERVER_NOT_FOUND));
                        //todo : servernot found
                    }
                } catch (SQLException throwables) {
                    dataTransfer.sendData(new ServerResponse("",ResponseType.SERVER_NOT_FOUND));
                    //todo : servernot found
                    throwables.printStackTrace();
                }


            }

            if(request.equals("createServer")){
                HashMap<String , String> data = (HashMap<String, String>)dataPacket.getData();
                String serverName = data.get("name");
                String token = data.get("token");
                String members = data.get("members");
                if(members==null){
                    members = "-1";
                }
                if(!token.equals(this.token)){
                    dataTransfer.sendData(new ServerResponse("",ResponseType.TOKEN_INVALID));
                }
                else {
                    try {
                        if(Main.dataManagement.getServerByname(serverName)!=null){
                            logger.Log4me.log(LogType.ERROR , "Server "+serverName+" already exists");
                            dataTransfer.sendData(new ServerResponse("" , ResponseType.SERVER_ALREADY_EXISTS));
                        }
                        else{
                            try {
                                Main.dataManagement.createNewServer(serverName,String.valueOf(DBManager.getUserByUserName((Token.getToken(token).getUsername())).getId()),members);
                                if(members.equals("-1"))
                                    Main.dataManagement.addMemberToFuckingServer(serverName,String.valueOf(DBManager.getUserByUserName((Token.getToken(token).getUsername())).getId()),"Nobody");
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            dataTransfer.sendData(new ServerResponse("",ResponseType.OK));
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }



            if(request.equals("verifyRequest")) {
                HashMap<String,String> data = (HashMap<String, String>)dataPacket.getData();
                String code = data.get("code");
                if(code.equals(DBManager.getVerifyCode(userName))){
                    try {
                        DBManager.verifyUser(userName);
                        dataTransfer.sendData(new ServerResponse("" , ResponseType.OK));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        dataTransfer.sendData(new ServerResponse("" , ResponseType.ERROR));
                    }
                }
                else{
                    dataTransfer.sendData(new ServerResponse("" , ResponseType.INVALID_VERIFICATION));
                }
            }
            if(request.equals("CheckStatus")){ //todo : DONE.
                HashMap<String,String> data = (HashMap)dataPacket.getData();
                String checkUsername = data.get("username");
                //todo:check status from database and report
                try {
                    UserStatus status = Main.dataManagement.getStatus(checkUsername);
                    dataTransfer.sendData(new ServerResponse(status.toString() , ResponseType.OK));
                } catch (SQLException throwables) {
                    dataTransfer.sendData(new ServerResponse("ERROR" , ResponseType.ERROR));
                }

            }
            if(request.equals("removeStatus")){
                HashMap<String,String> parameters = new HashMap<>();
                String token = parameters.get("token");
                if(token.equals(this.token)&&signedIn) {
                    Main.dataManagement.removeStatus(userName);
                    dataTransfer.sendData(new ServerResponse("", ResponseType.OK));
                }
            }
            if(request.equals("CheckOnline")){ //todo : important : check for user existance too
                HashMap<String , String> data = (HashMap)dataPacket.getData();
                if(data.get("token").equals(this.token)) {
                    String checkUsername = data.get("username");
                    if (Token.getTokenByUsername(checkUsername) == null) {
                        dataTransfer.sendData("Offline");
                    } else {
                        dataTransfer.sendData("Online");
                    }
                }
                else {
                    dataTransfer.sendData("Invalid Token");
                }
            }//todo : not done
            if(request.equals("CheckOnlineS")){ //TODO : important : check for users existance too
                ArrayList<String> usernames = (ArrayList<String>)dataPacket.getData();
                ArrayList<String> results = new ArrayList<>();
                for(String username : usernames){
                    if(Token.getTokenByUsername(username)!=null){
                        results.add("Online");
                    }
                    else{
                        results.add("Offline");
                    }
                }
                dataTransfer.sendData(results);
            }//todo : not done
            if(request.equals("getFriendShipRequests")){
                HashMap<String , String> data = new HashMap<>();
                if(signedIn){
                    String usernam = Token.getToken(token).getUsername();
                    ArrayList<String> friendShipRequests = new ArrayList<>();
                    try {
                        friendShipRequests = Main.dataManagement.getFriendshipRequests(usernam);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    dataTransfer.sendData(friendShipRequests);
                }
                else{
                    System.out.println("lol");
                    dataTransfer.sendData("ERROR");
                }
            }
            if(request.equals("friendshipReq")){
                HashMap<String , String> data = (HashMap<String,  String>)dataPacket.getData();
                String approveUsername = data.get("username");
                String accept = data.get("acceptance");
                String fromToken = data.get("token");
                try {
                    dealWithFriendRequest(approveUsername , accept,fromToken);
                    dataTransfer.sendData(new ServerResponse("OK" , ResponseType.OK));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    dataTransfer.sendData(new ServerResponse("ERROR" , ResponseType.SQLERROR));
                }
            }//todo : use this section for accept or reject friend requests

            //todo : remained requests : getInfo(with username), login by session(Token)
            //todo : requests that don't require to be signed in
            if(!signedIn) {
                if (request.equals("sign_up")) {
                    signUp(dataPacket);
                }
                else if (request.equals("SIGN-IN")) {
                    signIn(dataPacket);
                }
            }
        }
    }

    private void dealWithFriendRequest(String approveUsername, String accept,String fromToken) throws SQLException {
        String fromUsername = Token.getToken(fromToken).getUsername();
        String toUsername = approveUsername;
        Main.dataManagement.dealFriendShipRequest(fromUsername, toUsername,accept);
    }

    PublicKey byteToPublicKey(byte[] data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(data);
        PublicKey userKey;
        userKey = keyFactory.generatePublic(publicKeySpec);
        return userKey;
    }
}
