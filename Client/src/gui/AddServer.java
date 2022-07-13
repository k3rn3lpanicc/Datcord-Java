package gui;

import DiscordEvents.DownloadFininshedEvent;
import FileTransferProtocol.FileTransferProgressEvent;
import UserManagement.UserObject;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

public class AddServer implements Initializable {

    HashMap<String , String> usernameToID = new HashMap<>();

    @FXML
    TextField serverName;
    Stage mystage;
    @FXML
    TextField username;

    @FXML
    void exit(){
        mystage.close();
    }
    double xOffset;
    double yOffset;
    @FXML
    public void mousePressed(MouseEvent event) {
        xOffset = ((Stage)((Node)(event.getSource())).getScene().getWindow()).getX() - event.getScreenX();
        yOffset = ((Stage)((Node)(event.getSource())).getScene().getWindow()).getY() - event.getScreenY();
    }
    @FXML
    public void drg(MouseEvent event){
        ((Stage)((Node)(event.getSource())).getScene().getWindow()).setX(event.getScreenX() + xOffset);
        ((Stage)((Node)(event.getSource())).getScene().getWindow()).setY(event.getScreenY() + yOffset);
    }

    public void setMystage(Stage mystage) {
        this.mystage = mystage;
    }
    @FXML
    void accept(){
        String serversname = serverName.getText();
        String members = "-1,"+myID;
        for(String key : usernameToID.keySet()){
            members+=","+usernameToID.get(key);
        }
        Main.request.createServer(serversname,members);
        exit();
    }

    @FXML
    void addUser(){

    }
    @FXML
    ImageView imageprev;
    @FXML
    Label nameprev;
    @FXML
    Label addprev;
    DownloadManager manager;

    public void setManager(DownloadManager manager) {
        this.manager = manager;
    }

    @FXML
    VBox usersBox;
    String myID;

    public void setMyID(String myID) {
        this.myID = myID;
    }

    @FXML
    void search() throws IOException {
        if(usernameToID.get(username.getText())!=null){
            return;
        }
        UserObject user = Main.request.getInformationByUsername(username.getText());
        if(user!=null){
            System.out.println("ID : "+user.getId());
            System.out.println("Mine : "+myID);
            if(String.valueOf(user.getId()).equals(myID)){
                nameprev.setText("You are owner");
                addprev.setDisable(true);
                imageprev.setImage(null);
                return;
            }
            nameprev.setText(user.getUsername());
            nameprev.setTooltip(new Tooltip(nameprev.getText()));
            Main.request.downloadProfileImage(user.getUsername(), new DownloadFininshedEvent() {
                @Override
                public void fileDownloadFinishedHandler(String filename) {
                    imageprev.setImage(MainForm.generateProfilePicture(new Image("file:"+filename),45,45));
                    manager.downloadOrUploadFinished(filename);
                }
            }, new FileTransferProgressEvent() {
                @Override
                public void updateProgress(double percent, String fileName, Arc arc) {

                }
            });
            addprev.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    usernameToID.put(user.getUsername(),user.getId()+"");
                    usersBox.getChildren().add(getNewPane(user,imageprev.getImage()));
                    nameprev.setText("");
                    addprev.setDisable(true);
                    imageprev.setImage(null);
                }
            });
            addprev.setDisable(false);
        }
        else{
            nameprev.setText("NOT FOUND");
            addprev.setDisable(true);
            imageprev.setImage(null);
        }
    }
    @FXML
    Pane prevPane;
    private Pane getNewPane(UserObject user,Image img) {
        Pane result = new Pane();
        result.setStyle(prevPane.getStyle());
        result.setPrefHeight(prevPane.getPrefHeight());

        Label usersName = new Label();
        usersName.setText(user.getUsername());
        usersName.setTextFill(nameprev.getTextFill());
        usersName.setLayoutX(nameprev.getLayoutX());
        usersName.setLayoutY(nameprev.getLayoutY());
        usersName.setFont(nameprev.getFont());

        result.getChildren().add(usersName);

        ImageView profileImage = new ImageView();
        profileImage.setLayoutY(imageprev.getLayoutY());
        profileImage.setLayoutX(imageprev.getLayoutX());
        profileImage.setPreserveRatio(false);
        profileImage.setImage(img);

        result.getChildren().add(profileImage);

        Label removeFromList = new Label();
        removeFromList.setText("❌");
        removeFromList.setFont(addprev.getFont());
        removeFromList.setTextFill(Color.RED);
        removeFromList.setLayoutX(addprev.getLayoutX()+40);
        removeFromList.setLayoutY(addprev.getLayoutY());

        removeFromList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                usernameToID.remove(user.getUsername());
                usersBox.getChildren().remove(result);
            }
        });
        result.getChildren().add(removeFromList);

        return result;
    }
    @FXML
    Label accept;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        username.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)){
                    try {
                        search();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        serverName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(serverName.getText().isEmpty()){
                    accept.setDisable(true);
                }
                else
                    accept.setDisable(false);
            }
        });

    }
}
