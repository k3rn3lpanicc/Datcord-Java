package MainThread;

import NotifyService.NotifyBase;
import UserManagement.*;
import UserManagement.TokenManager.Token;
import UserManagement.TokenManager.TokenObj;
import logger.Log4me;
import logger.LogType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;

public class DBManager implements DataManagement{
    //some usefull regex :
    //[A-Za-z]{2,10} => all words and numbers from 2 to 10 chars long
    // \\s : whitespace (space)
    // \\d : digit
    // \\D
    // zipcode : 5 digits long : \\s\\d{5}\\s
    // | => or : to combine regex
    // A[BCD] => AB , AC , AD
    // {n,} => minimum is n but no limit for maximum
    // + => one time
    // . ^ * + ? {} [] \ | () => need to be double backslashed
    // A+ is equal to A{1,}
    // (\\{{1,}) => finds words that have one or more {

    //[A-Za-z0-9._-]+@[A-Za-z0-9]+\\.[A-Za-z]{2,4}"



    //email address : [A-Za-z0-9.-_]+@[a-zA-Z0-9]+\\.[A-Za-z0-9]{2,4}

    //valid email address : [A-Za-z0-9._-]+@[A-Za-z0-9]+\\.[A-Za-z]{2,4}

    private static boolean regexChecker(String theRegex , String strToCheck){
        theRegex = " "+theRegex+" ";
        strToCheck = " "+strToCheck+" ";
        Pattern checkRegex = Pattern.compile(theRegex);
        Matcher matcher = checkRegex.matcher(strToCheck);
        while(matcher.find()){
            if(matcher.group().length()!=0){
                return true;
            }
        }
        return false;
    }
    public static String dbFile;
    private static Connection connect(){
        String url = "jdbc:sqlite:"+dbFile;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
   public static void query(String query , ArrayList<String> inputs) throws SQLException {
        //Log4me.log(LogType.WARNING , "Started quering : ["+query+"]");
        Connection conn =null;
        PreparedStatement pstmt = null;
        try {
            conn = DBManager.connect();
            pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= inputs.size(); i++) {
                    pstmt.setString(i, inputs.get(i - 1));
            }
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
            //Log4me.log(LogType.WARNING, "Ended quering : [" + query + "]");
            logger.Log4me.logFrom(LogType.INFO, "sql command : [" + query + "] and parameters : " + inputs, "DatabaseManager");
        }
        catch (Exception ex){
            ex.printStackTrace();
            try {
                Thread.sleep(100);
                query(query,inputs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        finally {
            if(pstmt!=null)
                pstmt.close();
            if(conn!=null)
                conn.close();
        }
   }
    public static void query(String query , ArrayList<String> inputs , int id) throws SQLException {
        Log4me.log(LogType.WARNING , "Started quering : ["+query+"]");
        Connection conn = DBManager.connect();
        PreparedStatement pstmt = conn.prepareStatement(query);
        int i;
        for(i = 1;i<=inputs.size();i++)
            pstmt.setString(i, inputs.get(i-1));
        pstmt.setInt(i , id);
        pstmt.executeUpdate();
        pstmt.close();
        conn.close();
        Log4me.log(LogType.WARNING , "Ended quering : ["+query+"]");
        logger.Log4me.logFrom(LogType.INFO , "sql command : ["+query+"] and parameters : "+inputs , "DatabaseManager");
    }
    public static void selectAll(){
        String sql = "SELECT * FROM users";
        try {
            Connection conn = DBManager.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
            rs.close();
            stmt.close();conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static boolean checkUser(String username,String password , String email,String phoneNumber) throws Exception {
        //todo : return a responsetype instead of boolean
        //todo : password is hashed and sent to server, so it must be checked to be valid inside the client app
        //check email with regex
        if(!regexChecker("[A-Za-z0-9._-]+@[A-Za-z0-9]+\\.[A-Za-z]{2,4}", email)){
            return false;
        }
        //phone numbers can be : +98 9936142371 , 09936142371 , +98 993 614 2371 , +98 993-614-2371
        if(!regexChecker("\\+?([0-9]{2})?\\s?(\\[0-9]{3})( |-)?(\\d{3})( |-)?(\\d{3,5})", phoneNumber) && !phoneNumber.isEmpty()){
            return false;
        }
        //username must be : 1)unique 2)digit|char with minimum size of 6
        if(!regexChecker("[A-Za-z0-9]{6,}",username)){
            return false;
        }
        return true;
    }
    public static void verifyUser(String userName) throws SQLException {
        UserObject userObject = getUserByUserNameFromunuser(userName);
        String sql = "INSERT into users (username,passwordHash , email) values(?,?,?)";
        query(sql , new ArrayList<>(Arrays.asList(userObject.getUsername() , userObject.getPasswordHash() , userObject.getEmail())));
        String sql2 =  "DELETE FROM unuser WHERE username=?";
        query(sql2 , new ArrayList<>(Arrays.asList(userName)));
    }
    public static String getVerifyCode(String username){
        String verifyCode = "";
        if(username.contains("'")){
            return "";
        }
        String sql = "SELECT vercode from unuser WHERE username='"+username+"'";
        try {
            Connection conn = DBManager.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while (rs.next()) {
                String ver = rs.getString("vercode");
                rs.close();
                conn.close();
                stmt.close();
                return ver;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static void changeProperty(String tableName , String columnName,String userName , String newValue) {
        try {
            query("UPDATE "+tableName+" SET "+columnName+"=? WHERE username=?" , new ArrayList<>(Arrays.asList(newValue,userName)));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public static String getField(String tableName , String columnName , String userName){

        return "";
    }
    public static boolean userExists(String userName , String passwordHash) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = '"+userName+"' and passwordHash = '"+passwordHash+"'";
        Connection conn = DBManager.connect();
        Statement stmt  = conn.createStatement();
        ResultSet rs    = stmt.executeQuery(sql);
        int num = 0;
        while(rs.next()){
            num++;
        }
        conn.close();
        stmt.close();
        rs.close();
        return  num==1;
    }



    public static UserObject getUserByUserName(String username) throws SQLException {
        UserObject userObject = null;
        userObject = new UserObject();
        System.out.println("getting "+username);
        String sql = "SELECT * FROM users WHERE username = '"+username+"'";
        Connection conn = DBManager.connect();
        Statement stmt  = conn.createStatement();
        ResultSet rs    = stmt.executeQuery(sql);
        while(rs.next()){
           userObject.setUsername(rs.getString("username"));
           userObject.setPasswordHash(rs.getString("passwordHash"));
           userObject.setEmail(rs.getString("email"));
           userObject.setPhoneNumber(rs.getString("phoneNumber"));
           String userStatus = rs.getString("status");
           if(userStatus==null || userStatus.isEmpty()){
               if (Token.getTokenByUsername(userObject.getUsername()) == null) {
                   userStatus = "Offline";
               } else {
                   userStatus = "Online";
               }
           }
           userObject.setStatus(UserStatus.valueOf(userStatus));
           userObject.setId(rs.getInt("id"));
           userObject.setFriends(new ArrayList<String>(Arrays.asList((rs.getString("friends")!=null)?(rs.getString("friends").split(",")) : new String[]{})));
            conn.close();
            rs.close();
            stmt.close();
           return userObject;
        }
        conn.close();
        return null;
    }
    public static UserObject getUserByUserNameFromunuser(String username) throws SQLException {
        UserObject userObject = null;
        userObject = new UserObject();
        String sql = "SELECT * FROM unuser WHERE username = '"+username+"'";
        Connection conn = DBManager.connect();
        Statement stmt  = conn.createStatement();
        ResultSet rs    = stmt.executeQuery(sql);
        while(rs.next()){
            userObject.setUsername(rs.getString("username"));
            userObject.setPasswordHash(rs.getString("passwordHash"));
            userObject.setEmail(rs.getString("email"));
            userObject.setId(rs.getInt("id"));
            conn.close();
            rs.close();
            stmt.close();
            return userObject;
        }
        conn.close();
        return null;
    }
    public static void addFriendShipRequest(String from , String to) throws Exception {
        //todo : check if a user exists before adding it to the database:\\\
        if(getUserByUserName(from)!=null && getUserByUserName(to)!=null) {
            UserObject fromUser = getUserByUserName(from);
            UserObject toUser = getUserByUserName(to);
            query("INSERT INTO friendship(from_id , to_id) values (?,?)", new ArrayList<>(Arrays.asList(String.valueOf(fromUser.getId()), String.valueOf(toUser.getId()))));
        }
        else{
            throw new Exception("the user does not exist");
        }
    }

    public static void signUp(String username, String passwordHash, String email, String verifyCode) throws Exception {
//        if(!checkUser(username,passwordHash,email,"")){
//            throw new Exception("ERROR username exists");
//        }
        UserObject userObject = getUserByUserName(username);
        if(userObject!=null){
            throw new Exception("ERROR username exists");
        }
        String sqlFirst = "DELETE FROM unuser WHERE username=?";
        query(sqlFirst , new ArrayList<>(Arrays.asList(username)));
        String sql = "INSERT INTO unuser (username,passwordHash,email,vercode,expire) VALUES (?,?,?,?,?)";
        //todo : generate an expire date to be set in the record
        query(sql , new ArrayList<>(Arrays.asList(username,passwordHash,email,verifyCode,"-")));
    }

    @Override
    public void setToken(TokenObj token) throws SQLException {
        Token.setToken(token);
        query("INSERT INTO tokens values(?,?,?,?,?)" , new ArrayList<>(Arrays.asList(token.getToken() , token.getUsername() , token.getPublicKey() , token.getSymmetricKey() , token.getDate())));
    }

    @Override
    public TokenObj getToken(String token) {
        return Token.getToken(token);
    }

    @Override
    public void removeToken(String token) throws SQLException {
        Token.removeUsername(token);
        query("DELETE FROM tokens WHERE token=?",new ArrayList<>(Arrays.asList(token)));
    }

    @Override
    public UserStatus getStatus(String username) throws SQLException {
        UserObject user = getUserByUserName(username);
        if(user!=null){
            return user.getStatus();
        }
        return null;
    }

    @Override
    public void removeStatus(String username) {
        try {
            query("UPDATE users set status=NULL WHERE username=?" , new ArrayList<>(Arrays.asList(username)));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getUsernameByID(String id) throws SQLException {
        String sql = "SELECT username from users where id="+id;
        Connection conn = DBManager.connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            String result =  rs.getString("username");
            rs.close();
            conn.close();
            return result;
        }
        rs.close();
        conn.close();
        return null;
    }
    public UserObject getUserByID(String id) throws SQLException {
        return getUserByUserName(getUsernameByID(id));
    }
    @Override
    public ArrayList<String> getFriendshipRequests(String username) throws SQLException {
        ArrayList<String> result = new ArrayList<>();
        if(userExists(username)){
            String sql = "SELECT * from friendship WHERE to_id = '"+getUserByUserName(username).getId()+"'"; //todo ooooooooooo . must get id by username here
            Connection conn = DBManager.connect();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while(rs.next()){
                result.add(getUsernameByID(rs.getString("from_id")));
            }
            conn.close();
            rs.close();stmt.close();
        }

        return result;
    }

    @Override
    public void dealFriendShipRequest(String fromUsername, String toUsername, String acceptance) throws SQLException {
        query("DELETE FROM friendship WHERE from_id=? and to_id=?" , new ArrayList<String>(Arrays.asList(String.valueOf(getUserByUserName(toUsername).getId()),String.valueOf(getUserByUserName(fromUsername).getId()))));
        if(acceptance.equals("accept")){
            UserObject fromUser = getUserByUserName(fromUsername);
            UserObject toUser = getUserByUserName(toUsername);
            String newFromUserFriends = fromUser.getFriends()+","+toUsername;
            String newToUserFriends = toUser.getFriends()+","+fromUsername;
            query("UPDATE users SET friends = ? WHERE username=?" , new ArrayList<>(Arrays.asList(newFromUserFriends , fromUsername)));
            query("UPDATE users SET friends = ? WHERE username=?" , new ArrayList<>(Arrays.asList(newToUserFriends , toUsername)));
            ChatObject chatObject = new ChatObject();
            chatObject.setMembers("-1,"+fromUser.getId()+","+toUser.getId());
            chatObject.setType("text");
            chatObject.setServerID("-1");
            chatObject.setName(fromUsername+"@"+toUsername);
            chatObject.setPinnedMessage(null);
            createChat(chatObject);
            try{
                NotifyBase.notifyUserByUsername(fromUsername,"NewDirectChatCreated");
                Log4me.log(LogType.INFO , "Notified "+fromUsername+" of new chat");
            } catch (Exception e) {
                //e.printStackTrace();
                Log4me.log(LogType.ERROR,"Couldn't Notify "+fromUsername+" , maybe its offline");
            }
            try {
                NotifyBase.notifyUserByUsername(toUsername,"NewDirectChatCreated");
                Log4me.log(LogType.INFO , "Notified "+toUsername+" of new chat");
            } catch (Exception e) {
                Log4me.log(LogType.ERROR,"Couldn't Notify "+toUsername+" , maybe its offline");
               // e.printStackTrace();
            }
        }
    }

    @Override
    public void createNewServer(String name , String ownerId,String members) {
        try {
            query("INSERT INTO Servers (name,ownerId,members) values(?,?,?)" , new ArrayList<>(Arrays.asList(name,ownerId,members)));
            DiscordServer created = getServerByname(name);

            ChatObject newChat = new ChatObject("","General","text",created.getId(),created.getMembers());
            createChat(newChat);
            for(String member : members.split(",")){
                if(!member.equals("")&&!member.equals("-1")){
                    try {
                        if(!member.equals(ownerId))
                            query("INSERT INTO Access(userid,serverid,code) values(?,?,?)" , new ArrayList<>(Arrays.asList(member,created.getId(),"10000011")));
                        NotifyBase.notifyUserByUsername(getUsernameByID(member),"ServerCreated",getServerByname(name));
                        Log4me.log(LogType.INFO , "Sent to "+member);
                    }
                    catch (Exception ex){
                        Log4me.log(LogType.ERROR , "User with id of "+member+" is not online to be notified");
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void saveServer(DiscordServer server) throws SQLException {
        System.out.println(server);
        query("UPDATE Servers set name=? , members=? where id = ?" , new ArrayList<>(Arrays.asList(server.getName() , server.getMembers())) , Integer.parseInt(server.getId()));
    }
    @Override
    public void addMemberToFuckingServer(String servername, String memberid, String adder) throws SQLException {
        DiscordServer server = getServerByname(servername);
        if(server!=null){
            //todo we must add user to server members, but we need acess? nah. they should be separated by commas
            // english tip : it's separated not seprated
            if(server.getMembers().equals("")){
                server.setMembers(memberid);
            }else server.setMembers(server.getMembers()+","+memberid);
            saveServer(server);
            logger.Log4me.log(LogType.INFO,"Member with id of "+memberid+" has been added to server by "+adder);
        }
        else{
            //server does not exist
        }
    }

    @Override
    public UserObject getUserById(String id) throws SQLException {
        return getUserByID(id);
    }

    @Override
    public UserObject getUserbyUsername(String username) throws SQLException {
        return getUserByUserName(username);
    }

    @Override
    public DiscordServer getServerByname(String name) throws SQLException {
        DiscordServer discordServer = new DiscordServer();
        String sql = "SELECT * from Servers WHERE name = '"+name+"'";
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while(rs.next()){
                discordServer.setId(String.valueOf(rs.getInt("id")));
                discordServer.setName(rs.getString("name"));
                discordServer.setOwnerId(rs.getString("ownerId"));
                discordServer.setMembers(rs.getString("members"));
                rs.close();
                conn.close();
                stmt.close();
                return discordServer;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            stmt.close();
            conn.close();
        }
        return null;
    }
    @Override
    public DiscordServer getServerByID(String id) throws SQLException {
        DiscordServer discordServer = new DiscordServer();
        String sql = "SELECT * from Servers WHERE id = "+id;
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while(rs.next()){
                discordServer.setId(String.valueOf(rs.getInt("id")));
                discordServer.setName(rs.getString("name"));
                discordServer.setOwnerId(rs.getString("ownerId"));
                discordServer.setMembers(rs.getString("members"));
                rs.close();
                conn.close();
                stmt.close();
                return discordServer;
            }
        } catch (SQLException throwables) {
            Log4me.log(LogType.ERROR , "Server Not Found");
            //throwables.printStackTrace();
        }
        finally {
            stmt.close();
            conn.close();
        }
        return null;
    }

    public ChatObject getChatByName(String chatName) throws SQLException {
        ChatObject chat = new ChatObject();
        String sql = "SELECT * from chats WHERE name = '"+chatName+"'";
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while(rs.next()){
                chat.setId(String.valueOf(rs.getInt("id")));
                chat.setName(rs.getString("name"));
                chat.setType(rs.getString("type"));
                chat.setMembers(rs.getString("members"));
                String messagePinnedId = rs.getString("pinnedMessageId");
                if(messagePinnedId!=null && !messagePinnedId.isEmpty()) {
                    chat.setPinnedMessage(getMessageByID(messagePinnedId,chat.getId()));
                }
                rs.close();
                stmt.close();
                conn.close();
                return chat;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            stmt.close();
            conn.close();
        }
        return null;
    }
    @Override
    public ChatObject getChatByID(String chatID) throws SQLException {
        ChatObject chat = new ChatObject();
        String sql = "SELECT * from chats WHERE id = "+chatID;
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while(rs.next()){
                chat.setId(String.valueOf(rs.getInt("id")));
                chat.setName(rs.getString("name"));
                chat.setType(rs.getString("type"));
                chat.setServerID(rs.getString("serverId"));
                chat.setMembers(rs.getString("members"));
                String messagePinnedId = rs.getString("pinnedMessageId");
                if(messagePinnedId!=null && !messagePinnedId.isEmpty()) {
                    chat.setPinnedMessage(getMessageByID(messagePinnedId,chat.getId()));
                }
                rs.close();
                conn.close();
                return chat;
            }
        } catch (SQLException throwables) {
            try {
                Thread.sleep(1000);
                return getChatByID(chatID);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            throwables.printStackTrace();
        }
        finally {
            stmt.close();
            conn.close();
        }
        return null;
    }

    @Override
    public ArrayList<DiscordChat> getServerchats(String serverID) throws SQLException {
        ArrayList<DiscordChat> result = new ArrayList<>();
        ChatObject chat = new ChatObject();
        String sql = "SELECT * from chats WHERE serverId = "+serverID;
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while(rs.next()){
                chat.setId(String.valueOf(rs.getInt("id")));
                chat.setName(rs.getString("name"));
                chat.setType(rs.getString("type"));
                chat.setMembers(rs.getString("members"));
                String messagePinnedId = rs.getString("pinnedMessageId");
                if(messagePinnedId!=null && !messagePinnedId.isEmpty()) {
                    chat.setPinnedMessage(getMessageByID(messagePinnedId,chat.getId()));
                }
                DiscordChat chats = new DiscordChat();
                chats.setMembers(chat.getMembers());
                chats.setServerId(chat.getServerID());
                chats.setType(chat.getType());
                chats.setName(chat.getName());
                chats.setId(chat.getId());
                result.add(chats);
            }
            return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            stmt.close();
            conn.close();
        }
        conn.close();
        return null;
    }

    public Message getMessageByID(String message_id , String chat_id) throws SQLException {
        Message message = null;
        String sql = "SELECT * from chat_"+chat_id+" WHERE message_id = "+message_id;
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while(rs.next()){
                if(rs.getString("contentType").equals("text")){
                    message = new TextMessage(rs.getString("content") , rs.getString("date") , rs.getString("from_id") , String.valueOf(rs.getInt("message_id")) , rs.getString("reply_id"),getUsernameByID(rs.getString("from_id")),chat_id);
                }
                else{
                    String fileID = new String(rs.getString("file_id").getBytes(), Charset.forName("UTF-8"));
                    String filename = getFilePathByID(fileID);
                    message = new FileMessage(filename,rs.getString("file_id"), rs.getString("date") , rs.getString("from_id") , String.valueOf(rs.getInt("message_id")) , rs.getString("reply_id") , getUsernameByID(rs.getString("from_id")),chat_id);
                }
                stmt.close();
                conn.close();
                return message;
            }
        } catch (SQLException throwables) {
            //throwables.printStackTrace();
            stmt.close();
            conn.close();
        }
        finally {
            stmt.close();
            conn.close();
        }
        stmt.close();
        conn.close();
        return null;
    }

    Message getLastMessage(String chatID) throws SQLException {
        Message message = null;
        String sql = "SELECT max(message_id) from chat_"+chatID;
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        stmt = conn.createStatement();
        ResultSet rs    = stmt.executeQuery(sql);
        while(rs.next()){
            int messageId = rs.getInt("max(message_id)");
            stmt.close();
            conn.close();
            return getMessageByID(String.valueOf(messageId),chatID);
        }
        stmt.close();
        conn.close();
        return null;
    }
    public int getNewFileid() throws SQLException {
        Message message = null;
        String sql = "SELECT max(id) from files";
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        stmt = conn.createStatement();
        ResultSet rs    = stmt.executeQuery(sql);
        while(rs.next()){
            int messageId = rs.getInt("max(id)");
            stmt.close();
            conn.close();
            return messageId;
        }
        stmt.close();
        conn.close();
        return -1;
    }

    @Override
    public ServerAccess getUserAccess(String username, String serverID) throws SQLException {
        ServerAccess result;
        String sql = "SELECT code FROM Access WHERE serverid = "+serverID+" and userid = "+getUserByUserName(username).getId();
        Connection conn = DBManager.connect();
        Statement stmt  = conn.createStatement();
        ResultSet rs    = stmt.executeQuery(sql);
        while(rs.next()){
            String accesscode = rs.getString("code");
            result = ServerAccess.fromCode(accesscode);
            conn.close();
            rs.close();
            stmt.close();
            return result;
        }
        rs.close();
        stmt.close();
        conn.close();
        return ServerAccess.fromCode(getServerByID(serverID).getOwnerId().equals(getUserByUserName(username).getId()+"")?"11111111":null);
    }

    @Override
    public void changeAccessCode(String usernameTobeChanged, String serverID, String newAccessCode) throws SQLException {
        UserObject usr = getUserByUserName(usernameTobeChanged);
        query("UPDATE Access set code=? where serverid=? and userid=?",new ArrayList<>(Arrays.asList(newAccessCode,serverID,usr.getId()+"")));
    }

    @Override
    public void setPin(String chatID, String messageId) throws SQLException {
        ChatObject chat = getChatByID(chatID);
        query("UPDATE chats set pinnedMessageId=? where name=?" , new ArrayList<>(Arrays.asList(messageId,chat.getName())));
    }

    @Override
    public void react(String chat_id, String message_id, String from_id, String reaction) throws SQLException {
        query("INSERT INTO Reactions (chatid,message_id,from_id,reaction) values(?,?,?,?)" , new ArrayList<>(Arrays.asList(chat_id,message_id,from_id,reaction)));
    }
    @Override
    public void unreact(String chat_id, String message_id, String from_id, String reaction) throws SQLException {
        query("DELETE FROM Reactions where chatid = ? and message_id = ? and from_id = ? and reaction=?" , new ArrayList<>(Arrays.asList(chat_id,message_id,from_id,reaction)));
    }

    @Override
    public ArrayList<Reaction> getReactions(String chat_id, String message_id) throws SQLException {
        ArrayList<Reaction> result = new ArrayList<>();
        String sql = "SELECT * from Reactions where chatid='"+chat_id+"' and message_id='"+message_id+"'";
        System.out.println(sql);
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while(rs.next()){
                Reaction reaction = new Reaction(rs.getString("chatid"),rs.getString("message_id"),rs.getString("from_id"),rs.getString("reaction"));
                result.add(reaction);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            stmt.close();
            conn.close();
        }
        System.out.println(result);
        return result;
    }

    @Override
    public ArrayList<ChatObject> getAllChats() throws SQLException {
        ArrayList<ChatObject> chats = new ArrayList<>();
        String sql = "SELECT * from chats";
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while(rs.next()){
                ChatObject chat = new ChatObject();
                chat.setId(String.valueOf(rs.getString("id")));
                chat.setName(rs.getString("name"));
                chat.setMembers(rs.getString("members").replace("-1," , ""));
                chat.setServerID(rs.getString("serverId"));
                chat.setType(rs.getString("type"));
                String messagePinnedId = rs.getString("pinnedMessageId");
                if(messagePinnedId!=null && !messagePinnedId.isEmpty()) {
                    chat.setPinnedMessage(getMessageByID(messagePinnedId,chat.getId()));
                }
                chats.add(chat);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            stmt.close();
            conn.close();
        }
        return chats;
    }


    int sendMessageAndgetID(String chatID, String fromID, String contentType, String message, String date , String replyID,String fileID) throws SQLException {
        query("INSERT INTO chat_"+chatID+" (from_id,contentType,content,file_id,reply_id,date) values(?,?,?,?,?,?)" , new ArrayList<>(Arrays.asList(fromID, contentType, message , fileID , replyID , date)));
        int id = Integer.parseInt(getLastMessage(chatID).getMessageID());
        return id;
    }
    private DiscordChat getChatFromChatObject(ChatObject o){
        DiscordChat chat = new DiscordChat();
        chat.setId(o.getId());
        chat.setName(o.getName());
        chat.setMembers(o.getMembers());
        chat.setType(o.getType());
        chat.setServerId(o.getServerID());
        chat.setPinnedMessage(o.getPinnedMessage());
        return chat;
    }
    @Override
    public void sendMessage(String chatID, String fromID, String contentType, String message, String date , String replyID,String fileID) throws SQLException, IOException, ClassNotFoundException{
        //1 - find the chat and check if it exists
        //2 - add the message to it
        //3 - get the users
        //4 - send it through their notify socket (to all of them)

        ChatObject chat = getChatByID(chatID);
        DiscordChat chat2 = getChatFromChatObject(chat);
        if(chat==null){
            logger.Log4me.log(LogType.ERROR , "the chat does not exist");
            return;
        }
        if(!replyID.isEmpty()){
            Message messages = getMessageByID(replyID,chatID);
            if(messages==null){
                logger.Log4me.log(LogType.ERROR , "the repliedTo Message does not exist, chat_id:"+chatID+" , repliedID:"+replyID);
                return;
            }
        }

        int id = sendMessageAndgetID(chatID , fromID , contentType , message , date , replyID,fileID);
        Message sent = getMessageByID(String.valueOf(id),chatID);
        String members = chat.getMembers();
        System.out.println(members);
        for(String s : members.split(",")){
            if(!s.equals("")&&!s.equals("-1")){
                try {
                    NotifyBase.notifyUser(Token.getTokenByUsername(getUsernameByID(s)).getToken(),"ChatMessage", sent);
                    System.out.println("don1");
                    NotifyBase.notifyUser(Token.getTokenByUsername(getUsernameByID(s)).getToken(),"ChatMessage", chat2);
                    System.out.println("done2");
                    Log4me.log(LogType.INFO , "Sent to "+s);
                }
                catch (Exception ex){
                    Log4me.log(LogType.ERROR , "User with id of "+s+" is not online to be notified");
                }
            }
        }

    }

    @Override
    public ArrayList<DiscordServer> getServersMemberisIn(String userID) throws SQLException {
        ArrayList<DiscordServer> result = new ArrayList<>();
        String sql = "SELECT * from Servers where members like '%"+userID+"%'";
        System.out.println(sql);
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while(rs.next()){
                DiscordServer server = new DiscordServer(String.valueOf(rs.getInt("id")) , rs.getString("name") , rs.getString("ownerId") , rs.getString("members").replace("-1,",""));
                result.add(server);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            stmt.close();
            conn.close();
        }
        System.out.println(result);
        return result;
    }

    @Override
    public ArrayList<DiscordChat> getChatsOfUser(String username) throws SQLException {
        ArrayList<DiscordChat> chats = new ArrayList<>();
        String sql = "SELECT * from chats where members like '%"+getUserByUserName(username).getId()+"%'";
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while(rs.next()){
                DiscordChat chat = new DiscordChat();
                chat.setId(String.valueOf(rs.getString("id")));
                chat.setName(rs.getString("name"));
                chat.setMembers(rs.getString("members").replace("-1," , ""));
                chat.setServerId(rs.getString("serverId"));
                chat.setType(rs.getString("type"));
                String messagePinnedId = rs.getString("pinnedMessageId");
                if(messagePinnedId!=null && !messagePinnedId.isEmpty()) {
                    chat.setPinnedMessage(getMessageByID(messagePinnedId,chat.getId()));
                }
                chats.add(chat);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            stmt.close();
            conn.close();
        }
        return chats;
    }

    public String getFilePathByID(String fileid) throws SQLException {
        String sql = "SELECT * from files where id="+fileid;
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while(rs.next()){
                String path = rs.getString("path");
                return path;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                Thread.sleep(100);
                return getFilePathByID(fileid);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        finally {
            stmt.close();
            conn.close();
        }
        return null;
    }
    @Override
    public ArrayList<Message> getMessages(String chat_id) throws SQLException {
        ArrayList<Message> chats = new ArrayList<>();
        String sql = "SELECT * from chat_"+chat_id;
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while(rs.next()){
                if(rs.getString("contentType").equals("text")) {
                    Message message = new TextMessage(rs.getString("content"), rs.getString("date"), rs.getString("from_id"), String.valueOf(rs.getString("message_id")), rs.getString("reply_id"), getUsernameByID(rs.getString("from_id")), chat_id);
                    chats.add(message);
                }
                else{
                    String fileID = rs.getString("file_id");
                    String filename = getFilePathByID(fileID);
                    Message message = new FileMessage(filename,rs.getString("file_id"),rs.getString("date"),rs.getString("from_id"),rs.getString("message_id"),rs.getString("reply_id"),getUsernameByID(rs.getString("from_id")),chat_id);
                    chats.add(message);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            stmt.close();
            conn.close();
        }
        return chats;
    }

    @Override
    public boolean deleteServer(String token, String serverName) throws SQLException {
        if(getServerByname(serverName)==null)
            return false;
        boolean found = getServerByname(serverName).getOwnerId().equals(getUserByUserName(Token.getToken(token).getUsername()).getId()+"");
        query("DELETE FROM Servers WHERE name = ? and ownerId = ?" , new ArrayList<>(Arrays.asList(serverName, getUserByUserName(Token.getToken(token).getUsername()).getId()+"")));
        return found;
    }

    @Override
    public ArrayList<DiscordServer> getServersMemberOwn(String userID) throws SQLException {
        ArrayList<DiscordServer> result = new ArrayList<>();
        String sql = "SELECT * from Servers where ownerId ='"+userID+"'";
        System.out.println(sql);
        Connection conn = DBManager.connect();
        Statement stmt  = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while(rs.next()){
                DiscordServer server = new DiscordServer(String.valueOf(rs.getInt("id")) , rs.getString("name") , rs.getString("ownerId") , rs.getString("members").replace("-1,",""));
                result.add(server);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally {
            stmt.close();
            conn.close();
        }
        System.out.println(result);
        return result;
    }

    @Override
    public void createChat(ChatObject chat) {
        if(!chat.getServerID().isEmpty()&&!chat.getServerID().equals("-1")) {
            try {
                DiscordServer server = getServerByID(chat.getServerID());
                chat.setName(chat.getName()+"@"+server.getName());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        //todo : we must add the chat to the chats list and getits id and create a new table with its id and some columns
        try {
            query("INSERT INTO chats (name,type,serverId,members) values(?,?,?,?)" , new ArrayList<>(Arrays.asList(chat.getName() , chat.getType() , chat.getServerID(),chat.getMembers())));
            ChatObject createdChat = getChatByName(chat.getName());
            query("CREATE TABLE chat_"+createdChat.getId()+"(message_id INTEGER PRIMARY KEY,from_id TEXT NOT NULL,contentType TEXT NOT NULL,content TEXT NOT NULL,file_id TEXT,reply_id TEXT,date TEXT NOT NULL);" , new ArrayList<>(Arrays.asList()));
            logger.Log4me.logFrom(LogType.INFO , "New chat created" , "DatabaseManager");
        } catch (SQLException throwables) {
            logger.Log4me.log(LogType.ERROR , "Couldnt create the chat");
            throwables.printStackTrace();
        }

    }

    @Override
    public boolean userExistsinServer(String serverName, String userName) {
        try {
            UserObject user = getUserByUserName(userName);
            if(user!=null){
                String userid = String.valueOf(user.getId());
                return getServerByname(serverName).getMembers().contains(userid);
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
            logger.Log4me.log(LogType.ERROR,"The server ["+serverName+"] does not exist");
        }
        //todo important : here false means its not in server or the server does not exist
        return false;
    }


    private boolean userExists(String userName) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = '"+userName+"'";
        Connection conn = DBManager.connect();
        Statement stmt  = conn.createStatement();
        ResultSet rs    = stmt.executeQuery(sql);
        int num = 0;
        while(rs.next()){
            num++;
        }
        rs.close();
        stmt.close();
        conn.close();
        return  num==1;
    }

}
