package gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class AlertSystem {
    public static void AlertAndWait(Alert.AlertType type , String header , String title , String message){
        Alert alert = new Alert(type);
        alert.setResizable(false);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.setTitle(title);
        alert.showAndWait();
    }
    public static ButtonType AlertAndGet(Alert.AlertType type , String header , String title , String message){
        Alert alert = new Alert(type);
        alert.setResizable(false);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.setTitle(title);
        AtomicReference<ButtonType> result = null;
        alert.showAndWait().ifPresent(rs -> {
            result.set(rs);});
        return result.get();
    }
    public static ArrayList<NotificationObject> notificationQueue = new ArrayList<>();
    private static boolean isFree = true;
    public static synchronized void setIsFree(boolean isFree){
        System.out.println("isfree = "+isFree);
        AlertSystem.isFree = isFree;
        if(isFree == true && notificationQueue.size()!=0){
            System.out.println("going for next");
            NotificationObject next = notificationQueue.remove(0);
            notify(next.getTitle(),next.getMessage(),next.getType(),next.getDuration(),next.getMainForm(),next.getchatid(),next.getserverid());

        }

    }
    public static synchronized boolean getIsFree(){
        return isFree;
    }
    public static void Notify(String title, String message, NotificationType type, Duration duration, MainForm mainForm,String chat_id,String server_id){
        if(notificationQueue.size()!=0 || !getIsFree()){
            notificationQueue.add(new NotificationObject(title,message,duration,type,mainForm,chat_id,server_id));
            return;
        }else{
            setIsFree(false);
            notify(title,message,type,duration,mainForm,chat_id,server_id);
        }
    }
    private static void notify(String title, String message, NotificationType type, Duration duration, MainForm mainForm,String chat_id,String server_id){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(AlertSystem.class.getResource("Notification.fxml"));
                Scene scene = null;
                try {
                    Parent root = loader.load();
                    scene = new Scene(root);
                    stage.setScene(scene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setAlwaysOnTop(true);
                stage.setResizable(false);
                stage.setTitle("Notification");
                stage.getIcons().add(new Image("file:discord.png"));
                stage.initStyle(StageStyle.TRANSPARENT);
                scene.setFill(Color.TRANSPARENT);
                //stage.centerOnScreen();
                stage.setX(Screen.getPrimary().getBounds().getWidth()-453);
                stage.setY(Screen.getPrimary().getBounds().getHeight()-142);
                NotificationController controller = ((NotificationController)loader.getController());
                controller.setMessageInformation(chat_id,server_id);
                controller.setNotification(title,message,type , duration,stage,mainForm);
                controller.root.setOpacity(0);
                stage.show();

                controller.start();
            }
        });
    }

}
