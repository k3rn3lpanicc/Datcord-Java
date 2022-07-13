package gui;

import CallOB.CallObject;
import CallOB.CallRequest;
import CallService.CallListener;
import DataTransmit.DataPacket;
import DataTransmit.SocketDataTransfer;
import DiscordEvents.DownloadFininshedEvent;
import UserManagement.Request;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

public class PhoneCall implements Initializable {
    String profilePlace;
    boolean speakermute = false;
    boolean hasprofile;
    boolean ringing = true;
    int duration = 0;
    boolean callEnd = false;
    String callID;
    boolean uCall;
    String username;
    @FXML
    Label userfuckingname;
    @FXML
    Label usrfuckingname;
    public void setCall(boolean uCall,String username) throws IOException {
        this.uCall = uCall;
        this.username = username;
        userfuckingname.setText(username);
        usrfuckingname.setText(username);
        if(uCall){
            accept();
        }
        else{
            endCallButton.setDisable(false);
        }
        Main.request.downloadProfileImage(username, new DownloadFininshedEvent() {
            @Override
            public void fileDownloadFinishedHandler(String filename) {
                picture.setImage(MainForm.generateProfilePicture(new Image("file:"+filename),190,190));
            }
        },null);
    }

    public void setCallID(String callID) {
        this.callID = callID;

    }

    public void setHasprofile(boolean hasprofile) {
        this.hasprofile = hasprofile;
        picture.setImage(new Image("file:prof.jpg"));

    }

    public void setProfilePlace(String profilePlace) {
        this.profilePlace = profilePlace;
        picture.setImage(new Image("file:"+profilePlace));

    }
    @FXML
    Line speakerMute;
    @FXML
    Line audioInputMute;
    @FXML
    Line webcamMute;
    @FXML
    ImageView picture;
    @FXML
    ImageView picture2;
    @FXML
    ImageView picSpeaker;
    @FXML
    Label time;
    ScaleTransition scaleTransition;
    ScaleTransition scaleTransition2;
    @FXML
    Button rejectButton;
    @FXML
    Button endCallButton;

    MediaPlayer mediaPlayer;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        picture.setClip(new Circle(picture.getX()+picture.getFitWidth()/2 , picture.getY()+picture.getFitHeight()/2 , picture.fitWidthProperty().get()/2));
        picture2.setClip(new Circle(picture2.getX()+picture2.getFitWidth()/2 , picture2.getY()+picture2.getFitHeight()/2 , picture2.fitWidthProperty().get()/2));
        picture2.setImage(new Image("file:backp.png"));
        GaussianBlur blur = new GaussianBlur();
        blur.setRadius(20);
        picture2.setEffect(blur);

        scaleTransition = new ScaleTransition();
        scaleTransition.setNode(picture2);
        scaleTransition.setDuration(Duration.millis(200));
        scaleTransition.setFromX(1);
        scaleTransition.setToX(1.05);
        scaleTransition.setFromY(1);
        scaleTransition.setToY(1.05);
        scaleTransition.play();
        scaleTransition.setOnFinished(event -> {if(!ringing){scaleTransition2.setDuration(Duration.millis(1000));}scaleTransition2.play();});

        scaleTransition2 = new ScaleTransition();
        scaleTransition2.setNode(picture2);
        scaleTransition2.setDuration(Duration.millis(200));
        scaleTransition2.setFromX(1.05);
        scaleTransition2.setToX(1);
        scaleTransition2.setFromY(1.05);
        scaleTransition2.setToY(1);
        scaleTransition2.setOnFinished(event -> {if(!ringing){scaleTransition.setDuration(Duration.millis(1000));}scaleTransition.play();});
        //scaleTransition2.play()
        String path = "src//gui//ringtone.mp3";
        Media media = new Media(Paths.get(path).toUri().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.cycleCountProperty().setValue(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

    }
    @FXML
    void reject(){

        endCall();
        if(mediaPlayer!=null)
            mediaPlayer.dispose();
        //todo : add a reject sound effect
        ((Stage)picture.getScene().getWindow()).close();
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
    private String numberToTime(){
        int d = duration;
        int hours = (d/60)/60;
        d-= hours*60*60;
        int minutes = d/60;
        d-= minutes*60;
        int seconds = d;
        return (hours>=10?hours:"0"+hours)+":"+(minutes>=10?minutes:"0"+minutes)+":"+(seconds>=10?seconds:"0"+seconds);
    }
    @FXML
    void accept(){
        mediaPlayer.stop();

        ringing = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(callEnd)
                        break;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            time.setText(numberToTime());
                        }
                    });
                    duration++;

                }
            }
        }).start();
        FadeTransition fadeTransition = new FadeTransition();
        FadeTransition fadeTransition2 = new FadeTransition();
        fadeTransition2.setNode(pane2);
        fadeTransition2.setFromValue(0);
        fadeTransition2.setToValue(1);
        fadeTransition.setNode(pane1);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setDuration(Duration.millis(200));
        fadeTransition2.setDuration(Duration.millis(200));
        fadeTransition.setOnFinished(event ->{pane1.setVisible(false);pane2.setOpacity(0);pane2.setVisible(true);fadeTransition2.play();});
        fadeTransition.play();
        if(!uCall) {
            HashMap<String, String> data = new HashMap<>();
            data.put("accepted", "true");
            data.put("call", request.getCallID());
            data.put("token", Main.request.getToken());
            Main.request.getCallIO().sendData(new DataPacket("CallAccept", data));
            IO.sendData("OK");
            Request.outgoingCalls.add(new CallObject(request.getCallID(), new Date().getTime()));
            Request.callThread = new Thread(new CallListener(Main.request.getCallService(), Main.request.getToken(), Main.request.getCallIO(), request.getCallID(), true));
            Request.callThread.start();
        }

    }

    @FXML
    Pane pane1;
    void endCall(){
        callEnd = true;
        Main.request.disconnectFromCall(callID);

    }
    @FXML
    void SpeakermuteUnmute(){
        if(speakermute){

            speakermute = false;
            speakerMute.setVisible(false);
        }
        else{
            speakermute = true;
            speakerMute.setVisible(true);

        }
    }
    boolean audioSourceAvailable = true;
    @FXML
    void AudioSourceMuteUnmute(){
        if(!audioSourceAvailable){
            audioInputMute.setVisible(false);
            audioSourceAvailable = true;
        }
        else {
            audioInputMute.setVisible(true);
            audioSourceAvailable = false;
        }
    }
    boolean webcamAvailable = true;
    @FXML
    Pane pane2;
    @FXML
    void webcamMuteUnmute(){
        if(!webcamAvailable){
            webcamMute.setVisible(false);
            webcamAvailable = true;
        }
        else {
            webcamMute.setVisible(true);
            webcamAvailable = false;
        }
    }
    CallRequest request;
    SocketDataTransfer IO;
    public void SetCallReq(CallRequest request, SocketDataTransfer IO) {
        this.request = request;
        userfuckingname.setText(request.getUsername());
        usrfuckingname.setText(request.getUsername());
        this.IO = IO;
    }
}

