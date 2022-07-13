package MainThread;

import HashModel.Hasher;
import NotifyService.NotifyBase;
import UserManagement.Server;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Scanner;

public class Main {
    public static void endProgram(){
        System.exit(0);
    }
    public static Scanner sc = new Scanner(System.in);
    public static DataManagement dataManagement;
    public static void main(String[] args) throws IOException, SQLException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
        dataManagement = new DBManager();
        Thread serverThread = new Thread(new Server());
        serverThread.start();
        DBManager.dbFile = "Data.db";
    }
}
