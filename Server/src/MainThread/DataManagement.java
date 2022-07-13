package MainThread;

import UserManagement.*;
import UserManagement.TokenManager.TokenObj;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DataManagement {
    void setToken(TokenObj token) throws SQLException;
    TokenObj getToken(String token);
    void removeToken(String token) throws SQLException;
    UserStatus getStatus(String username) throws SQLException;

    void removeStatus(String username);
    ArrayList<String> getFriendshipRequests(String username) throws SQLException;
    void dealFriendShipRequest(String fromUsername , String toUsername , String acceptance) throws SQLException;
    void createNewServer(String name , String ownerId,String members);
    void addMemberToFuckingServer(String servername , String memberid , String adder) throws SQLException;

    UserObject getUserById(String id) throws SQLException;
    UserObject getUserbyUsername(String username) throws SQLException;


    boolean userExistsinServer(String serverName, String userName);
    DiscordServer getServerByname(String name) throws SQLException;
    void createChat(ChatObject chat);
    public DiscordServer getServerByID(String id) throws SQLException;
    ChatObject getChatByName(String name) throws SQLException;

    void sendMessage(String chatID, String fromID, String contentType, String message, String date , String replyID,String fileID) throws SQLException, IOException, ClassNotFoundException;

    ArrayList<DiscordServer> getServersMemberisIn(String userID) throws SQLException;

    ArrayList<DiscordChat> getChatsOfUser(String username) throws SQLException;

    ArrayList<Message> getMessages(String chat_id) throws SQLException;

    boolean deleteServer(String token, String serverName) throws SQLException;

    ArrayList<DiscordServer> getServersMemberOwn(String userID) throws SQLException;

    ChatObject getChatByID(String chatID) throws SQLException;

    ArrayList<DiscordChat> getServerchats(String serverID) throws SQLException;
    int getNewFileid() throws SQLException;

    ServerAccess getUserAccess(String username , String serverID) throws SQLException;

    void changeAccessCode(String usernameTobeChanged, String serverID, String newAccessCode) throws SQLException;

    void setPin(String chatID, String messageId) throws SQLException;
    void react(String chat_id , String message_id , String from_id , String reaction) throws SQLException;
    void unreact(String chat_id , String message_id , String from_id , String reaction) throws SQLException;

    ArrayList<Reaction> getReactions(String chat_id, String message_id) throws SQLException;

    ArrayList<ChatObject> getAllChats() throws SQLException;
}
