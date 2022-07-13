package Caller;
import CallService.CallPacket;
import DataTransmit.SocketDataTransfer;

import java.io.*;
import javax.sound.sampled.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;

public class CallerService {
    private static HashMap<String , Call> calls = new HashMap<>();

    static ArrayList<String> vals = new ArrayList<>(Arrays.asList("a" , "b" , "c" , "d" , "e" , "f" , "g" , "h", "i" , "j" , "k" , "l" , "m" , "n" , "o" , "p" , "q" , "r" , "s" , "t" , "u" , "v" , "w" , "x" , "y" , "z" , "0" , "1" , "2" , "3" , "4" , "5" , "6" ,"7" , "8" , "9" , "A" , "B" , "C" , "D" , "E" , "F" , "G" , "H" , "I" , "J" , "K" , "L" , "M" , "N" , "O" , "P" , "Q" , "R" ,"S" , "T" , "U" , "V" , "W" , "X" , "Y" , "Z"));
    private static synchronized String getRandomString(int length){
        Random rn = new Random(LocalDateTime.now().getSecond());
        StringBuilder result = new StringBuilder("");
        for(int i = 0;i<length;i++){
            result.append(vals.get(((Math.abs(rn.nextInt()))%vals.size())));
        }
        return result.toString();
    }

     public static synchronized String addCall(CallType type, String token, SocketDataTransfer IO){
        Call call = new Call(type);
        call.addToCall(token,IO);
        String callID =getRandomString(30);
        calls.put(callID,call);
        return callID;
    }
    public static synchronized void addCall(CallType type,String callID){
        Call call = new Call(type);
        calls.put(callID,call);
    }
    public static synchronized void broadCast(String token , CallPacket packet){
        try {
            calls.get(packet.getCallID()).broadcastToUsers(token, packet);
        }
        catch (Exception ex){
            return;
        }
    }

    public static synchronized void addToCall(String token , SocketDataTransfer IO,String callID){
        if(calls.keySet().contains(callID)){
            calls.get(callID).addToCall(token,IO);
        }
        else{
            System.out.println("call does not fucking exist");
        }
    }

    public static synchronized boolean removeFromCall(String token, String callID) {
        if(calls.keySet().contains(callID)){
            if(calls.get(callID).tokens.contains(token)){
                calls.get(callID).removeFromCall(token);

                return true;
            }
        }
        return false;
    }
}
