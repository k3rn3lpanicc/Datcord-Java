package gui;

import DiscordEvents.DownloadFininshedEvent;
import FileTransferProtocol.FileManager;
import UserManagement.UserObject;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserInformation implements Initializable , DownloadFininshedEvent {
    @FXML
    Label lUsername;
    @FXML
    Label lemail;
    @FXML
    Label lphoneNumber;
    @FXML
    ImageView Profile;
    @FXML
    Label lStat;

    void setInformation(UserObject user) throws IOException {
        lUsername.setText(user.getUsername());
        lemail.setText(user.getEmail());
        lphoneNumber.setText(user.getPhoneNumber());
        Profile.setCursor(Cursor.HAND);
        lStat.setText("Status : "+user.getStatus().toString().toUpperCase());
        Main.request.downloadProfileImage(user.getUsername(),this,manager);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Profile.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
            }
        });
        
    }

    @Override
    public void fileDownloadFinishedHandler(String filename) {
        mainForm.fileDownloadFinishedHandler(filename);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try{Profile.setImage(new Image("file:"+filename)); }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }
    DownloadManager manager;
    MainForm mainForm;
    public void setManager(DownloadManager manager,MainForm mainForm) {
        this.manager = manager;
        this.mainForm = mainForm;
    }

}
