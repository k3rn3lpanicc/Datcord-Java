package UserManagement.TokenManager;

import java.time.LocalDateTime;
import java.util.*;

public class Token {
    public static HashSet<String> tokens = new HashSet<>();
    public static HashMap<String,String> tokenUsername = new HashMap<String,String>();
    public static ArrayList<TokenObj> tokenObjs = new ArrayList<>();
    static ArrayList<String> vals = new ArrayList<>(Arrays.asList("a" , "b" , "c" , "d" , "e" , "f" , "g" , "h", "i" , "j" , "k" , "l" , "m" , "n" , "o" , "p" , "q" , "r" , "s" , "t" , "u" , "v" , "w" , "x" , "y" , "z" , "0" , "1" , "2" , "3" , "4" , "5" , "6" ,"7" , "8" , "9" , "A" , "B" , "C" , "D" , "E" , "F" , "G" , "H" , "I" , "J" , "K" , "L" , "M" , "N" , "O" , "P" , "Q" , "R" ,"S" , "T" , "U" , "V" , "W" , "X" , "Y" , "Z"));
    private static String getRandomString(int length){
        Random rn = new Random(LocalDateTime.now().getSecond());
        StringBuilder result = new StringBuilder("");
        for(int i = 0;i<length;i++){
            result.append(vals.get(((Math.abs(rn.nextInt()))%vals.size())));
        }
        return result.toString();
    }
    public static void setToken(TokenObj tokenObj){
        tokenUsername.put(tokenObj.getToken() , tokenObj.getUsername());
        tokenObjs.add(tokenObj);
    }
    public static synchronized void removeUsername(String token){
        tokenUsername.remove(token);
        tokens.remove(token);
        tokenObjs.removeIf(tokenObj -> tokenObj.getToken().equals(token));
    }
    public static TokenObj getTokenByUsername(String username){
        for(TokenObj tokenObj : tokenObjs){
            if(tokenObj.getUsername().equals(username)){
                return tokenObj;
            }
        }
        return null;
    }

    public static String generateToken() {
        String k = getRandomString(15);
        if(tokens.contains(k)){
            return generateToken();
        }
        else{
            tokens.add(k);
            return k;
        }
    }
    public static TokenObj getToken(String token){
        //todo : check to see if the token exists in tokenUsernames
        for(TokenObj tokenObj : tokenObjs){
            if(tokenObj.getToken().equals(token)){
                return tokenObj;
            }
        }
        return null;
    }

}
