package gui;

import DiscordEvents.DownloadFininshedEvent;
import FileTransferProtocol.FileTransferProgressEvent;
import UserManagement.DiscordServer;
import UserManagement.ServerAccess;
import UserManagement.UserObject;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
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
import java.util.ResourceBundle;

public class ServerSettings implements Initializable {
    @FXML
    Label accept;
    Stage myStage;
    DiscordServer server;
    UserObject me;
    HashMap<String , String> usernameToID = new HashMap<>();


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
    @FXML
    void exit(){
        myStage.close();
    }
    @FXML
    TextField username;
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
    void accept(){

        exit();
    }

    public void setUsernameToID(HashMap<String, String> usernameToID) {
        this.usernameToID = usernameToID;
    }

    public void setMe(UserObject me) {
        this.me = me;
        myID = me.getId()+"";
    }

    public void setServer(DiscordServer server) {
        this.server = server;
    }

    public void setMyStage(Stage myStage) {
        this.myStage = myStage;
    }

    @FXML
    Label deleteServer;
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
    }
    public void start(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for(String username : usernameToID.keySet()){
                    UserObject user = Main.request.getInformationByUsername(username);
                    if((user.getId()+"").equals(myID))
                        continue;
                    ImageView tmp = new ImageView();
                    try {
                        Main.request.downloadProfileImage(user.getUsername(), new DownloadFininshedEvent() {
                            @Override
                            public void fileDownloadFinishedHandler(String filename) {
                                tmp.setImage(MainForm.generateProfilePicture(new Image("file:"+filename),45,45));
                                manager.downloadOrUploadFinished(filename);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        usersBox.getChildren().add(getNewPane(user,tmp.getImage()));

                                    }
                                });
                            }
                        }, new FileTransferProgressEvent() {
                            @Override
                            public void updateProgress(double percent, String fileName, Arc arc) {

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
    @FXML
    VBox usersBox;
    String myID;

    @FXML
    Pane prevPane;

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
    private Pane getNewPane(UserObject user, Image img) {
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
    ServerAccess myAccess;
    @FXML
    TextField chatName;
    @FXML
    ChoiceBox<String> choices;
    @FXML
    Label addprev1;
    public void setAccess(ServerAccess access) {
        choices.getItems().add("text");
        choices.getItems().add("voice");
        choices.getItems().add("screen");
        myAccess = access;
        addprev1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.request.createChat(chatName.getText(),choices.getValue(),server.getName(),"");
            }
        });
        if(access==null){
            //todo : i'm admin
            deleteServer.setVisible(true);
        }
        else{
            deleteServer.setVisible(false);
        }
    }
}
