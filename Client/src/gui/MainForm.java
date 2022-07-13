package gui;

import Audio.AudioHandler;
import CallOB.CallRequest;
import CallService.CallListener;
import CallService.CallPacket;
import DataTransmit.DataPacket;
import DataTransmit.SocketDataTransfer;
import DiscordEvents.*;
import FileTransferProtocol.FileManager;
import FileTransferProtocol.FileTransferProgressEvent;
import LocalData.SaveAndLoad;
import UserManagement.*;
import gui.emojiset.Emoji;
import gui.emojiset.Emojies;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainForm implements Initializable , NewMessageArrivedEvent, DownloadFininshedEvent , UploadFinishedEvent {
    UserObject me;
    double xOffset;
    double yOffset;
    @FXML
    ImageView profileBottom;
    @FXML
    ImageView muteBottom;
    @FXML
    ImageView speakerBottom;
    @FXML
    ImageView settingsBottom;
    @FXML
    Pane groupOthersPane;
    @FXML
    Pane msgAreaOthersPane;
    @FXML
    Pane groupOthersProfilePane;
    @FXML
    Pane fileMessagePane;
    @FXML
    Label fileNameLabel;
    @FXML Label fileDate;
    @FXML
    Arc filePercentage;
    @FXML
    Label downloadLabel;
    @FXML
    Label msgContent;
    @FXML
    Pane chatPane;
    @FXML
    Label chatTypeLabel;
    @FXML
    Label chatNameLabel;
    @FXML
    Pane sideProfPane;
    @FXML
    ImageView sideProfPic;
    @FXML
    Label sideprofUsername;
    @FXML
    Label sideprofStatus;
    @FXML
    Label selectedServerName;
    @FXML VBox chatsVB;
    @FXML
    ImageView serverSettings;
    @FXML
    GridPane emojiPane;
    @FXML
    VBox chatBoxes;
    @FXML Label userNameLabel;
    @FXML
    Label likeEmojis;
    @FXML
    Label countT;
    Label lastReplied;
    @FXML Circle rooE;
    int repliyingToMessageID = -1;
    @FXML
    ScrollPane mainPane;
    @FXML
    VBox sideprofsVB;
    @FXML
    TextField typedText;
    @FXML
    Label replyLabel;
    ArrayList<Message> messagesTMP = new ArrayList<>();
    @FXML
    Label myNameBelow;
    DownloadManager manager;
    @FXML
    VBox chatArea;
    @FXML
    ImageView img1;
    @FXML
    Pane emojipane2;

    boolean sy = false;

    Pane getSideprofPane(UserObject user){
        Pane result = new Pane();
        Label username = new Label();
        Label status = new Label();
        ImageView profilePic = new ImageView();
        profilePic.setLayoutY(sideProfPic.getLayoutY()-6);
        profilePic.setLayoutX(sideProfPic.getLayoutX());
        String fromUsername = user.getUsername();
        ImageWaitingEvent myEvent = new ImageWaitingEvent() {
            @Override
            public void Finished(String filename, String username, Image img) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        profilePic.setImage(img);
                    }
                });
            }
        };
        ImagePool.addListener(fromUsername,myEvent);
        result.getChildren().addAll(username,status,profilePic);
        result.setStyle(sideProfPane.getStyle());
        username.setText(user.getUsername());
        username.setTextFill(sideprofUsername.getTextFill());
        username.setFont(sideprofUsername.getFont());
        username.setLayoutX(sideprofUsername.getLayoutX());
        username.setLayoutY(sideprofUsername.getLayoutY()-3);
        MainForm mms = this;
        username.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                showUserInformation(fromUsername,mms);
            }
        });
        username.setCursor(Cursor.HAND);
        profilePic.setOnMouseClicked(username.getOnMouseClicked());
        profilePic.setCursor(Cursor.HAND);
        status.setText(user.getStatus().toString());
        status.setTextFill(sideprofStatus.getTextFill());
        status.setLayoutY(sideprofStatus.getLayoutY()-8);
        status.setLayoutX(sideprofStatus.getLayoutX());
        return result;
    }
    Pane getAChatPane(DiscordChat chat){
        Pane result = new Pane();
        Label chatType = new Label();
        Label chatName = new Label();
        result.setStyle(chatPane.getStyle());
        result.setPrefHeight(chatPane.getPrefHeight());

        chatName.setFont(chatNameLabel.getFont());
        chatName.setTextFill(chatNameLabel.getTextFill());
        chatName.setLayoutX(chatNameLabel.getLayoutX());
        chatName.setLayoutY(chatNameLabel.getLayoutY());
        chatName.setCursor(Cursor.HAND);
        chatName.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //todo : this will happen when they click on the chat name
                try {
                    if(currentCallID!=null){
                        Main.request.disconnectFromCall(currentCallID);
                        currentCallID = null;
                    }
                    if(chat.getType().equals("text"))
                        setChatMessages(chat.getId());
                    else if(chat.getType().equals("voice"))
                        setVoiceChannel(chat.getId());
                    else if(chat.getType().equals("screen"))
                        setScreenChannel(chat.getId());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        chatType.setFont(chatTypeLabel.getFont());
        chatType.setTextFill(chatTypeLabel.getTextFill());
        chatType.setLayoutY(chatTypeLabel.getLayoutY());
        chatType.setLayoutX(chatTypeLabel.getLayoutX());
        chatType.setText(chat.getType().equals("text")?"- #":"- \uD83D\uDD0A");
        chatName.setText(chat.getName().split("@")[0]);
        result.getChildren().addAll(chatName,chatType);

        return result;
    }
    String callingToUsername;
    Pane getADMChatPane(DiscordChat chat){

        String username = chat.getName();
        Pane result = new Pane();
        Label chatName = new Label();
        ImageView imageView = new ImageView();

        try {
            Main.request.downloadProfileImage(username, new DownloadFininshedEvent() {
                @Override
                public void fileDownloadFinishedHandler(String filename) {
                    imageView.setImage(generateProfilePicture(new Image("file:"+filename),38,38));
                }
            },manager);
        } catch (IOException e) {
            e.printStackTrace();
        }

        result.setStyle(chatPane.getStyle());
        result.setPrefHeight(chatPane.getPrefHeight()+10);
        chatName.setFont(chatNameLabel.getFont());
        chatName.setTextFill(chatNameLabel.getTextFill());
        chatName.setLayoutX(chatNameLabel.getLayoutX()+5);
        chatName.setLayoutY(chatNameLabel.getLayoutY()+6);
        chatName.setCursor(Cursor.HAND);
        chatName.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //todo : this will happen when they click on the chat name
                try {
                    setChatMessages(chat.getId());
                    callUser.setVisible(true);
                    callingToUsername = username;

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        imageView.setLayoutY(chatTypeLabel.getLayoutY()+1);
        imageView.setLayoutX(chatTypeLabel.getLayoutX());
        MainForm mm = this;
        imageView.setCursor(Cursor.HAND);
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                showUserInformation(username,mm);
            }
        });
        imageView.setFitWidth(38);
        imageView.setFitHeight(38);
        imageView.setPreserveRatio(false);
        chatName.setText(chat.getName().split("@")[0]);
        result.getChildren().addAll(chatName,imageView);
        return result;
    }


    @FXML
    void loadServerSettings(){
        Stage stage = new Stage();
        ServerAccess access = Main.request.getUserAccess(selectedServer.getId(),me.getUsername());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ServerSettings.fxml"));
        Scene scene = null;
        try {
            Parent root = loader.load();
            scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setResizable(false);
        stage.setTitle("ServerSettings");
        ((ServerSettings)loader.getController()).setMyStage(stage);
        ((ServerSettings)loader.getController()).setServer(selectedServer);
        ((ServerSettings)loader.getController()).setMe(me);
        ((ServerSettings)loader.getController()).setManager(manager);
        HashMap<String , String> nametoid = new HashMap<>();
        String members = selectedServer.getMembers();
        System.out.println(members);
        for(String member : members.split(",")){
            if(!member.equals("")&&!member.equals("-1")){
                nametoid.put(Main.request.getInformationByID(member).getUsername(),member);
            }
        }
        ((ServerSettings)loader.getController()).setUsernameToID(nametoid);
        stage.getIcons().add(new Image("file:discord.png"));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.centerOnScreen();
        stage.setAlwaysOnTop(true);
        stage.show();
        ((ServerSettings)loader.getController()).setAccess(access);
        ((ServerSettings)loader.getController()).start();
    }
    void loadServer(String serverID) throws IOException, InterruptedException {
        serverSettings.setVisible(true);
        chatsVB.getChildren().clear();
        ArrayList<DiscordChat> chats = Main.request.getServerChats(serverID);
        for(DiscordChat chat : chats)
            chatsVB.getChildren().add(getAChatPane(chat));
        if(chats.size()!=0)setChatMessages(chats.get(0).getId());
        else {
            chatArea.getChildren().clear();
        }
    }
    @FXML
    void createServer(){
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddServer.fxml"));
        Scene scene = null;
        try {
            Parent root = loader.load();
            scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setResizable(false);
        stage.setTitle("AddServer");
        ((AddServer)loader.getController()).setMystage(stage);
        ((AddServer)loader.getController()).setManager(manager);
        ((AddServer)loader.getController()).setMyID(me.getId()+"");

        stage.getIcons().add(new Image("file:discord.png"));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.centerOnScreen();
        stage.setAlwaysOnTop(true);

        stage.show();

    }
    private String selectedChatID = "-1";
    public void addServer(DiscordServer server){
        ImageView imm = noname1(String.valueOf(server.getName().charAt(0)), String.valueOf(server.getName().charAt(server.getName().length()-1)));
        Label roo = new Label();
        roo.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                roo.setScaleX(1.1);
                roo.setScaleY(1.1);
            }
        });
        roo.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        roo.setGraphic(imm);
        Tooltip t = new Tooltip(server.getName());
        t.setStyle("-fx-font-family: 'Segoe UI';-fx-font-size: 16px;");
        roo.setTooltip(t);
        roo.setPadding(new Insets(12, 0, 12, 0));
        chatBoxes.getChildren().add(roo);
        roo.setCursor(Cursor.HAND);
        roo.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    selectedServerName.setText(server.getName());
                    selectedServer = server;
                    loadServer(server.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    DiscordServer selectedServer;
    public void loadServers() {
        ArrayList<DiscordServer> joinedServers = Main.request.getServers();
        for (DiscordServer server : joinedServers) {
            addServer(server);
        }
        if (joinedServers.size() != 0) {
            try {
                loadServer(joinedServers.get(0).getId());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    Button sendMessageButton;
    @FXML
    Button sendFileButton;
    @FXML
    Pane typePane;
    @FXML
    Label PinLABEL;
    @FXML
    VBox pendingVBox;

    @FXML
    Label usernamepending;

    @FXML
    Label pendinglabel;
    @FXML
    Label rejectLabel;
    @FXML
    Label acceptLabel;


    Pane PendingPane(String username){
        Pane result = new Pane();

        ImageView profilePic = new ImageView();
        try {
            Main.request.downloadProfileImage(username, new DownloadFininshedEvent() {
                @Override
                public void fileDownloadFinishedHandler(String filename) {
                    profilePic.setImage(generateProfilePicture(new Image("file:"+filename),33,33));
                }
            },manager);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainForm mm = this;
        profilePic.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                showUserInformation(username,mm);
            }
        });
        profilePic.setCursor(Cursor.HAND);
        profilePic.setLayoutX(22);
        profilePic.setLayoutY(4);
        profilePic.setFitHeight(33);
        profilePic.setFitWidth(33);
        profilePic.setPreserveRatio(false);

        Label usersName = new Label();
        usersName.setText(username);
        usersName.setLayoutX(usernamepending.getLayoutX());
        usersName.setLayoutY(usernamepending.getLayoutY());
        usersName.setFont(usernamepending.getFont());
        usersName.setTextFill(usernamepending.getTextFill());

        Label pending = new Label();
        pending.setText("Pending...");
        pending.setFont(pendinglabel.getFont());
        pending.setLayoutX(pendinglabel.getLayoutX());
        pending.setLayoutY(pendinglabel.getLayoutY());
        pending.setTextFill(pendinglabel.getTextFill());

        Label reject = new Label();
        reject.setText("❌");
        reject.setFont(rejectLabel.getFont());
        reject.setTextFill(rejectLabel.getTextFill());
        reject.setLayoutX(rejectLabel.getLayoutX());
        reject.setLayoutY(rejectLabel.getLayoutY());
        reject.setPrefWidth(54);
        reject.setPrefHeight(29);
        reject.setAlignment(Pos.CENTER);
        reject.getStylesheets().addAll(this.getClass().getResource("checkBox.css").toExternalForm());
        reject.getStyleClass().set(0,"rejectButton");
        reject.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton().equals(MouseButton.PRIMARY)){
                    Main.request.approveFriendShipRequest(username,false);
                    pendingVBox.getChildren().remove(result);
                }
            }
        });

        Label accept = new Label();
        accept.setText("✓");
        accept.setFont(acceptLabel.getFont());
        accept.setTextFill(acceptLabel.getTextFill());
        accept.setLayoutX(acceptLabel.getLayoutX());
        accept.setLayoutY(acceptLabel.getLayoutY());
        accept.setPrefHeight(29);
        accept.setPrefWidth(54);
        accept.setAlignment(Pos.CENTER);
        accept.getStylesheets().addAll(this.getClass().getResource("checkBox.css").toExternalForm());
        accept.getStyleClass().set(0,"friendShipButton");
        accept.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton().equals(MouseButton.PRIMARY)){
                    Main.request.approveFriendShipRequest(username,true);
                    pendingVBox.getChildren().remove(result);
                }
            }
        });

        result.setStyle("-fx-border-color:  gray;-fx-border-radius: 30");
        result.getChildren().addAll(profilePic,usersName,pending,reject,accept);
        result.setPrefWidth(1050);
        result.setPrefHeight(42);
        return result;
    }
    @FXML
    Pane pendingScrollPane;
    @FXML
    TextField friendShipUsername;
    @FXML
    Label callUser;
    @FXML
    void loadFriendRequests() throws IOException, InterruptedException {
        sideprofsVB.getChildren().clear();
        hideChatsArea();

        callUser.setVisible(true);
        selectedServer = null;
        selectedChatID = null;
        chatsVB.getChildren().clear();
        ArrayList<DiscordChat> chats = Main.request.getListOfDirectChats(me.getUsername());
        for(DiscordChat chat : chats) {
            Pane pp = getADMChatPane(chat);
            chatsVB.getChildren().add(pp);
        }
        ArrayList<String> friendShipRequests = Main.request.getFriendshipRequests();
        System.out.println(friendShipRequests);
        pendingVBox.getChildren().clear();

        for(String username : friendShipRequests){
            Pane ok = PendingPane(username);
            pendingVBox.getChildren().add(ok);
            pendingVBox.setMargin(ok,new Insets(3,1,3,1));
        }
        if(pendingVBox.getChildren().size()==0){
            pendingScrollPane.setVisible(false);
        }
        else
            pendingScrollPane.setVisible(true);


    }
    @FXML
    Pane mainmainPane;
    private void hideChatsArea(){
        selectedServerName.setText("Direct Chats");
        chatsVB.getChildren().clear();
        mainPane.setVisible(false);
        PinLABEL.setVisible(false);
        emojipane2.setVisible(false);
        typedText.setVisible(false);
        sendFileButton.setVisible(false);
        sendMessageButton.setVisible(false);
        typePane.setVisible(false);
        mainmainPane.setVisible(true);
        serverSettings.setVisible(false);
        callUser.setVisible(false);

    }
    private void showChatsArea(){
        callUser.setVisible(false);

        mainPane.setVisible(true);
        emojipane2.setVisible(true);
        typedText.setVisible(true);
        sendFileButton.setVisible(true);
        sendMessageButton.setVisible(true);
        typePane.setVisible(true);
        PinLABEL.setVisible(true);
        mainmainPane.setVisible(false);
        screenPANE.setVisible(false);

    }


    HashMap<String , ArrayList<ImageView>> nameToImageView = new HashMap<>();
    public static String getLast(String filename){
        return filename.split("\\\\")[filename.split("\\\\").length-1];
    }
    private boolean loaded = false;
    synchronized boolean getloaded(){
        return loaded;
    }
    synchronized void setloaded(boolean loaded){
        this.loaded = loaded;
    }

    private Pane OthersImagePane(FileMessage message, ServerAccess myAccess, DiscordChat chat) throws IOException {
        ArrayList<Reaction> reactions = Main.request.getReactions(chat.getId(),message.getMessageID());
        int likesCounts = (int) reactions.stream().filter(e -> e.getReaction().equals("like")).count();
        int dislikesCounts = (int) reactions.stream().filter(e -> e.getReaction().equals("dislike")).count();
        int laughsCounts = (int) reactions.stream().filter(e -> e.getReaction().equals("laugh")).count();
        final int[][] liked = {{(int) reactions.stream().filter(e -> e.getFrom_id().equals(me.getId() + "") && e.getReaction().equals("like")).count()}};
        final int[] disliked = {(int) reactions.stream().filter(e -> e.getFrom_id().equals(me.getId() + "") && e.getReaction().equals("dislike")).count()};
        final int[] laughed = {(int) reactions.stream().filter(e -> e.getFrom_id().equals(me.getId() + "") && e.getReaction().equals("laugh")).count()};
        ArrayList<String> whoLiked = new ArrayList<>();
        reactions.forEach(e->{if(e.getReaction().equals("like")){whoLiked.add(e.getFrom_id());}});
        ArrayList<String> whoDisLiked = new ArrayList<>();
        reactions.forEach(e->{if(e.getReaction().equals("dislike")){whoDisLiked.add(e.getFrom_id());}});
        ArrayList<String> whoLaughed = new ArrayList<>();
        reactions.forEach(e->{if(e.getReaction().equals("laugh")){whoLaughed.add(e.getFrom_id());}});
        Tooltip likedT = new Tooltip();
        String likedText = "";
        for(String userid : whoLiked){
            likedText += Main.request.getInformationByID(userid).getUsername()+" ,";
        }
        if(likedText.length()>=2) {
            likedText = " "+likedText.substring(0, likedText.length() - 2);
            likedText += " liked this message";
        }
        else{
            likedText = "Nobody liked this message";
        }
        likedT.setText(likedText);
        Tooltip dislikedT = new Tooltip();
        String dislikedText = "";
        for(String userid : whoDisLiked){
            dislikedText += " "+Main.request.getInformationByID(userid).getUsername()+" ,";
        }
        if(dislikedText.length()>=2) {
            dislikedText = dislikedText.substring(0, dislikedText.length() - 2);
            dislikedText += " disliked this message";
        }
        else{
            dislikedText = "Nobody Disliked this message";
        }
        dislikedT.setText(dislikedText);
        Tooltip laughedT = new Tooltip();
        String laughedText = "";
        for(String userid : whoLaughed){
            laughedText += " "+Main.request.getInformationByID(userid).getUsername()+" ,";
        }
        if(laughedText.length()>=2) {
            laughedText = laughedText.substring(0, laughedText.length() - 2);
            laughedText += " laughed at this message";
        }
        else {
            laughedText = "Nobody laughed at this message";
        }
        laughedT.setText(laughedText);


        FileMessage messageObject = message;
        String fromUsername = message.getFromUsername();
        String date = message.getDate();
        Pane result = new Pane();
        Pane messageArea = new Pane();
        Pane profileArea = new Pane();
        result.getChildren().add(profileArea);
        result.getChildren().add(messageArea);
        ImageView profilePic = new ImageView();
        ImageWaitingEvent myEvent = new ImageWaitingEvent() {
            @Override
            public void Finished(String filename, String username, Image img) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        profilePic.setImage(img);
                    }
                });
            }
        };
        ImagePool.addListener(fromUsername,myEvent);
        Label userNameLabels = new Label();
        userNameLabels.setText(fromUsername);
        ImageView image = new ImageView();


        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("        Pin        ");
        MenuItem item2 = new MenuItem("      Reply      ");


        item2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(repliyingToMessageID==-1) {
                    repliyingToMessageID = Integer.parseInt(messageObject.getMessageID());
                    //messageArea.setStyle("-fx-background-color: #3e3e3e;-fx-background-radius:  0 20 20 20;");
                    userNameLabels.setTextFill(Color.LIME);
                    lastReplied = userNameLabels;
                }else{
                    if(lastReplied == userNameLabels){
                        repliyingToMessageID = -1;
                        lastReplied.setTextFill(Color.WHITE);
                        lastReplied = null;
                    }
                    else {
                        lastReplied.setTextFill(Color.WHITE);
                        userNameLabels.setTextFill(Color.LIME);
                        lastReplied = userNameLabels;
                        repliyingToMessageID = Integer.parseInt(messageObject.getMessageID());
                    }
                }
            }
        });
        //contextMenu.setStyle("-fx-background-color : black;  -fx-text-fill: white;");
        item1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //todo : do a pin request to server and set the message as pinned
                Main.request.pinMessage(messageObject.getChat_id(),messageObject.getMessageID());
                for(int i = 0; i<messagesTMP.size();i++){
                    if(messagesTMP.get(i).getMessageID().equals(messageObject.getMessageID())){
                        pinnedMessageIndex = i;
                    }
                }
            }
        });
        if(chat!=null) {
            if (myAccess==null || myAccess.isAbilityToPin()) {
                contextMenu.getItems().add(item1);
            }
        }
        contextMenu.getItems().add(item2);
        //contextMenu.getItems().addAll(item1,item2);
        image.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                contextMenu.show(image,event.getScreenX(),event.getScreenY());
            }
        });

//        ImageWaitingEvent myEvent2 = new ImageWaitingEvent() {
//            @Override
//            public void Finished(String filename, String username, Image img) {
//                image.setImage(new Image("file:"+filename));
//            }
//        };
        if(!Files.exists(Paths.get("downloads\\" + getLast(message.getFilename())))) {
            FileManager.downloadFile(message.getFilename(), "downloads\\" + getLast(message.getFilename()), new DownloadFininshedEvent() {
                @Override
                public void fileDownloadFinishedHandler(String filename) {
                    image.setImage(new Image("file:" + filename));
                    manager.downloadOrUploadFinished(filename);
                    setloaded(true);
                }
            }, manager, null, false);
        }
        else{
            image.setImage(new Image("file:downloads\\"+getLast(message.getFilename())));
            setloaded(true);
        }
        image.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!event.getButton().equals(MouseButton.PRIMARY))
                    return;
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ImageViewer.fxml"));
                Scene scene = null;
                try {
                    Parent root = loader.load();
                    scene = new Scene(root);
                    stage.setScene(scene);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                stage.setResizable(false);
                stage.setTitle("ImageViewer");
                stage.getIcons().add(new Image("file:discord.png"));
                stage.initStyle(StageStyle.TRANSPARENT);
                scene.setFill(Color.TRANSPARENT);
                stage.centerOnScreen();
                stage.setMaximized(true);
                stage.setAlwaysOnTop(true);
                ((ImageViewer)loader.getController()).setImageView(image.getImage());
                stage.show();
            }
        });
        image.setCursor(Cursor.HAND);
        //        Label msgLabel = new Label();
//        msgLabel.setText(message);
//        msgLabel.setLayoutX(msgContent.getLayoutX());
//        msgLabel.setLayoutY(msgContent.getLayoutY());
//        msgLabel.setFont(msgContent.getFont());
//        msgLabel.setMaxWidth(700);
//        msgLabel.setWrapText(true);
//        msgLabel.setTextFill(msgContent.getTextFill());
//        msgLabel.setCursor(Cursor.HAND);
        messageArea.setPrefSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);
        StackPane bb = new StackPane();
        bb.setLayoutX(msgContent.getLayoutX());
        bb.setLayoutY(msgContent.getLayoutY());
        bb.getChildren().add(image);
        image.setLayoutY(0);
        image.setLayoutX(0);
        image.setPreserveRatio(true);
        image.setFitWidth(400);
        bb.setPadding(msgContent.getPadding());
        messageArea.getChildren().add(bb);
        Label dataTimeLabel = new Label();
        dataTimeLabel.setText(date);
        dataTimeLabel.setFont(userNameLabel.getFont());
        dataTimeLabel.setTextFill(Color.GRAY);
        dataTimeLabel.setLayoutY(33);
        dataTimeLabel.setLayoutX(49);
        profileArea.getChildren().add(profilePic);
        profileArea.getChildren().add(userNameLabels);
        profileArea.getChildren().add(dataTimeLabel);
        userNameLabels.setStyle(userNameLabel.getStyle());
        userNameLabels.setLayoutX(userNameLabel.getLayoutX()-3);
        userNameLabels.setLayoutY(userNameLabel.getLayoutY()+5);
        userNameLabels.setFont(userNameLabel.getFont());
        userNameLabels.setTextFill(Color.WHITE);
        if(fromUsername.equals(me.getUsername())){
            userNameLabels.setText("You");
            userNameLabels.setTextFill(Color.RED);
        }else{
            userNameLabels.setTextFill(Color.CYAN);
        }
        profilePic.setLayoutX(6);
        profilePic.setLayoutY(10);
        profileArea.setLayoutX(groupOthersProfilePane.getLayoutX());
        profileArea.setLayoutY(groupOthersProfilePane.getLayoutY());
        profileArea.setStyle(groupOthersProfilePane.getStyle());
        messageArea.setLayoutX(msgAreaOthersPane.getLayoutX()-5);
        messageArea.setLayoutY(msgAreaOthersPane.getLayoutY());
        messageArea.setStyle(msgAreaOthersPane.getStyle());
        profileArea.setPrefWidth(groupOthersProfilePane.getPrefWidth());
        profileArea.setPrefHeight(groupOthersProfilePane.getPrefHeight());
        profilePic.setCursor(Cursor.HAND);
        MainForm mm = this;
        profilePic.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                showUserInformation(fromUsername,mm);
            }
        });
        Pane likePane = new Pane();
        likePane.setStyle("-fx-border-color: gray; -fx-border-radius:10");
        likePane.setLayoutY(1);
        likePane.setLayoutX(206);
        likePane.setPrefWidth(50);
        likePane.setPrefHeight(17);
        Label likesCount = new Label();
        Label likeEmoji = new Label();
        likeEmoji.setTextFill(Color.WHITE);
        likeEmoji.setFont(likeEmojis.getFont());
        likeEmoji.setLayoutY(likeEmojis.getLayoutY());
        likeEmoji.setLayoutX(likeEmojis.getLayoutX());
        likePane.getChildren().addAll(likeEmoji , likesCount);
        likesCount.setFont(countT.getFont());
        likesCount.setLayoutX(countT.getLayoutX());
        likesCount.setLayoutY(countT.getLayoutY());
        likesCount.setText("0");
        likesCount.setTextFill(countT.getTextFill());
        likeEmoji.setText(likeEmojis.getText());
        profileArea.getChildren().add(likePane);
        likesCount.setText(likesCounts+"");
        likeEmoji.setCursor(Cursor.HAND);
        likeEmoji.setTooltip(likedT);

        Pane dislikePane = new Pane();
        dislikePane.setStyle("-fx-border-color: gray; -fx-border-radius:10");
        dislikePane.setLayoutY(19);
        dislikePane.setLayoutX(206);
        dislikePane.setPrefWidth(50);
        dislikePane.setPrefHeight(17);
        Label dislikesCount = new Label();
        Label dislikeEmoji = new Label();
        dislikeEmoji.setTextFill(Color.WHITE);
        dislikeEmoji.setFont(likeEmojis.getFont());
        dislikeEmoji.setLayoutY(likeEmojis.getLayoutY());
        dislikeEmoji.setLayoutX(likeEmojis.getLayoutX());
        dislikePane.getChildren().addAll(dislikeEmoji , dislikesCount);
        dislikesCount.setFont(countT.getFont());
        dislikesCount.setLayoutX(countT.getLayoutX());
        dislikesCount.setLayoutY(countT.getLayoutY());
        dislikesCount.setText("0");
        dislikesCount.setTextFill(countT.getTextFill());
        dislikeEmoji.setText("\uD83D\uDC4E");
        profileArea.getChildren().add(dislikePane);
        dislikesCount.setText(dislikesCounts+"");
        dislikeEmoji.setCursor(Cursor.HAND);
        dislikeEmoji.setTooltip(dislikedT);

        Pane laughPane = new Pane();
        laughPane.setStyle("-fx-border-color: gray; -fx-border-radius:10");
        laughPane.setLayoutY(37);
        laughPane.setLayoutX(206);
        laughPane.setPrefWidth(50);
        laughPane.setPrefHeight(17);
        Label laughCount = new Label();
        Label laughEmoji = new Label();
        laughEmoji.setTextFill(Color.WHITE);
        laughEmoji.setFont(likeEmojis.getFont());
        laughEmoji.setLayoutY(likeEmojis.getLayoutY());
        laughEmoji.setLayoutX(likeEmojis.getLayoutX());
        laughPane.getChildren().addAll(laughEmoji , laughCount);
        laughCount.setFont(countT.getFont());
        laughCount.setLayoutX(countT.getLayoutX());
        laughCount.setLayoutY(countT.getLayoutY());
        laughCount.setText("0");
        laughCount.setTextFill(countT.getTextFill());
        laughEmoji.setText("\uD83D\uDE02");
        profileArea.getChildren().add(laughPane);
        laughCount.setText(laughsCounts+"");
        laughEmoji.setCursor(Cursor.HAND);
        laughEmoji.setTooltip(laughedT);
        likeEmoji.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"like");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"dislike");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"laugh");
                if(liked[0][0] ==0) {
                    likesCount.setText((Integer.parseInt(likesCount.getText()) + 1) + "");
                    whoLiked.add(me.getUsername());
                    Main.request.react(chat.getId(),messageObject.getMessageID(),"like");
                    liked[0][0]++;
                }
                else{
                    Main.request.unreact(chat.getId(),messageObject.getMessageID(),"like");
                    likesCount.setText((Integer.parseInt(likesCount.getText()) -1) + "");
                    whoLiked.remove(me.getUsername());
                    liked[0][0]--;
                }
                if(disliked[0] ==1){
                    disliked[0]--;
                    whoDisLiked.remove(me.getUsername());
                    dislikesCount.setText((Integer.parseInt(dislikesCount.getText()) -1) + "");
                }
                if(laughed[0] == 1){
                    whoLaughed.remove(me.getUsername());
                    laughCount.setText((Integer.parseInt(laughCount.getText()) -1) + "");
                    laughed[0]--;
                }

            }
        });
        dislikeEmoji.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"like");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"dislike");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"laugh");

                if(disliked[0]==0) {
                    dislikesCount.setText((Integer.parseInt(dislikesCount.getText()) + 1) + "");
                    Main.request.react(chat.getId(),messageObject.getMessageID(),"dislike");
                    disliked[0]++;
                    whoDisLiked.add(me.getUsername());
                }
                else{
                    Main.request.unreact(chat.getId(),messageObject.getMessageID(),"dislike");
                    dislikesCount.setText((Integer.parseInt(dislikesCount.getText()) -1) + "");
                    disliked[0]--;
                    whoDisLiked.remove(me.getUsername());
                }
                if(liked[0][0] ==1){
                    liked[0][0]--;
                    likesCount.setText((Integer.parseInt(likesCount.getText()) -1) + "");
                    whoLiked.remove(me.getUsername());
                }
                if(laughed[0] == 1){
                    whoLaughed.remove(me.getUsername());
                    laughCount.setText((Integer.parseInt(laughCount.getText()) -1) + "");
                    laughed[0]--;
                }


            }
        });
        laughEmoji.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"like");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"dislike");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"laugh");
                if(laughed[0]==0) {
                    laughCount.setText((Integer.parseInt(laughCount.getText())+1)+"");
                    Main.request.react(chat.getId(),messageObject.getMessageID(),"laugh");
                    laughed[0]++;
                    whoLaughed.add(me.getUsername());
                }
                else{
                    whoLaughed.remove(me.getUsername());
                    Main.request.unreact(chat.getId(),messageObject.getMessageID(),"laugh");
                    laughCount.setText((Integer.parseInt(laughCount.getText())-1)+"");
                    laughed[0]--;
                }
                if(liked[0][0] ==1){
                    liked[0][0]--;
                    whoLiked.remove(me.getUsername());
                    likesCount.setText((Integer.parseInt(likesCount.getText()) -1) + "");
                }
                if(disliked[0] == 1){
                    whoDisLiked.remove(me.getUsername());
                    dislikesCount.setText((Integer.parseInt(dislikesCount.getText()) -1) + "");
                    disliked[0]--;
                }
            }
        });
        profilePic.setPreserveRatio(false);
        profilePic.setSmooth(true);
        profilePic.setFitWidth(38);
        profilePic.setFitHeight(38);
        bb.setPadding(msgContent.getPadding());
        result.setPrefHeight(groupOthersPane.getPrefHeight());
        result.setPrefHeight(Region.USE_COMPUTED_SIZE);
        result.setPadding(msgContent.getPadding());
        while(!getloaded());
        setloaded(false);
        return result;
    }
    Pane OthersPane(String message, String fromUsername, String date, TextMessage messageObject, ServerAccess myAccess, DiscordChat chat) throws IOException {
        ArrayList<Reaction> reactions = Main.request.getReactions(chat.getId(),messageObject.getMessageID());
        int likesCounts = (int) reactions.stream().filter(e -> e.getReaction().equals("like")).count();
        int dislikesCounts = (int) reactions.stream().filter(e -> e.getReaction().equals("dislike")).count();
        int laughsCounts = (int) reactions.stream().filter(e -> e.getReaction().equals("laugh")).count();

        final int[][] liked = {{(int) reactions.stream().filter(e -> e.getFrom_id().equals(me.getId() + "") && e.getReaction().equals("like")).count()}};
        final int[] disliked = {(int) reactions.stream().filter(e -> e.getFrom_id().equals(me.getId() + "") && e.getReaction().equals("dislike")).count()};
        final int[] laughed = {(int) reactions.stream().filter(e -> e.getFrom_id().equals(me.getId() + "") && e.getReaction().equals("laugh")).count()};

        ArrayList<String> whoLiked = new ArrayList<>();
        reactions.forEach(e->{if(e.getReaction().equals("like")){whoLiked.add(e.getFrom_id());}});
        ArrayList<String> whoDisLiked = new ArrayList<>();
        reactions.forEach(e->{if(e.getReaction().equals("dislike")){whoDisLiked.add(e.getFrom_id());}});
        ArrayList<String> whoLaughed = new ArrayList<>();
        reactions.forEach(e->{if(e.getReaction().equals("laugh")){whoLaughed.add(e.getFrom_id());}});

        Tooltip likedT = new Tooltip();
        String likedText = "";
        for(String userid : whoLiked){
            likedText += Main.request.getInformationByID(userid).getUsername()+" ,";
        }
        if(likedText.length()>=2) {
            likedText = " "+likedText.substring(0, likedText.length() - 2);
            likedText += " liked this message";
        }
        else{
            likedText = "Nobody liked this message";
        }
        likedT.setText(likedText);


        Tooltip dislikedT = new Tooltip();
        String dislikedText = "";
        for(String userid : whoDisLiked){
            dislikedText += " "+Main.request.getInformationByID(userid).getUsername()+" ,";
        }
        if(dislikedText.length()>=2) {
            dislikedText = dislikedText.substring(0, dislikedText.length() - 2);
            dislikedText += " disliked this message";
        }
        else{
            dislikedText = "Nobody Disliked this message";
        }
        dislikedT.setText(dislikedText);

        Tooltip laughedT = new Tooltip();
        String laughedText = "";
        for(String userid : whoLaughed){
            laughedText += " "+Main.request.getInformationByID(userid).getUsername()+" ,";
        }
        if(laughedText.length()>=2) {
            laughedText = laughedText.substring(0, laughedText.length() - 2);
            laughedText += " laughed at this message";
        }
        else {
            laughedText = "Nobody laughed at this message";
        }
        laughedT.setText(laughedText);






        message = new String(message.getBytes(),Charset.forName("UTF-8"));
        Pattern myPattern = Pattern.compile(":[a-z]{1,}:");
        Matcher m = myPattern.matcher(message);
        while(m.find()){
            message = message.replace(m.group(0),Emojies.getEmoji(m.group(0).replace(":","")));
        }
        Pane result = new Pane();
        Pane messageArea = new Pane();

        Pane profileArea = new Pane();
        result.getChildren().add(profileArea);
        result.getChildren().add(messageArea);
        ImageView profilePic = new ImageView();
        ImageWaitingEvent myEvent = new ImageWaitingEvent() {
            @Override
            public void Finished(String filename, String username, Image img) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        profilePic.setImage(img);
                    }
                });
            }
        };

        Pane likePane = new Pane();
        likePane.setStyle("-fx-border-color: gray; -fx-border-radius:10");
        likePane.setLayoutY(1);
        likePane.setLayoutX(206);
        likePane.setPrefWidth(50);
        likePane.setPrefHeight(17);
        Label likesCount = new Label();
        Label likeEmoji = new Label();
        likeEmoji.setTextFill(Color.WHITE);
        likeEmoji.setFont(likeEmojis.getFont());
        likeEmoji.setLayoutY(likeEmojis.getLayoutY());
        likeEmoji.setLayoutX(likeEmojis.getLayoutX());
        likePane.getChildren().addAll(likeEmoji , likesCount);
        likesCount.setFont(countT.getFont());
        likesCount.setLayoutX(countT.getLayoutX());
        likesCount.setLayoutY(countT.getLayoutY());
        likesCount.setText("0");
        likesCount.setTextFill(countT.getTextFill());
        likeEmoji.setText(likeEmojis.getText());
        profileArea.getChildren().add(likePane);
        likesCount.setText(likesCounts+"");
        likeEmoji.setCursor(Cursor.HAND);
        likeEmoji.setTooltip(likedT);

        Pane dislikePane = new Pane();
        dislikePane.setStyle("-fx-border-color: gray; -fx-border-radius:10");
        dislikePane.setLayoutY(19);
        dislikePane.setLayoutX(206);
        dislikePane.setPrefWidth(50);
        dislikePane.setPrefHeight(17);
        Label dislikesCount = new Label();
        Label dislikeEmoji = new Label();
        dislikeEmoji.setTextFill(Color.WHITE);
        dislikeEmoji.setFont(likeEmojis.getFont());
        dislikeEmoji.setLayoutY(likeEmojis.getLayoutY());
        dislikeEmoji.setLayoutX(likeEmojis.getLayoutX());
        dislikePane.getChildren().addAll(dislikeEmoji , dislikesCount);
        dislikesCount.setFont(countT.getFont());
        dislikesCount.setLayoutX(countT.getLayoutX());
        dislikesCount.setLayoutY(countT.getLayoutY());
        dislikesCount.setText("0");
        dislikesCount.setTextFill(countT.getTextFill());
        dislikeEmoji.setText("\uD83D\uDC4E");
        profileArea.getChildren().add(dislikePane);
        dislikesCount.setText(dislikesCounts+"");
        dislikeEmoji.setCursor(Cursor.HAND);
        dislikeEmoji.setTooltip(dislikedT);

        Pane laughPane = new Pane();
        laughPane.setStyle("-fx-border-color: gray; -fx-border-radius:10");
        laughPane.setLayoutY(37);
        laughPane.setLayoutX(206);
        laughPane.setPrefWidth(50);
        laughPane.setPrefHeight(17);
        Label laughCount = new Label();
        Label laughEmoji = new Label();
        laughEmoji.setTextFill(Color.WHITE);
        laughEmoji.setFont(likeEmojis.getFont());
        laughEmoji.setLayoutY(likeEmojis.getLayoutY());
        laughEmoji.setLayoutX(likeEmojis.getLayoutX());
        laughPane.getChildren().addAll(laughEmoji , laughCount);
        laughCount.setFont(countT.getFont());
        laughCount.setLayoutX(countT.getLayoutX());
        laughCount.setLayoutY(countT.getLayoutY());
        laughCount.setText("0");
        laughCount.setTextFill(countT.getTextFill());
        laughEmoji.setText("\uD83D\uDE02");
        profileArea.getChildren().add(laughPane);
        laughCount.setText(laughsCounts+"");
        laughEmoji.setCursor(Cursor.HAND);
        laughEmoji.setTooltip(laughedT);
        likeEmoji.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"like");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"dislike");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"laugh");
                if(liked[0][0] ==0) {
                    likesCount.setText((Integer.parseInt(likesCount.getText()) + 1) + "");
                    whoLiked.add(me.getUsername());
                    Main.request.react(chat.getId(),messageObject.getMessageID(),"like");
                    liked[0][0]++;
                }
                else{
                    Main.request.unreact(chat.getId(),messageObject.getMessageID(),"like");
                    likesCount.setText((Integer.parseInt(likesCount.getText()) -1) + "");
                    whoLiked.remove(me.getUsername());
                    liked[0][0]--;
                }
                if(disliked[0] ==1){
                    disliked[0]--;
                    whoDisLiked.remove(me.getUsername());
                    dislikesCount.setText((Integer.parseInt(dislikesCount.getText()) -1) + "");
                }
                if(laughed[0] == 1){
                    whoLaughed.remove(me.getUsername());
                    laughCount.setText((Integer.parseInt(laughCount.getText()) -1) + "");
                    laughed[0]--;
                }

            }
        });
        dislikeEmoji.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"like");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"dislike");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"laugh");

                if(disliked[0]==0) {
                    dislikesCount.setText((Integer.parseInt(dislikesCount.getText()) + 1) + "");
                    Main.request.react(chat.getId(),messageObject.getMessageID(),"dislike");
                    disliked[0]++;
                    whoDisLiked.add(me.getUsername());
                }
                else{
                    Main.request.unreact(chat.getId(),messageObject.getMessageID(),"dislike");
                    dislikesCount.setText((Integer.parseInt(dislikesCount.getText()) -1) + "");
                    disliked[0]--;
                    whoDisLiked.remove(me.getUsername());
                }
                if(liked[0][0] ==1){
                    liked[0][0]--;
                    likesCount.setText((Integer.parseInt(likesCount.getText()) -1) + "");
                    whoLiked.remove(me.getUsername());
                }
                if(laughed[0] == 1){
                    whoLaughed.remove(me.getUsername());
                    laughCount.setText((Integer.parseInt(laughCount.getText()) -1) + "");
                    laughed[0]--;
                }


            }
        });
        laughEmoji.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"like");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"dislike");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"laugh");
                if(laughed[0]==0) {
                    laughCount.setText((Integer.parseInt(laughCount.getText())+1)+"");
                    Main.request.react(chat.getId(),messageObject.getMessageID(),"laugh");
                    laughed[0]++;
                    whoLaughed.add(me.getUsername());
                }
                else{
                    whoLaughed.remove(me.getUsername());
                    Main.request.unreact(chat.getId(),messageObject.getMessageID(),"laugh");
                    laughCount.setText((Integer.parseInt(laughCount.getText())-1)+"");
                    laughed[0]--;
                }
                if(liked[0][0] ==1){
                    liked[0][0]--;
                    whoLiked.remove(me.getUsername());
                    likesCount.setText((Integer.parseInt(likesCount.getText()) -1) + "");
                }
                if(disliked[0] == 1){
                    whoDisLiked.remove(me.getUsername());
                    dislikesCount.setText((Integer.parseInt(dislikesCount.getText()) -1) + "");
                    disliked[0]--;
                }
            }
        });
        Label replyToLabel = new Label();
        replyToLabel.setText(replyLabel.getText());
        replyToLabel.setFont(replyLabel.getFont());
        replyToLabel.setTextFill(replyLabel.getTextFill());
        replyToLabel.setLayoutX(replyLabel.getLayoutX());
        replyToLabel.setLayoutY(replyLabel.getLayoutY());
        if(!messageObject.getReplyID().isEmpty()){
            profileArea.getChildren().add(replyToLabel);
            replyToLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    gotoMessageByID(messageObject.getReplyID());
                }
            });
        }
        ImagePool.addListener(fromUsername,myEvent);
        Label userNameLabels = new Label();
        userNameLabels.setText(fromUsername);
        Label msgLabel = new Label();
        msgLabel.setText(message);
        msgLabel.setLayoutX(msgContent.getLayoutX());
        msgLabel.setLayoutY(msgContent.getLayoutY());
        msgLabel.setFont(msgContent.getFont());
        msgLabel.setMaxWidth(700);
        msgLabel.setWrapText(true);
        msgLabel.setTextFill(msgContent.getTextFill());
        msgLabel.setCursor(Cursor.HAND);
        String finalMessage = message;
        msgLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(finalMessage),null);
            }
        });
        messageArea.setPrefSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);
        HBox bb = new HBox();
        bb.setLayoutX(msgContent.getLayoutX());
        bb.setLayoutY(msgContent.getLayoutY());
        bb.getChildren().add(msgLabel);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("        Pin        ");
        MenuItem item2 = new MenuItem("      Reply      ");


        item2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(repliyingToMessageID==-1) {
                    repliyingToMessageID = Integer.parseInt(messageObject.getMessageID());
                    //messageArea.setStyle("-fx-background-color: #3e3e3e;-fx-background-radius:  0 20 20 20;");
                    msgLabel.setTextFill(Color.LIME);
                    lastReplied = msgLabel;
                }else{
                    if(lastReplied == msgLabel){
                        repliyingToMessageID = -1;
                        lastReplied.setTextFill(Color.WHITE);
                        lastReplied = null;
                    }
                    else {
                        lastReplied.setTextFill(Color.WHITE);
                        msgLabel.setTextFill(Color.LIME);
                        lastReplied = msgLabel;
                        repliyingToMessageID = Integer.parseInt(messageObject.getMessageID());
                    }
                }
            }
        });
        //contextMenu.setStyle("-fx-background-color : black;  -fx-text-fill: white;");
        item1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //todo : do a pin request to server and set the message as pinned
                Main.request.pinMessage(messageObject.getChat_id(),messageObject.getMessageID());
                for(int i = 0; i<messagesTMP.size();i++){
                    if(messagesTMP.get(i).getMessageID().equals(messageObject.getMessageID())){
                        pinnedMessageIndex = i;
                    }
                }
            }
        });
        if(chat!=null) {
            if (myAccess==null || myAccess.isAbilityToPin()) {
                contextMenu.getItems().add(item1);
            }
        }
        contextMenu.getItems().add(item2);
        msgLabel.setContextMenu(contextMenu);

        msgLabel.setLayoutY(0);
        msgLabel.setLayoutX(0);
        messageArea.getChildren().add(bb);
        Label dataTimeLabel = new Label();
        dataTimeLabel.setText(date);
        dataTimeLabel.setFont(userNameLabel.getFont());
        dataTimeLabel.setTextFill(Color.GRAY);
        dataTimeLabel.setLayoutY(33);
        dataTimeLabel.setLayoutX(49);
        profileArea.getChildren().add(profilePic);
        profileArea.getChildren().add(userNameLabels);
        profileArea.getChildren().add(dataTimeLabel);
        userNameLabels.setStyle(userNameLabel.getStyle());
        userNameLabels.setLayoutX(userNameLabel.getLayoutX()-3);
        userNameLabels.setLayoutY(userNameLabel.getLayoutY()+5);
        userNameLabels.setFont(userNameLabel.getFont());
        userNameLabels.setTextFill(Color.WHITE);
        if(fromUsername.equals(me.getUsername())){
            userNameLabels.setText("You");
            userNameLabels.setTextFill(Color.RED);
        }else{
            userNameLabels.setTextFill(Color.CYAN);
        }
        profilePic.setLayoutX(6);
        profilePic.setLayoutY(10);
        profileArea.setLayoutX(groupOthersProfilePane.getLayoutX());
        profileArea.setLayoutY(groupOthersProfilePane.getLayoutY());
        profileArea.setStyle(groupOthersProfilePane.getStyle());
        messageArea.setLayoutX(msgAreaOthersPane.getLayoutX()-5);
        messageArea.setLayoutY(msgAreaOthersPane.getLayoutY());
        messageArea.setStyle(msgAreaOthersPane.getStyle());
        profileArea.setPrefWidth(groupOthersProfilePane.getPrefWidth());
        profileArea.setPrefHeight(groupOthersProfilePane.getPrefHeight());
        profilePic.setCursor(Cursor.HAND);
        MainForm mm = this;
        profilePic.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                showUserInformation(fromUsername,mm);
            }
        });
        profilePic.setPreserveRatio(false);
        profilePic.setSmooth(true);
        profilePic.setFitWidth(38);
        profilePic.setFitHeight(38);
        msgLabel.setPadding(msgContent.getPadding());
        result.setPrefHeight(groupOthersPane.getPrefHeight());
        result.setPrefHeight(Region.USE_COMPUTED_SIZE);
        result.setPadding(msgContent.getPadding());
        return result;
    }
    Pane OthersFilePane(String fileID, String fromUsername, String date, FileMessage messageObject, ServerAccess myAccess,DiscordChat chat){

        ArrayList<Reaction> reactions = Main.request.getReactions(chat.getId(),messageObject.getMessageID());
        int likesCounts = (int) reactions.stream().filter(e -> e.getReaction().equals("like")).count();
        int dislikesCounts = (int) reactions.stream().filter(e -> e.getReaction().equals("dislike")).count();
        int laughsCounts = (int) reactions.stream().filter(e -> e.getReaction().equals("laugh")).count();

        final int[][] liked = {{(int) reactions.stream().filter(e -> e.getFrom_id().equals(me.getId() + "") && e.getReaction().equals("like")).count()}};
        final int[] disliked = {(int) reactions.stream().filter(e -> e.getFrom_id().equals(me.getId() + "") && e.getReaction().equals("dislike")).count()};
        final int[] laughed = {(int) reactions.stream().filter(e -> e.getFrom_id().equals(me.getId() + "") && e.getReaction().equals("laugh")).count()};

        ArrayList<String> whoLiked = new ArrayList<>();
        reactions.forEach(e->{if(e.getReaction().equals("like")){whoLiked.add(e.getFrom_id());}});
        ArrayList<String> whoDisLiked = new ArrayList<>();
        reactions.forEach(e->{if(e.getReaction().equals("dislike")){whoDisLiked.add(e.getFrom_id());}});
        ArrayList<String> whoLaughed = new ArrayList<>();
        reactions.forEach(e->{if(e.getReaction().equals("laugh")){whoLaughed.add(e.getFrom_id());}});

        Tooltip likedT = new Tooltip();
        String likedText = "";
        for(String userid : whoLiked){
            likedText += Main.request.getInformationByID(userid).getUsername()+" ,";
        }
        if(likedText.length()>=2) {
            likedText = " "+likedText.substring(0, likedText.length() - 2);
            likedText += " liked this message";
        }
        else{
            likedText = "Nobody liked this message";
        }
        likedT.setText(likedText);


        Tooltip dislikedT = new Tooltip();
        String dislikedText = "";
        for(String userid : whoDisLiked){
            dislikedText += " "+Main.request.getInformationByID(userid).getUsername()+" ,";
        }
        if(dislikedText.length()>=2) {
            dislikedText = dislikedText.substring(0, dislikedText.length() - 2);
            dislikedText += " disliked this message";
        }
        else{
            dislikedText = "Nobody Disliked this message";
        }
        dislikedT.setText(dislikedText);

        Tooltip laughedT = new Tooltip();
        String laughedText = "";
        for(String userid : whoLaughed){
            laughedText += " "+Main.request.getInformationByID(userid).getUsername()+" ,";
        }
        if(laughedText.length()>=2) {
            laughedText = laughedText.substring(0, laughedText.length() - 2);
            laughedText += " laughed at this message";
        }
        else {
            laughedText = "Nobody laughed at this message";
        }
        laughedT.setText(laughedText);




        Pane result = new Pane();
        Pane messageArea = new Pane();
        Pane profileArea = new Pane();
        result.getChildren().add(profileArea);
        result.getChildren().add(messageArea);
        ImageView profilePic = new ImageView();
        ImageWaitingEvent myEvent = new ImageWaitingEvent() {
            @Override
            public void Finished(String filename, String username, Image img) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        profilePic.setImage(img);
                    }
                });
            }
        };
        ImagePool.addListener(fromUsername,myEvent);

        Label userNameLabels = new Label();
        userNameLabels.setText(fromUsername);
        Label dataTimeLabel = new Label();
        dataTimeLabel.setText(date);
        dataTimeLabel.setFont(userNameLabel.getFont());
        dataTimeLabel.setTextFill(Color.GRAY);
        dataTimeLabel.setLayoutY(33);
        dataTimeLabel.setLayoutX(49);
        profileArea.getChildren().add(dataTimeLabel);
        messageArea.setPrefSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);
        HBox bb = new HBox();
        bb.setLayoutX(msgContent.getLayoutX());
        bb.setLayoutY(msgContent.getLayoutY());
        Pane fileArea = new Pane();
        fileArea.setPrefWidth(fileMessagePane.getPrefWidth());
        fileArea.setPrefHeight(fileMessagePane.getPrefHeight());
        fileArea.setStyle(fileMessagePane.getStyle());
        Label fileName = new Label();
        fileName.setText(getLast(fileID));
        fileName.setTextFill(fileNameLabel.getTextFill());
        fileName.setLayoutX(fileNameLabel.getLayoutX());
        fileName.setLayoutY(fileNameLabel.getLayoutY());
        fileName.setFont(fileNameLabel.getFont());
        fileName.setMaxWidth(fileNameLabel.getMaxWidth());
        Tooltip t = new Tooltip(fileName.getText());
        t.setStyle("-fx-font-family: 'Segoe UI';-fx-font-size: 16px;");
        fileName.setTooltip(t);
        Label dateL = new Label();
        dateL.setTextFill(fileDate.getTextFill());
        dateL.setText(date);
        dateL.setFont(fileDate.getFont());
        dateL.setLayoutX(fileDate.getLayoutX());
        dateL.setLayoutY(fileDate.getLayoutY());
        Arc arc = new Arc();
        arc.setRadiusX(filePercentage.getRadiusX());
        arc.setRadiusY(filePercentage.getRadiusY());
        arc.setLayoutX(filePercentage.getLayoutX());
        arc.setStartAngle(0);
        arc.setLayoutY(filePercentage.getLayoutY());
        arc.setStyle(filePercentage.getStyle());
        arc.setRotate(filePercentage.getRotate());
        arc.setLength(0);
        arc.setCenterX(0);
        arc.setCenterY(0);
        arc.setType(ArcType.ROUND);
        Circle roo = new Circle();
        roo.setStyle(rooE.getStyle());
        roo.setLayoutX(rooE.getLayoutX());
        roo.setLayoutY(rooE.getLayoutY());
        roo.setRadius(rooE.getRadius());
        Label ll = new Label();
        ll.setText("↓");
        ll.setTextFill(downloadLabel.getTextFill());
        ll.setFont(downloadLabel.getFont());
        ll.setLayoutX(downloadLabel.getLayoutX());
        ll.setLayoutY(downloadLabel.getLayoutY());
        ll.setUnderline(true);
        MainForm mm = this;
        ll.setCursor(Cursor.HAND);
        if(Files.exists(Paths.get("downloads\\" + getLast(fileID)))){
                arc.setStyle("-fx-fill: lime;-fx-stroke-width: 0;");
                ll.setFont(downloadLabel.getFont());
                ll.setStyle("");
                ll.setUnderline(false);
                ll.setLayoutX(downloadLabel.getLayoutX() - 7);
                ll.setLayoutY(downloadLabel.getLayoutY());
                ll.setTextFill(Color.LIME);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ll.setText("✓");
                        arc.setLength(360);
                    }
                });
                ll.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        try {
                            Desktop.getDesktop().open(new File("downloads\\"+getLast(fileID)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        }else {
            ll.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    try {
                        FileManager.downloadFile(fileID, "downloads\\" + getLast(fileID), mm, new FileTransferProgressEvent() {
                            @Override
                            public void updateProgress(double percent, String fileName, Arc arc) {
                                String filename = fileName;
                                double value = percent;
                                if (arc != null)
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            arc.setLength(3.6 * value);
                                            ll.setText(value + "%");
                                            if (Double.compare(value, 100.0) == 0 || Double.compare(value, 99.0) == 0) {
                                                arc.setStyle("-fx-fill: lime;-fx-stroke-width: 0;");
                                                ll.setFont(downloadLabel.getFont());
                                                ll.setStyle("");
                                                ll.setLayoutX(downloadLabel.getLayoutX() - 7);
                                                ll.setLayoutY(downloadLabel.getLayoutY());
                                                ll.setTextFill(Color.LIME);
                                                Platform.runLater(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ll.setText("✓");
                                                        arc.setLength(360);
                                                    }
                                                });
                                                ll.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                                    @Override
                                                    public void handle(MouseEvent event) {
                                                        try {
                                                            Desktop.getDesktop().open(new File(filename));
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                            }
                        }, arc, false);
                        //ll.setVisible(false);
                        ll.setText("0%");
                        ll.setUnderline(false);
                        ll.setTextFill(Color.CYAN);
                        ll.setLayoutY(ll.getLayoutY() + 17);
                        ll.setLayoutX(ll.getLayoutX() - 4);
                        ll.setStyle("-fx-font-family: 'Segoe UI';-fx-font-size: 12px");
                        ll.setOnMouseClicked(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("        Pin        ");
        MenuItem item2 = new MenuItem("      Reply      ");


        item2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(repliyingToMessageID==-1) {
                    repliyingToMessageID = Integer.parseInt(messageObject.getMessageID());
                    //messageArea.setStyle("-fx-background-color: #3e3e3e;-fx-background-radius:  0 20 20 20;");
                    fileName.setTextFill(Color.LIME);
                    lastReplied = fileName;
                }else{
                    if(lastReplied == fileName){
                        repliyingToMessageID = -1;
                        lastReplied.setTextFill(Color.WHITE);
                        lastReplied = null;
                    }
                    else {
                        lastReplied.setTextFill(Color.WHITE);
                        fileName.setTextFill(Color.LIME);
                        lastReplied = fileName;
                        repliyingToMessageID = Integer.parseInt(messageObject.getMessageID());
                    }
                }
            }
        });
        //contextMenu.setStyle("-fx-background-color : black;  -fx-text-fill: white;");
        item1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //todo : do a pin request to server and set the message as pinned
                Main.request.pinMessage(messageObject.getChat_id(),messageObject.getMessageID());
                for(int i = 0; i<messagesTMP.size();i++){
                    if(messagesTMP.get(i).getMessageID().equals(messageObject.getMessageID())){
                        pinnedMessageIndex = i;
                    }
                }
            }
        });
        if(chat!=null) {
            if (myAccess==null || myAccess.isAbilityToPin()) {
                contextMenu.getItems().add(item1);
            }
        }

        Pane likePane = new Pane();
        likePane.setStyle("-fx-border-color: gray; -fx-border-radius:10");
        likePane.setLayoutY(1);
        likePane.setLayoutX(206);
        likePane.setPrefWidth(50);
        likePane.setPrefHeight(17);
        Label likesCount = new Label();
        Label likeEmoji = new Label();
        likeEmoji.setTextFill(Color.WHITE);
        likeEmoji.setFont(likeEmojis.getFont());
        likeEmoji.setLayoutY(likeEmojis.getLayoutY());
        likeEmoji.setLayoutX(likeEmojis.getLayoutX());
        likePane.getChildren().addAll(likeEmoji , likesCount);
        likesCount.setFont(countT.getFont());
        likesCount.setLayoutX(countT.getLayoutX());
        likesCount.setLayoutY(countT.getLayoutY());
        likesCount.setText("0");
        likesCount.setTextFill(countT.getTextFill());
        likeEmoji.setText(likeEmojis.getText());
        profileArea.getChildren().add(likePane);
        likesCount.setText(likesCounts+"");
        likeEmoji.setCursor(Cursor.HAND);
        likeEmoji.setTooltip(likedT);

        Pane dislikePane = new Pane();
        dislikePane.setStyle("-fx-border-color: gray; -fx-border-radius:10");
        dislikePane.setLayoutY(19);
        dislikePane.setLayoutX(206);
        dislikePane.setPrefWidth(50);
        dislikePane.setPrefHeight(17);
        Label dislikesCount = new Label();
        Label dislikeEmoji = new Label();
        dislikeEmoji.setTextFill(Color.WHITE);
        dislikeEmoji.setFont(likeEmojis.getFont());
        dislikeEmoji.setLayoutY(likeEmojis.getLayoutY());
        dislikeEmoji.setLayoutX(likeEmojis.getLayoutX());
        dislikePane.getChildren().addAll(dislikeEmoji , dislikesCount);
        dislikesCount.setFont(countT.getFont());
        dislikesCount.setLayoutX(countT.getLayoutX());
        dislikesCount.setLayoutY(countT.getLayoutY());
        dislikesCount.setText("0");
        dislikesCount.setTextFill(countT.getTextFill());
        dislikeEmoji.setText("\uD83D\uDC4E");
        profileArea.getChildren().add(dislikePane);
        dislikesCount.setText(dislikesCounts+"");
        dislikeEmoji.setCursor(Cursor.HAND);
        dislikeEmoji.setTooltip(dislikedT);

        Pane laughPane = new Pane();
        laughPane.setStyle("-fx-border-color: gray; -fx-border-radius:10");
        laughPane.setLayoutY(37);
        laughPane.setLayoutX(206);
        laughPane.setPrefWidth(50);
        laughPane.setPrefHeight(17);
        Label laughCount = new Label();
        Label laughEmoji = new Label();
        laughEmoji.setTextFill(Color.WHITE);
        laughEmoji.setFont(likeEmojis.getFont());
        laughEmoji.setLayoutY(likeEmojis.getLayoutY());
        laughEmoji.setLayoutX(likeEmojis.getLayoutX());
        laughPane.getChildren().addAll(laughEmoji , laughCount);
        laughCount.setFont(countT.getFont());
        laughCount.setLayoutX(countT.getLayoutX());
        laughCount.setLayoutY(countT.getLayoutY());
        laughCount.setText("0");
        laughCount.setTextFill(countT.getTextFill());
        laughEmoji.setText("\uD83D\uDE02");
        profileArea.getChildren().add(laughPane);
        laughCount.setText(laughsCounts+"");
        laughEmoji.setCursor(Cursor.HAND);
        laughEmoji.setTooltip(laughedT);
        likeEmoji.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"like");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"dislike");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"laugh");
                if(liked[0][0] ==0) {
                    likesCount.setText((Integer.parseInt(likesCount.getText()) + 1) + "");
                    whoLiked.add(me.getUsername());
                    Main.request.react(chat.getId(),messageObject.getMessageID(),"like");
                    liked[0][0]++;
                }
                else{
                    Main.request.unreact(chat.getId(),messageObject.getMessageID(),"like");
                    likesCount.setText((Integer.parseInt(likesCount.getText()) -1) + "");
                    whoLiked.remove(me.getUsername());
                    liked[0][0]--;
                }
                if(disliked[0] ==1){
                    disliked[0]--;
                    whoDisLiked.remove(me.getUsername());
                    dislikesCount.setText((Integer.parseInt(dislikesCount.getText()) -1) + "");
                }
                if(laughed[0] == 1){
                    whoLaughed.remove(me.getUsername());
                    laughCount.setText((Integer.parseInt(laughCount.getText()) -1) + "");
                    laughed[0]--;
                }

            }
        });
        dislikeEmoji.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"like");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"dislike");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"laugh");

                if(disliked[0]==0) {
                    dislikesCount.setText((Integer.parseInt(dislikesCount.getText()) + 1) + "");
                    Main.request.react(chat.getId(),messageObject.getMessageID(),"dislike");
                    disliked[0]++;
                    whoDisLiked.add(me.getUsername());
                }
                else{
                    Main.request.unreact(chat.getId(),messageObject.getMessageID(),"dislike");
                    dislikesCount.setText((Integer.parseInt(dislikesCount.getText()) -1) + "");
                    disliked[0]--;
                    whoDisLiked.remove(me.getUsername());
                }
                if(liked[0][0] ==1){
                    liked[0][0]--;
                    likesCount.setText((Integer.parseInt(likesCount.getText()) -1) + "");
                    whoLiked.remove(me.getUsername());
                }
                if(laughed[0] == 1){
                    whoLaughed.remove(me.getUsername());
                    laughCount.setText((Integer.parseInt(laughCount.getText()) -1) + "");
                    laughed[0]--;
                }


            }
        });
        laughEmoji.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"like");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"dislike");
                Main.request.unreact(chat.getId(),messageObject.getMessageID(),"laugh");
                if(laughed[0]==0) {
                    laughCount.setText((Integer.parseInt(laughCount.getText())+1)+"");
                    Main.request.react(chat.getId(),messageObject.getMessageID(),"laugh");
                    laughed[0]++;
                    whoLaughed.add(me.getUsername());
                }
                else{
                    whoLaughed.remove(me.getUsername());
                    Main.request.unreact(chat.getId(),messageObject.getMessageID(),"laugh");
                    laughCount.setText((Integer.parseInt(laughCount.getText())-1)+"");
                    laughed[0]--;
                }
                if(liked[0][0] ==1){
                    liked[0][0]--;
                    whoLiked.remove(me.getUsername());
                    likesCount.setText((Integer.parseInt(likesCount.getText()) -1) + "");
                }
                if(disliked[0] == 1){
                    whoDisLiked.remove(me.getUsername());
                    dislikesCount.setText((Integer.parseInt(dislikesCount.getText()) -1) + "");
                    disliked[0]--;
                }
            }
        });
        contextMenu.getItems().add(item2);
        //contextMenu.getItems().addAll(item1,item2);
        fileName.setContextMenu(contextMenu);
        fileArea.getChildren().add(fileName);
        fileArea.getChildren().add(arc);
        fileArea.getChildren().add(roo);
        fileArea.getChildren().add(dateL);
        fileArea.getChildren().add(ll);
        fileArea.setLayoutY(0);
        fileArea.setLayoutX(0);
        bb.setLayoutY(0);
        bb.setLayoutX(15);
        bb.getChildren().add(fileArea);
        bb.setPrefWidth(250);
        bb.setPrefHeight(80);
        messageArea.getChildren().add(bb);
        profileArea.getChildren().add(profilePic);
        profileArea.getChildren().add(userNameLabels);
        userNameLabels.setLayoutX(userNameLabel.getLayoutX());
        userNameLabels.setLayoutY(userNameLabel.getLayoutY());
        userNameLabels.setFont(userNameLabel.getFont());
        userNameLabels.setTextFill(Color.WHITE);
        if(fromUsername.equals(me.getUsername())){
            userNameLabels.setText("You");
            userNameLabels.setTextFill(Color.RED);
        }else{
            userNameLabels.setTextFill(Color.CYAN);
        }
        profilePic.setLayoutX(6);
        profilePic.setLayoutY(10);
        MainForm mms = this;
        profilePic.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                showUserInformation(fromUsername,mms);
            }
        });
        profilePic.setCursor(Cursor.HAND);
        profileArea.setLayoutX(groupOthersProfilePane.getLayoutX());
        profileArea.setLayoutY(groupOthersProfilePane.getLayoutY());
        profileArea.setStyle(groupOthersProfilePane.getStyle());
        messageArea.setLayoutX(msgAreaOthersPane.getLayoutX()-5);
        messageArea.setLayoutY(msgAreaOthersPane.getLayoutY());
        messageArea.setStyle(msgAreaOthersPane.getStyle());
        profileArea.setPrefWidth(groupOthersProfilePane.getPrefWidth());
        profileArea.setPrefHeight(groupOthersProfilePane.getPrefHeight());
        profilePic.setPreserveRatio(false);
        profilePic.setSmooth(true);
        profilePic.setFitWidth(38);
        profilePic.setFitHeight(38);
        result.setPrefHeight(groupOthersPane.getPrefHeight());
        result.setPrefHeight(Region.USE_COMPUTED_SIZE);
        result.setPadding(msgContent.getPadding());
        return result;
    }
    private void showUserInformation(String fromUsername, MainForm mms) {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserInformation.fxml"));
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
        stage.setTitle("UserInformation");
        UserInformation userInformation = (UserInformation) loader.getController();
        userInformation.setManager(manager,mms);
        try {
            userInformation.setInformation(Main.request.getInformationByUsername(fromUsername));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.getIcons().add(new Image("file:discord.png"));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.centerOnScreen();
        stage.show();
    }
    public void percentChanged(double value, String filename,Arc arc){


    }
    public void setChatMessages(ArrayList<Message> messages, String members, ServerAccess myAccess,DiscordChat chat) throws InterruptedException, IOException {
        chatArea.getChildren().clear();
        repliyingToMessageID = -1;
        showChatsArea();
        ArrayList<Pane> panes = new ArrayList<>();
        for(Message message : messages){
            if(message instanceof TextMessage){
                TextMessage mm = (TextMessage)message;
                Pane ok = OthersPane(mm.getContent(),mm.getFromUsername(),mm.getDate(),mm,myAccess,chat);
                chatArea.getChildren().add(ok);
                panes.add(ok);
               ok.setVisible(false);
            }

            else if(message instanceof FileMessage){
                FileMessage mm = (FileMessage)message;
                Pane ok = null;
                if(mm.getFilename().endsWith(".jpg")||mm.getFilename().endsWith(".png")){
                    ok = OthersImagePane(mm,myAccess,chat);
                }
                else {
                    ok = OthersFilePane(mm.getFilename(), mm.getFromUsername(), mm.getDate(),mm,myAccess,chat);
                }
                chatArea.getChildren().add(ok);
                ok.setVisible(false);
                panes.add(ok);
            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainPane.setVvalue(1);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = panes.size()-1;i>=0;i--){
                    Pane ok = panes.get(i);
                    int finalI = i;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ok.setVisible(true);
                            TranslateTransition anim = new TranslateTransition();
                            anim.setNode(ok);
                            anim.setByX(-5);
                            anim.setDuration(Duration.millis(150));
                            anim.play();
                            anim.setOnFinished(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    TranslateTransition anim2 = new TranslateTransition();
                                    anim2.setNode(ok);
                                    anim2.setByX(+5);
                                    anim2.setDuration(Duration.millis(150));
                                    anim2.play();
                                }
                            });
                        }
                    });

                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
    void downloadProfilePics(String members) throws IOException {
        for(String k : members.split(",")){
            UserObject user = Main.request.getInformationByID(k);
            Main.request.downloadProfileImage(user.getUsername(),this,manager);
        }
    }
    @FXML
    void sendMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message = new String(typedText.getText().getBytes(),Charset.forName("UTF-8"));
                if(message.replace(" " , "").isEmpty())
                    return;
                typedText.setText("");
                Main.request.sendMessage(selectedChatID,repliyingToMessageID==-1?"":repliyingToMessageID+"",message);
                if(lastReplied!=null){
                    lastReplied.setTextFill(Color.WHITE);
                    lastReplied = null;
                    repliyingToMessageID = -1;
                }
            }
        }).start();


    }
    @FXML
    void goToPinnedMessage(){
        System.out.println("Message index : "+pinnedMessageIndex);
        if(pinnedMessageIndex!=-1 && pinnedMessageIndex<messagesTMP.size()){
            mainPane.setVvalue(((double)(pinnedMessageIndex+1))/chatArea.getChildren().size());
            Pane ok = (Pane)chatArea.getChildren().get(pinnedMessageIndex);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    TranslateTransition anim = new TranslateTransition();
                    anim.setNode(ok);
                    anim.setDuration(Duration.millis(50));
                    anim.setByX(20);
                    anim.play();
                    anim.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            TranslateTransition anim = new TranslateTransition();
                            anim.setNode(ok);
                            anim.setDuration(Duration.millis(50));
                            anim.setByX(-20);
                            anim.play();
                        }
                    });
                }
            });
        }
    }
    void gotoMessageByID(String message_id){
        int cnt = 1;
        for(Message message:messagesTMP){
            if(message.getMessageID().equals(message_id)){
                mainPane.setVvalue(((double)(cnt))/chatArea.getChildren().size());
                Pane ok = (Pane)chatArea.getChildren().get(cnt-1);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        TranslateTransition anim = new TranslateTransition();
                        anim.setNode(ok);
                        anim.setDuration(Duration.millis(50));
                        anim.setByX(10);
                        anim.play();
                        anim.setOnFinished(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                TranslateTransition anim = new TranslateTransition();
                                anim.setNode(ok);
                                anim.setDuration(Duration.millis(50));
                                anim.setByX(-10);
                                anim.play();
                            }
                        });
                    }
                });
            }
            cnt++;
        }
    }
    @FXML
    ImageView callView;
    int pinnedMessageIndex = -1;
    String currentCallID = null;
    public void setVoiceChannel(String chat_id) throws IOException {
        mainPane.setVisible(false);
        PinLABEL.setVisible(false);
        emojipane2.setVisible(false);
        typedText.setVisible(false);
        sendFileButton.setVisible(false);
        sendMessageButton.setVisible(false);
        typePane.setVisible(false);
        mainmainPane.setVisible(false);
        serverSettings.setVisible(false);
        screenPANE.setVisible(false);
        selectedChatID = chat_id;
        messagesTMP = null;
        currentCallID = chat_id;
        boolean voice = true;
        Main.request.joinCall(chat_id,voice);
    }
    @FXML
    ScrollPane screenPANE;
    @FXML
    VBox screenVB;


    public void setScreenChannel(String chat_id){
        mainPane.setVisible(false);
        PinLABEL.setVisible(false);
        emojipane2.setVisible(false);
        typedText.setVisible(false);
        sendFileButton.setVisible(false);
        sendMessageButton.setVisible(false);
        typePane.setVisible(false);
        mainmainPane.setVisible(false);
        serverSettings.setVisible(false);

        selectedChatID = chat_id;
        messagesTMP = null;
        currentCallID = chat_id;
        boolean voice = false;
        screenPANE.setVisible(true);
        HashMap<String , ImageView> usersToImage = new HashMap<>();
        screenVB.getChildren().clear();
        Main.listenForCallPackets = new CallPacketArrived() {
            @Override
            public void handle(CallPacket packet) throws IOException, InterruptedException {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if(packet.getScreenImageDate()!=null) {
                            try {
                                if(!usersToImage.keySet().contains(packet.getToken())){
                                    ImageView img = new ImageView();
                                    img.setPreserveRatio(true);
                                    img.setFitWidth(720);
                                    img.setFitHeight(405);
                                    usersToImage.put(packet.getToken(),img);
                                    img.setLayoutX(0);
                                    img.setLayoutY(0);
                                    screenVB.getChildren().add(img);
                                    screenVB.setMargin(img,new Insets(0,0,15,0));
                                }
                                else{
                                    usersToImage.get(packet.getToken()).setImage(SwingFXUtils.toFXImage(AudioHandler.byteArrayToImage(packet.getScreenImageDate()),null));
                                }
                            } catch (IOException  e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                Thread.sleep(40);
            }
        };
        Main.request.joinCall(chat_id,voice);
    }
    public void setChatMessages(String chat_id) throws IOException, InterruptedException {
        selectedChatID = chat_id;
        DiscordChat chat = Main.request.getChatInformation(chat_id);
        ServerAccess myAccess = Main.request.getUserAccess(chat.getServerId(),me.getUsername());
        Message pinnedMessage = chat.getPinnedMessage();
        ArrayList<Message> messages = Main.request.getMessages(chat);
        messagesTMP = messages;
        if(pinnedMessage!=null){
            int index = 0;
            for(int i = 0; i<messages.size();i++){
                if(messages.get(i).equals(pinnedMessage)){
                    index = i;
                }
            }
            pinnedMessageIndex = index;
        }
        else{
            pinnedMessageIndex = -1;
        }

        chat.setMembers(chat.getMembers().replace("-1,",""));
        sideprofsVB.getChildren().clear();
        try {
            downloadProfilePics(chat.getMembers());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<Pane> panes = new ArrayList<>();
        for(String k : chat.getMembers().split(",")){
            UserObject user = Main.request.getInformationByID(k);
            Pane ok =getSideprofPane(user);
            sideprofsVB.getChildren().add(ok);
            panes.add(ok);

            //ok.setVisible(false);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(Pane ok : panes){
                    //ok.setVisible(true);
                    TranslateTransition anim = new TranslateTransition();
                    anim.setNode(ok);
                    anim.setDuration(Duration.millis(50));
                    anim.setByY(5);
                    anim.play();
                    anim.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            TranslateTransition anim = new TranslateTransition();
                            anim.setNode(ok);
                            anim.setDuration(Duration.millis(50));
                            anim.setByY(-5);
                            anim.play();
                        }
                    });
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

        setChatMessages(messages,chat.getMembers(),myAccess,chat);
    }
    void loadSideProfs(DiscordChat chat){

    }
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
    void close(){
        manager.close();
        getMyStage().close();
        SaveAndLoad.setIsRunning(false);
    }

    void loadEmojies(){
        ArrayList<Emoji> allEmojies = Emojies.getAllEmojies();
        for(int i = 0 ;i<7;i++){
            for(int j = 0 ;j<11;j++){
                BorderPane pane = new BorderPane();
                Label l = new Label(allEmojies.get(11*i+j).getEmoji());
                Tooltip t = new Tooltip(":"+allEmojies.get(11*i+j).getName()+":");
                t.setStyle("-fx-font-family: 'Segoe UI';-fx-font-size: 16px;");
                l.setTooltip(t);
                l.setTextAlignment(TextAlignment.CENTER);
                l.setAlignment(Pos.CENTER);
                l.setTextFill(Color.WHITE);
                emojiPane.setAlignment(Pos.CENTER);
                emojiPane.add(pane,i,j);
                pane.setCenter(l);
                l.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        typedText.setText(typedText.getText()+l.getText());
                    }
                });
                l.setCursor(Cursor.HAND);
                l.setFont(msgContent.getFont());
            }
        }
    }
    @FXML
    void minize(){
        getMyStage().setIconified(true);
        
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        me = Main.request.getMyInformation();

        loadEmojies();
        try {
            removeProfs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        typedText.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER))
                    sendMessage();
            }
        });
        MainForm hmm = this;
        profileBottom.setCursor(Cursor.HAND);
        ImageWaitingEvent myEvent = new ImageWaitingEvent() {
            @Override
            public void Finished(String filename, String username, Image img) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        profileBottom.setImage(img);
                    }
                });
            }
        };
        ImagePool.addListener(me.getUsername(),myEvent);

        profileBottom.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                showUserInformation(me.getUsername(),hmm);
            }
        });
        myNameBelow.setOnMouseClicked(profileBottom.getOnMouseClicked());
        myNameBelow.setCursor(Cursor.HAND);
        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DownloadManager.fxml"));
        Scene scene = null;
        try {
            Parent root = loader.load();
            scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setResizable(false);
        stage.setTitle("DownloadManager");
        manager = (DownloadManager)loader.getController();
        manager.setHmm(this);
        stage.getIcons().add(new Image("file:discord.png"));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setX(1697);
        stage.setY(50);
        stage.show();
        Main.listener.addMessageListener(this);
        Main.listener.mainForm = this;
        myNameBelow.setText(me.getUsername());

        muteBottom.setImage(new Image("file:unmuted.png"));
        serverSettings.setImage(new Image("file:settings.png"));
        speakerBottom.setImage(new Image("file:speaker.png"));
        settingsBottom.setImage(new Image("file:settings.png"));
        settingsBottom.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("jpg files","*.jpg"));
                File chosen = fileChooser.showOpenDialog(getMyStage());

                if(chosen!=null) {
                    try {
                        removeProfsMine();
                        FileManager.uploadProfilePictur(chosen.getAbsolutePath(), new DownloadFininshedEvent() {
                            @Override
                            public void fileDownloadFinishedHandler(String filename) {
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        selectedServerName.setText("matinServer");

        loadServers();
        try {
            loadFriendRequests();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mainPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != mainPane
                        && event.getDragboard().hasFiles()) {
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });
        mainPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                List<File> files = event.getDragboard().getFiles();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (files.size()!=0) {
                            for(File file:files) {
                                if(file.exists()&&file.isFile()){
                                    System.out.println(file.getAbsolutePath()+" is now sending...");
                                    sendFile(file.getAbsolutePath());
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }).start();
                event.setDropCompleted(true);
                event.consume();
            }
        });

        Main.listener.newChatListener = new NewChatCreated() {
            @Override
            public void onChatCreated() throws IOException, InterruptedException {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                chatsVB.getChildren().clear();
                                ArrayList<DiscordChat> chats = Main.request.getListOfDirectChats(me.getUsername());
                                for(DiscordChat chat : chats) {
                                    Pane pp = getADMChatPane(chat);
                                    chatsVB.getChildren().add(pp);
                                }
                            }
                            catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    });
            }
        };

        friendShipUsername.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)){
                    try {
                        checkforUser();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Main.incomingCallEvent = new NewIncomingCall() {
            @Override
            public void handle(CallRequest request,SocketDataTransfer IO) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(request.getUsername());
                        Stage stage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("PhoneCall.fxml"));
                        Scene scene = null;
                        try {
                            Parent root = loader.load();
                            scene = new Scene(root);
                            stage.setScene(scene);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stage.setResizable(false);
                        stage.setTitle("PhoneCall");
                        try {
                            ((PhoneCall)loader.getController()).setCall(false,request.getUsername());
                            ((PhoneCall)loader.getController()).setCallID(request.getCallID());

                            ((PhoneCall)loader.getController()).SetCallReq(request,IO);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        stage.getIcons().add(new Image("file:discord.png"));
                        stage.initStyle(StageStyle.TRANSPARENT);
                        scene.setFill(Color.TRANSPARENT);
                        stage.centerOnScreen();
                        stage.setAlwaysOnTop(true);
                        stage.show();
                    }
                });
            }
        };


        callUser.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PhoneCall.fxml"));
                Scene scene = null;
                try {
                    Parent root = loader.load();
                    scene = new Scene(root);
                    stage.setScene(scene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setResizable(false);
                stage.setTitle("PhoneCall");
                try {
                    ((PhoneCall)loader.getController()).setCall(true,callingToUsername);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.getIcons().add(new Image("file:discord.png"));
                stage.initStyle(StageStyle.TRANSPARENT);
                scene.setFill(Color.TRANSPARENT);
                stage.centerOnScreen();
                stage.setAlwaysOnTop(true);
                stage.show();
                try {
                    Main.request.callUser(callingToUsername);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                Main.callConnectedEvent = new CallConnected() {
                    @Override
                    public void handle(String callid) {
                        ((PhoneCall)loader.getController()).setCallID(callid);
                    }
                };
            }
        });
    }
    @FXML
    ImageView friendprof;
    @FXML
    Label friendShipReqSend;
    void checkforUser() throws IOException {
        String username = friendShipUsername.getText();
        UserObject user = Main.request.getInformationByUsername(username);
        if(user!=null){
            Main.request.downloadProfileImage(username, new DownloadFininshedEvent() {
                @Override
                public void fileDownloadFinishedHandler(String filename) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            friendprof.setImage(generateProfilePicture(new Image("file:"+filename),33,33));
                        }
                    });

                }
            },manager);
            friendShipReqSend.setDisable(false);
            MainForm ff = this;
            friendprof.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    showUserInformation(username,ff);
                }
            });
            friendprof.setCursor(Cursor.HAND);
        }
        else {
            friendShipReqSend.setDisable(true);
            friendprof.setImage(null);
            friendprof.setCursor(Cursor.DEFAULT);
            friendprof.setOnMouseClicked(null);
        }

    }
    @FXML
    void sendFriendShipReq(){
        Main.request.friendshipRequest(friendShipUsername.getText());
    }


    Stage getMyStage(){
        return ((Stage)((Node)chatNameLabel).getScene().getWindow());
    }
    @FXML
    void chooseFileToSend(){
        FileChooser fileChooser = new FileChooser();
        File chosen = fileChooser.showOpenDialog(getMyStage());
        if(chosen!=null) {
            System.out.println(chosen.getAbsolutePath());
            sendFile(chosen.getAbsolutePath().trim());
        }

    }
    void sendFile(String filename){
        filename = new String(filename.getBytes(),Charset.forName("UTF-8"));

        String finalFilename = filename;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int id = FileManager.uploadFile(finalFilename, new UploadFinishedEvent() {
                        @Override
                        public void uploadFinishedHandle(String filename) {
                            manager.downloadOrUploadFinished(filename);
                        }
                    }, manager, null);
                    Main.request.sendMessage(selectedChatID,String.valueOf(id));
                    System.out.println(id);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    private void removeProfs() throws IOException {
        File dir = new File("ProfilePics");
        for(File file: dir.listFiles())
            if (!file.isDirectory())
                file.delete();

    }
    private void removeProfsMine(){
        File dir = new File("ProfilePics");
        for(File file: dir.listFiles())
            if (!file.isDirectory() && file.getName().equals(me.getUsername()+".jpg"))
                file.delete();

    }
    private static ImageView noname1(String name , String familyName){
        ImageView img1 = new ImageView();
        img1.setImage(generateProfilePicture(name , familyName,125,125));
        img1.setStyle("-fx-background-radius: 45;\n" +
                "    -fx-border-radius: 45;\n" +
                "    -fx-border-width: 1;\n" +
                "    -fx-border-color: cyan;\n" +
                "    -fx-pref-width: 50;\n" +
                "    -fx-pref-height: 50;\n" +
                "    -fx-background-color: transparent;\n" +
                "    -fx-background-size: 50;");
        img1.setFitWidth(55);
        img1.setFitHeight(55);

        return img1;
    }
    private static ImageView noname2(String profilePath,int width , int height){
        ImageView img1 = new ImageView();
        img1.setImage(generateProfilePicture(new Image("file:"+profilePath),width,height));
        img1.setStyle("-fx-background-radius: 45;\n" +
                "    -fx-border-radius: 45;\n" +
                "    -fx-border-width: 1;\n" +
                "    -fx-border-color: cyan;\n" +
                "    -fx-pref-width: 50;\n" +
                "    -fx-pref-height: 50;\n" +
                "    -fx-background-color: transparent;\n" +
                "    -fx-background-size: 50;");
        img1.setFitWidth(55);
        img1.setFitHeight(55);
        return img1;
    }
    private static ImageView noname2(Image img,int width , int height){
        ImageView img1 = new ImageView();
        img1.setImage(generateProfilePicture(img,width,height));
        img1.setStyle("-fx-background-radius: 45;\n" +
                "    -fx-border-radius: 45;\n" +
                "    -fx-border-width: 1;\n" +
                "    -fx-border-color: cyan;\n" +
                "    -fx-pref-width: 50;\n" +
                "    -fx-pref-height: 50;\n" +
                "    -fx-background-color: transparent;\n" +
                "    -fx-background-size: 50;");
        img1.setFitWidth(55);
        img1.setFitHeight(55);

        return img1;
    }
    private static Image generateProfilePicture(String name , String familyName,int width,int height) {//pass 125 125
        String text = name.substring(0,1).toUpperCase()+familyName.substring(0,1).toUpperCase();
        Label label = new Label(text);
        label.setStyle("-fx-background-color: transparent; -fx-text-fill:white;-fx-font-size: 50px; -fx-font-family: 'Segoe UI'");
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        //label.setLayoutX(20);
        //label.setLayoutY(33);
        //label.setWrapText(true);
        AnchorPane g = new AnchorPane(label);
        g.setPrefWidth(125);
        g.setPrefHeight(125);
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);
        g.setStyle("-fx-background-color: #8b3ad4;-fx-border-color:#5662F6;-fx-background-radius: 100; -fx-border-width: 0;");
        Scene scene = new Scene(g);
        scene.setFill(Color.TRANSPARENT);
        
        WritableImage img = new WritableImage(width, height);
        scene.snapshot(img);
        return img ;
    }
    public static Image generateProfilePicture(Image profilePic,int width , int height) { //pass 124 124
        ImageView label = new ImageView();
        label.setImage(profilePic);

        label.setStyle(
                "    -fx-border-width: 1;\n" +
                "    -fx-border-color: cyan;\n" +
                "    -fx-pref-width: 50;\n" +
                "    -fx-pref-height: 50;\n" +
                "    -fx-background-color: red;\n" +
                "     -fx-border-style: none;");

        //label.setLayoutX(20);
        //label.setLayoutY(33);
        //label.setWrapText(true);
        AnchorPane g = new AnchorPane();
        g.setPrefWidth(width);
        g.setPrefHeight(height);

        g.setStyle("-fx-background-color: transparent;-fx-border-color:black;-fx-background-radius: 100;-fx-border-width: 0; -fx-border-style: none; ");

        Circle c = new Circle(width/2 - 1);
        c.smoothProperty().set(true);
        c.setStroke(Color.WHITE);
        c.setStrokeWidth(2);
        c.setFill(new ImagePattern(profilePic));

        AnchorPane.setLeftAnchor(c, 0.0);
        AnchorPane.setRightAnchor(c, 0.0);
        AnchorPane.setTopAnchor(c, 0.0);
        AnchorPane.setBottomAnchor(c, 0.0);
        g.getChildren().add(c);
        Scene scene = new Scene(g);
        scene.setFill(Color.TRANSPARENT);
        WritableImage img = new WritableImage(width,height);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                scene.snapshot(img);

            }
        });
        return img ;
    }
    synchronized void setSy(boolean v){
        sy = v;
    }
    synchronized boolean getSy(){return sy;}
    public synchronized void loadMessage(String serverID , String chat_id) throws IOException, InterruptedException {
        ((Stage)((Node)chatNameLabel).getScene().getWindow()).setAlwaysOnTop(true);
        Thread.sleep(10);
        ((Stage)((Node)chatNameLabel).getScene().getWindow()).setAlwaysOnTop(false);
        //loadServer(serverID);
        selectedChatID = chat_id;
        setChatMessages(chat_id);
    }
    @Override
    public void handle(DiscordChat chat, Message message) throws IOException {
        setSy(false);
        if(message instanceof TextMessage) {
            TextMessage msg = (TextMessage) message;
            if(!message.getChat_id().equals(selectedChatID)) {
                AlertSystem.Notify(message.getFromUsername()+" at "+chat.getName() ,msg.getContent() , NotificationType.SUCCESS,Duration.millis(3000),this,chat.getId(),chat.getServerId());
                return;
            }

        }
        else{
            FileMessage msg = (FileMessage) message;
            if(!message.getChat_id().equals(selectedChatID)) {
                AlertSystem.Notify(message.getFromUsername()+" at "+chat.getName() ,"File : "+msg.getFilename() , NotificationType.SUCCESS,Duration.millis(3000),this,chat.getId(),chat.getServerId());
                return;
            }
        }
        final int[] jun = new int[1];
        double now = mainPane.getHeight();
        final Pane[] ok = new Pane[1];
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                jun[0] = chatArea.getChildren().size();
                try {
                    messagesTMP.add(message);
                    ServerAccess myAccess = Main.request.getUserAccess(chat.getServerId(),me.getUsername());
                    if(message instanceof TextMessage)
                    ok[0] = OthersPane(((TextMessage) message).getContent(),message.getFromUsername(), message.getDate(), (TextMessage) message, myAccess, chat);
                    else {
                        if(((FileMessage) message).getFilename().endsWith(".png")||((FileMessage) message).getFilename().endsWith(".jpg")){
                            ok[0] = OthersImagePane((FileMessage) message, myAccess, chat);
                        }
                        else
                            ok[0] = OthersFilePane(((FileMessage) message).getFilename(), message.getFromUsername(), message.getDate(), (FileMessage) message, myAccess,chat);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                chatArea.getChildren().add(ok[0]);
                TranslateTransition anim = new TranslateTransition();
                anim.setNode(ok[0]);
                anim.setByX(-5);
                anim.setDuration(Duration.millis(150));
                anim.play();
                anim.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        TranslateTransition anim2 = new TranslateTransition();
                        anim2.setNode(ok[0]);
                        anim2.setByX(+5);
                        anim2.setDuration(Duration.millis(150));
                        anim2.play();
                    }
                });
            }
        });
        while(true) {
            final boolean[] stop = new boolean[1];
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (jun[0] != chatArea.getChildren().size()) {
                        mainPane.setVvalue(mainPane.getVmax());
                        setSy(true);
                    }
                }
            });
            if(getSy())
                break;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("done");

    }
    @Override
    public void fileDownloadFinishedHandler(String filename) {
        manager.downloadOrUploadFinished(filename);
        ImagePool.newImageArrived(filename);
        manager.downloadOrUploadFinished(filename);
    }
    @Override
    public void uploadFinishedHandle(String filename) {
        manager.downloadOrUploadFinished(filename);
    }
}
