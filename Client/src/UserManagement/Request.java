package UserManagement;

import CallOB.CallObject;
import CallService.CallListener;
import DataTransmit.DataPacket;
import DataTransmit.SocketDataTransfer;
import DiscordEvents.FileIOEvent;
import EncryptionModel.Asymmetric;
import EncryptionModel.Symmetric;
import FileTransferProtocol.FileManager;
import FileTransferProtocol.FileTransferProgressEvent;
import gui.Main;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class Request  {
    private Socket socket;
    private SocketDataTransfer mainIO;
    private SocketDataTransfer callIO;
    private String token;
    public static Asymmetric asymmetric;
    public static Symmetric symmetric;
    public static ArrayList<CallObject> outgoingCalls = new ArrayList<>();
    public static Thread callThread = new Thread();
    public void setMainIO(SocketDataTransfer mainIO) {
        this.mainIO = mainIO;
    }
    public Request(Socket socket, String token) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
        this.socket = socket;
        this.token = token;
        symmetric = new Symmetric();
        asymmetric = new Asymmetric();
        //read public key from server and set it
        byte[] publicKeyBytes = new byte[2048];
        socket.getInputStream().read(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        asymmetric.setUserKey(keyFactory.generatePublic(publicKeySpec));
        //send my public key to server
        socket.getOutputStream().write(asymmetric.getPublic().getEncoded());
        //System.out.println("My public key sent : "+ Base64.getEncoder().encodeToString(asymmetric.getPublic().getEncoded()));
        //System.out.println("server public key : "+Base64.getEncoder().encodeToString(asymmetric.getUserKey().getEncoded()));
        //get the symmetric key from server and decrypt it with my own privateKey
        int len = 344;
        byte[] secretKeyBytes = new byte[len];
        socket.getInputStream().read(secretKeyBytes);
        String secretKeyLocked = new String(secretKeyBytes);
        //System.out.println("SecretKey locked : "+secretKeyLocked);
        String secretKey = asymmetric.decrypt(secretKeyLocked);
        //System.out.println("SecretKey : "+secretKey);
        byte[] encodedKey = Base64.getDecoder().decode(secretKey);
        symmetric.setKey(new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES"));

    }
    public ServerResponse signIn(String username , String password){
        HashMap<String,String> data = new HashMap<>();
        data.put("username" ,username);
        data.put("password" ,password);
        data.put("token" , token);
        mainIO.sendData(new DataPacket("SIGN-IN" , data));
        ServerResponse response = mainIO.getResponse();
        return response;
    }
    public ServerResponse signOut(){
        mainIO.sendData(new DataPacket("signout" , token));
        ServerResponse response = mainIO.getResponse();
        return response;
    }
    public DiscordChat getChatInformation(String chatID){
        HashMap<String,String> data = new HashMap<>();
        data.put("token" , token);
        data.put("chat_id" , chatID);
        mainIO.sendData(new DataPacket("getChatInfo" , data));
        DiscordChat result = (DiscordChat) mainIO.readData();
        return result;
    }
    public ResponseType signUp(String username, String passwordHash, String email) throws IOException, ClassNotFoundException {
        HashMap<String,String> data = new HashMap<>();
        data.put("username" ,username);
        data.put("password" , passwordHash);
        data.put("email" , email);
        mainIO.sendData(new DataPacket("sign_up" , data));
        ServerResponse result = mainIO.getResponse();
        return result.getType();
    }
    public void setToken(String token) {
        this.token = token;
    }
    public ServerResponse verifyCode(String code){
        HashMap<String,String> data = new HashMap<>();
        data.put("code" , code);
        data.put("token" , token);
        mainIO.sendData(new DataPacket("verifyRequest", data));
        ServerResponse response = mainIO.getResponse();
        return response;
    }
    public ServerResponse friendshipRequest(String username){
        HashMap<String , String> data = new HashMap<>();
        data.put("fromToken" , token);
        data.put("toUsername" , username);
        mainIO.sendData(new DataPacket("request_friendShip" , data));
        ServerResponse response = mainIO.getResponse();
        return response;
    }
    public ServerResponse changeStatus(String newStatus){ //TODO : DONE
        HashMap<String,String> data = new HashMap<>();
        data.put("token" , token);
        data.put("status" , newStatus);
        mainIO.sendData(new DataPacket("change_status" , data));
        ServerResponse response = mainIO.getResponse();
        return response;
    }
    public boolean setPicture(String picture,FileIOEvent event){
        //todo : check for image file to see if it actually is an image
        //todo : use token and send a message to server to handle the image
        try {
            FileManager.uploadProfilePictur(picture,event);
            //todo : get server response
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    public Socket callService;

    public void setCallService(Socket callService) {
        this.callService = callService;
    }

    public boolean callUser(String username) throws IOException, ClassNotFoundException {
        System.out.println("Calling ...");
        HashMap<String,String> data = new HashMap<>();
        data.put("token" , token);
        data.put("username" , username);
        data.put("call" , "create");
        callIO.sendData(new DataPacket("call" , data));
        String callID = callIO.readString();
        callThread = new Thread(new CallListener(callService,token,callIO,callID, false));
        outgoingCalls.add(new CallObject(callID , new Date().getTime()));
        callThread.start();
        return true;
    }//todo : add a method for cancelling a call

    private CallObject getCallByID(String callID){
        for(CallObject object : outgoingCalls)
            if(object.getCallID().equals(callID))
                return object;
            return null;
    }

    public boolean disconnectFromCall(String call_ID){

        HashMap<String , String> data = new HashMap<>();
        data.put("callID" , call_ID);
        System.out.println("callID : "+call_ID);
        callIO.sendData(new DataPacket("call_disconnect" , data));
        System.out.print("Call ended. time taken : ");
        CallObject call = getCallByID(call_ID);
        if(call!=null){
            System.out.println(new Date().getTime()-call.getTimeStarted());
        }
        outgoingCalls.removeIf(e->e.getCallID().equals(call_ID));

        callThread = new Thread();
        try {
            Socket calll = new Socket(Main.ip,8399);
            callIO = new SocketDataTransfer(calll,false,null);
            callIO.sendData(token);
            setCallService(calll);
            setCallIO(callIO);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void setCallIO(SocketDataTransfer callIO) {
        this.callIO = callIO;
    }

    public SocketDataTransfer getCallIO() {
        return callIO;
    }

    public Socket getCallService() {
        return callService;
    }

    public UserStatus getStatus(String username){
        HashMap<String , String> data = new HashMap<String,String>();
        data.put("token" , token);
        data.put("username" , username);
        mainIO.sendData(new DataPacket("CheckStatus" , data));
        ServerResponse response = mainIO.getResponse();
        if(response.getType()==ResponseType.OK){
            return UserStatus.valueOf(response.getData());
        }
        else{
            System.out.println("Error");
            return null;
        }
    }
    public ServerResponse removeStatus(){
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        mainIO.sendData(new DataPacket("removeStatus" , data));
        ServerResponse response = mainIO.getResponse();
        return response;
    }
    public boolean checkOnline(String username){
        HashMap<String , String> data = new HashMap<>();
        data.put("username" , username);
        data.put("token" , token);
        DataPacket dataPacket = new DataPacket("CheckOnline" , data);
        mainIO.sendData(dataPacket);
        String checked = mainIO.readString();
        if(!checked.equals("Invalid Token"))
            return checked.equals("Online");
        else{
            System.out.println("Invalid Token/Access Denied");
            return false;
        }
    }
    public ArrayList<String> checkOnlineS(ArrayList<String> usernames){
        DataPacket packet = new DataPacket("CheckOnlineS" , usernames);
        mainIO.sendData(packet);
        ArrayList<String> result = (ArrayList<String>)mainIO.readData();
        return result;
    }
    public ArrayList<String> getFriendshipRequests(){
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        mainIO.sendData(new DataPacket("getFriendShipRequests" , data));
        ArrayList<String> result = (ArrayList<String>)mainIO.readData();
        return result;
    }
    public ServerResponse approveFriendShipRequest(String username,boolean accept){
        HashMap<String, String> data = new HashMap<>();
        data.put("token" , token);
        data.put("username" , username);
        data.put("acceptance" , accept?"accept":"reject");
        mainIO.sendData(new DataPacket("friendshipReq" ,data));
        ServerResponse response = mainIO.getResponse();
        return response;
    }

    public ServerResponse createServer(String name){
        HashMap<String , String> data = new HashMap<>();
        data.put("name" , name);
        data.put("token" , token);
        mainIO.sendData(new DataPacket("createServer" , data));
        ServerResponse response = mainIO.getResponse();
        return response;
    }public ServerResponse createServer(String name,String members){
        HashMap<String , String> data = new HashMap<>();
        data.put("name" , name);
        data.put("token" , token);
        data.put("members", members);
        mainIO.sendData(new DataPacket("createServer" , data));
        ServerResponse response = mainIO.getResponse();
        return response;
    }
    public ServerResponse addMemberToServer(String serverName , String memberusername){
        HashMap<String , String> data = new HashMap<>();
        data.put("servername" , serverName);
        data.put("token" , token);
        data.put("memberUsername" , memberusername);
        mainIO.sendData(new DataPacket("addMemberToServer" , data));
        ServerResponse response = mainIO.getResponse();
        return response;
    }
    public ServerResponse createChat(String chatName , String chatType, String ServerName , String members){ //todo tip : leave the members empty to add all the users of a channel to this chat
        HashMap<String , String> data = new HashMap<>();
        data.put("chatName" , chatName);
        data.put("chat_type" , chatType);
        data.put("ServerName" , ServerName);
        data.put("members" , members);
        data.put("token" , token);
        mainIO.sendData(new DataPacket("createChat" , data));
        ServerResponse response = mainIO.getResponse();
        return response;
    }

    public void sendMessage(String chat_id , String repliedTo , String message){
        HashMap<String , String> data = new HashMap<>();
        data.put("chat_id" , chat_id);
        data.put("token" , token); //fromid
        data.put("contentType" , "text"); //contenttype
        data.put("message" , message);
        data.put("replied_to_id" , repliedTo); //reply_id
        //date
        mainIO.sendData(new DataPacket("sendMessage" , data));
    }
    public boolean sendMessage(String chat_id , String file){
        HashMap<String , String> data = new HashMap<>();
        data.put("chat_id" , chat_id);
        data.put("token" , token);
        data.put("contentType" , "file");
        data.put("fileId" , file);
        data.put("replied_to_id","");//todo
        mainIO.sendData(new DataPacket("sendMessage",data));
        return false;
    }
    public UserObject getMyInformation(){
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        mainIO.sendData(new DataPacket("getInformation" , data));
        UserObject object = (UserObject)mainIO.readPacket().getData();
        return object;
    }
    public synchronized UserObject getInformationByID(String id){
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        data.put("id" , id);
        mainIO.sendData(new DataPacket("getSomeonesInformation" , data));
        UserObject object = (UserObject)mainIO.readPacket().getData();
        return object;
    }

    //todo : set permissions to users , send message inside a chat(group or direct) ,

    public String getToken() {
        return token;
    }

    public ArrayList<DiscordServer> getServers() {
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        mainIO.sendData(new DataPacket("ServerList" , data));
        ArrayList<DiscordServer> object = (ArrayList<DiscordServer>) mainIO.readData();
        return object;
    }
    public ArrayList<DiscordServer> getMyServers() {
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        mainIO.sendData(new DataPacket("MyServerList" , data));
        ArrayList<DiscordServer> object = (ArrayList<DiscordServer>) mainIO.readData();
        return object;
    }

    public ArrayList<DiscordChat> getListOfDirectChats() {
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        mainIO.sendData(new DataPacket("ChatsList" , data));
        ArrayList<DiscordChat> object = (ArrayList<DiscordChat>) mainIO.readData();
        ArrayList<DiscordChat> results = new ArrayList<>();
        for(DiscordChat chat : object){
            chat.setMembers(chat.getMembers().replace("-1,",""));
            if(chat.getMembers().split(",").length==2 && chat.getServerId().equals("-1")){
                UserObject first = getInformationByID(chat.getMembers().split(",")[0]);
                UserObject second = getInformationByID(chat.getMembers().split(",")[1]);
                chat.setName(first.getUsername()+" - "+second.getUsername());
                results.add(chat);
            }
        }
        return results;
    }


    public ArrayList<Message> getMessages(DiscordChat chatToMove) {
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        data.put("chat_id" , chatToMove.getId());
        mainIO.sendData(new DataPacket("MessageList" , data));
        ArrayList<Message> object = (ArrayList<Message>) mainIO.readData();

        return object;
    }

    public ResponseType changeUserAccessCode(String serverID,String usertobechangedUsername , String newAccessCode){
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        data.put("serverid" , serverID);
        data.put("usertochange" , usertobechangedUsername);
        data.put("accessCode" , newAccessCode);
        mainIO.sendData(new DataPacket("changeAccessCode" , data));
        ServerResponse response = mainIO.getResponse();
        return response.getType();
    }
    public ServerAccess getUserAccess(String serverId , String username){
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        data.put("serverid" , serverId);
        data.put("usertochange" , username);
        mainIO.sendData(new DataPacket("getUserAccessCode" , data));
        Object result = mainIO.readData();
        if(result instanceof ServerAccess)
            return (ServerAccess) result;
        else{
            if(result instanceof ServerResponse){
                System.out.println(((ServerResponse) result).getType());
            }
        }
        return null;
    }

    public ResponseType deleteServer(String getServerName) {
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        data.put("serverName" , getServerName);
        mainIO.sendData(new DataPacket("DeleteServer" , data));
        ServerResponse response = mainIO.getResponse();
        return response.getType();
    }

    public void downloadProfileImage(String username, FileIOEvent listener, FileTransferProgressEvent percentLitener) throws IOException {
        if(!Files.exists(Paths.get("ProfilePics\\" + username + ".jpg")))
        FileManager.downloadFile("ProfilePics\\"+username+".jpg","ProfilePics\\"+username+".jpg",listener,percentLitener,null,false);
        else
            FileManager.downloadFile("ProfilePics\\"+username+".jpg","ProfilePics\\"+username+".jpg",listener,percentLitener,null,true);

    }

    public UserObject getInformationByUsername(String fromUsername) {
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        data.put("username" , fromUsername);
        mainIO.sendData(new DataPacket("getSomeonesInformationU" , data));
        UserObject object = (UserObject)mainIO.readPacket().getData();
        return object;

    }

    public ArrayList<DiscordChat> getServerChats(String serverID) {
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        data.put("serverID" , serverID);

        mainIO.sendData(new DataPacket("getServerChats" , data));
        ArrayList<DiscordChat> chats = (ArrayList<DiscordChat>)(mainIO.readPacket().getData());
        return chats;
    }
    public ArrayList<Reaction> getReactions(String chat_id , String message_id){
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        data.put("chat_id" , chat_id);
        data.put("message_id" , message_id);
        mainIO.sendData(new DataPacket("getReactions",  data));
        return (ArrayList<Reaction>)mainIO.readData();
    }

    public ResponseType react(String chat_id , String message_id , String reaction){
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        data.put("chat_id" , chat_id);
        data.put("message_id" , message_id);
        data.put("reaction" , reaction);
        mainIO.sendData(new DataPacket("react" , data));
        ServerResponse response = mainIO.getResponse();
        return response.getType();
    }
    public ResponseType unreact(String chat_id , String message_id , String reaction){
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        data.put("chat_id" , chat_id);
        data.put("message_id" , message_id);
        data.put("reaction" , reaction);
        mainIO.sendData(new DataPacket("unreact" , data));
        ServerResponse response = mainIO.getResponse();
        return response.getType();
    }


    public ResponseType pinMessage(String chat_id, String messageID) {
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        data.put("chat_id" , chat_id);
        data.put("messageId" , messageID);
        mainIO.sendData(new DataPacket("SetPin" , data));
        ServerResponse result = mainIO.getResponse();
        return result.getType();
    }

    public ArrayList<DiscordChat> getListOfDirectChats(String username) {
        HashMap<String , String> data = new HashMap<>();
        data.put("token" , token);
        mainIO.sendData(new DataPacket("ChatsList" , data));
        ArrayList<DiscordChat> object = (ArrayList<DiscordChat>) mainIO.readData();
        ArrayList<DiscordChat> results = new ArrayList<>();
        for(DiscordChat chat : object){
            chat.setMembers(chat.getMembers().replace("-1,",""));
            if(chat.getMembers().split(",").length==2 && chat.getServerId().equals("-1")){
                UserObject first = getInformationByID(chat.getMembers().split(",")[0]);
                UserObject second = getInformationByID(chat.getMembers().split(",")[1]);
                chat.setName(first.getUsername().equals(username)?second.getUsername():first.getUsername());
                results.add(chat);
            }
        }
        return results;
    }

    public void joinCall(String chat_id, boolean voice) {
        HashMap<String , String> data = new HashMap<>();
        data.put("call" , chat_id);
        data.put("token" , token);
        callIO.sendData(new DataPacket("JoinCall" , data));
        Request.outgoingCalls.add(new CallObject(chat_id,new Date().getTime()));
        Request.callThread = new Thread(new CallListener(Main.listener.callSocket,token,callIO,chat_id,voice));
        Request.callThread.start();
    }
}
