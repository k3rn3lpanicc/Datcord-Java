package LocalData;

import UserManagement.Request;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SaveAndLoad {
    synchronized public static void setIsRunning(boolean isRunning){
        SaveAndLoad.isRunning = isRunning;
    }
    public static boolean isRunning = true;
    public static Request loadData() throws IOException, ClassNotFoundException {
        Request request = null;
        FileInputStream inputStream = new FileInputStream("data.conf");
        ObjectInputStream oin = new ObjectInputStream(inputStream);
        request = (Request)oin.readObject();
        oin.close();
        inputStream.close();
        return request;
    }
    public static void SaveData(Request request) throws IOException {
        FileOutputStream fout = new FileOutputStream("data.conf");
        ObjectOutputStream oout = new ObjectOutputStream(fout);
        oout.writeObject(request);
    }
    public static boolean isFirstTime(){
        return !(Files.exists(Paths.get("data.conf")));
    }
}
