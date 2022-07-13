package FileTransferProtocol;

import MainThread.DBManager;
import MainThread.Main;

import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class FileManager {
    public static String getLast(String filename){
        return filename.split("\\\\")[filename.split("\\\\").length-1];
    }
    public static ArrayList<Thread> FileTransferThreadPool = new ArrayList<>();
    public static void downloadFile(String destPath, Socket socket, boolean profileitis) {
        FileTransfer fileTransfer = new FileTransfer(true , socket,profileitis);

        if(!profileitis) {
            while (Files.exists(Paths.get(destPath))) {
                System.out.println(destPath);
                String extention = getExtension(destPath);
                System.out.println(extention + "    " + destPath);
                String fileName = getFileName(destPath);
                System.out.println(fileName + "    " + extention + "      " + destPath);
                String path = getPath(destPath);

                destPath = path + fileName + "C." + extention;
                System.out.println("new dest :" + destPath);
            }

            System.out.println("Saving to " + destPath);
            int id = -1;
            try {
                DBManager.query("INSERT INTO files(path) values(?)", new ArrayList<>(Arrays.asList(destPath)));
                id = Main.dataManagement.getNewFileid();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            fileTransfer.setId(id);
        }
        fileTransfer.setDestPath(destPath);
        Thread downloadThread = new Thread(fileTransfer);
        FileTransferThreadPool.add(downloadThread);
        downloadThread.start();

    }

    private static String getPath(String destPath) {
        return destPath.replace(getLast(destPath),"");
    }

    private static String getFileName(String destPath) {
        return getLast(destPath).replace("."+getExtension(destPath),"");
    }

    private static String getExtension(String destPath) {
        System.out.println("last : "+getLast(destPath));
        String last = getLast(destPath).trim();
        System.out.println(last.split("\\.").length);
        return getLast(destPath).split("\\.")[last.split("\\.").length-1];
    }

    public static void uploadFile(String filename , Socket socket,boolean b){
        FileTransfer fileTransfer = new FileTransfer(false , socket, b);
        fileTransfer.setFilename(filename);
        Thread thread = new Thread(fileTransfer);
        FileTransferThreadPool.add(thread);
        thread.start();
    }
}
