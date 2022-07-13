package logger;

import java.io.Console;

public class Log4me {
    public static boolean log2file = false;

    public static void log(LogType type, String data){
        String color = "";
        switch (type){
            case INFO:
                color = "\u001B[32m";
            break;
            case ERROR:
                color ="\u001B[31m";
                break;
            case WARNING:
                color = "\u001B[33m";
        }
        System.out.println(color+"Logger@Server:~$ \t\t\t\t["+type.toString()+"] ("+data+")"+"\u001B[0m");
        if(log2file){
            //todo : log shit to file
        }
    }
    public static void logFrom(LogType type, String data,String from){
        String color = "";
        switch (type){
            case INFO:
                color = "\u001B[32m";
                break;
            case ERROR:
                color ="\u001B[31m";
                break;
            case WARNING:
                color = "\u001B[33m";
        }
        System.out.println(color+from+"@Server:~$ \t\t\t\t["+type.toString()+"] ("+data+")"+"\u001B[0m");
        if(log2file){
            //todo : log shit to file
        }
    }

}
