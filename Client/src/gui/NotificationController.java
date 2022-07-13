package gui;

import gui.Main;
import gui.NotificationType;
import gui.UserInformation;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NotificationController implements Initializable {
    String title;
    String message;
    NotificationType notifType;
    Duration duration;
    Stage myStage;

    @FXML
    Label txtTitle;
    @FXML
    Label txtMessage;
    @FXML
    ImageView imgView;

    private String chat_id;
    private String server_id;
    private MainForm mainForm;

    public void setMessageInformation(String chat_id,String server_id){
        this.chat_id = chat_id;
        this.server_id = server_id;
    }
    public void setNotification(String title , String message , NotificationType type , Duration duration,Stage myStage,MainForm mainForm){
        this.title = title;
        this.message = message;
        this.notifType = type;
        this.duration = duration;
        this.myStage = myStage;
        this.mainForm = mainForm;
    }
    @FXML
    AnchorPane root;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    public void start(){
        imgView.setImage(new Image("file:"+ (notifType==NotificationType.ERROR?"error.png":(notifType==NotificationType.INFO?"info.png":(notifType==NotificationType.SUCCESS?"success.png":(notifType==NotificationType.WARNING?"warning.png":"discord.png"))))));

        txtTitle.setText(title);
        txtMessage.setText(message);
        flipIn();
    }
    void flipIn(){
        FadeTransition anim = new FadeTransition();
        anim.setNode(root);
        anim.setDuration(Duration.millis(200));
        anim.setToValue(1);
        anim.play();
        anim.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                waitForIt();
            }
        });
    }
    void waitForIt(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep((int)duration.toMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //myStage.close();
                goAway();
            }
        }).start();

    }
    void goAway(){
        FadeTransition anim = new FadeTransition();
        anim.setNode(root);
        anim.setToValue(0);
        anim.setDuration(Duration.millis(200));
        anim.play();
        anim.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                exit();
            }
        });
    }
    @FXML
    void exit(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                myStage.close();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(700);
                            AlertSystem.setIsFree(true);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
    @FXML
    void onClicked() throws IOException, InterruptedException {
        if(mainForm!=null){
            System.out.println("s , c "+chat_id);
            mainForm.loadMessage(server_id,chat_id);
            exit();
        }
    }


}
