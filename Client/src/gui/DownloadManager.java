package gui;

import DiscordEvents.DownloadFininshedEvent;
import DiscordEvents.DownloadOrUploadFileEvent;
import DiscordEvents.FileIOEvent;
import FileTransferProtocol.FileManager;
import FileTransferProtocol.FileTransferProgressEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class DownloadManager implements Initializable,FileTransferProgressEvent, DownloadOrUploadFileEvent {

    @FXML
    Pane example;
    @FXML
    ProgressBar barExample;
    @FXML
    Label fileNameExample;
    @FXML
    VBox vbb;
    HashMap<String , ArrayList<ProgressBar>> fileToProgressBar = new HashMap<>();
    HashMap<String , ArrayList<Pane>> fileToPane = new HashMap<>();
    HashMap<String , ArrayList<Label>> fileToDownloadingLabel = new HashMap<>();
    MainForm hmm;

    public void setHmm(MainForm hmm) {
        this.hmm = hmm;
    }

    Pane getNewFilePane(String filename , boolean downloading){
        ProgressBar bar = new ProgressBar();
        bar.setProgress(0);
        //bar.setStyle(barExample.getStyle());

        bar.getStylesheets().add(0,MainForm.class.getResource("checkbox.css").toExternalForm());
        bar.getStyleClass().set(0,"progressBar");
        bar.setPrefWidth(barExample.getPrefWidth());
        bar.setPrefHeight(8);
        Label filenameLabel = new Label();
        filenameLabel.setTextFill(fileNameExample.getTextFill());
        filenameLabel.setFont(fileNameExample.getFont());
        filenameLabel.setText(filename);
        Label DOwnloadingOrUploading = new Label();
        DOwnloadingOrUploading.setTextFill(fileNameExample.getTextFill());
        DOwnloadingOrUploading.setFont(fileNameExample.getFont());
        DOwnloadingOrUploading.setText(downloading?"Downloading" : "Uploading");
        Pane result = new Pane();
        result.setStyle(example.getStyle());
        result.setPrefHeight(example.getPrefHeight());
        result.setPrefWidth(example.getPrefWidth());
        result.getChildren().addAll(filenameLabel , DOwnloadingOrUploading , bar);
        bar.setLayoutX(barExample.getLayoutX());
        bar.setLayoutY(barExample.getLayoutY());
        DOwnloadingOrUploading.setLayoutY(33);
        DOwnloadingOrUploading.setLayoutX(21);
        filenameLabel.setLayoutX(16);
        filenameLabel.setLayoutY(10);
        if(!fileToProgressBar.keySet().contains(filename)){
            fileToProgressBar.put(filename , new ArrayList<>());
        }
        if(!fileToPane.keySet().contains(filename)){
            fileToPane.put(filename , new ArrayList<>());
        }
        if(!fileToDownloadingLabel.keySet().contains(filename)){
            fileToDownloadingLabel.put(filename , new ArrayList<>());
        }

        fileToProgressBar.get(filename).add(bar);
        fileToPane.get(filename).add(result);
        fileToDownloadingLabel.get(filename).add(DOwnloadingOrUploading);
        return result;
    }
    @Override
    public void updateProgress(double percent, String fileName, Arc arc) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                hmm.percentChanged(percent,fileName,arc);
            }
        }).start();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                fileToProgressBar.get(fileName).forEach(e->{e.setProgress(percent/100);});
                if(percent==100){
                    downloadOrUploadFinished(fileName);
                }
            }
        });
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FileManager.setDouListener(this);
    }

    @Override
    public void handleIt(String filename,boolean downloading) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbb.getChildren().add(getNewFilePane(filename,downloading));
            }
        });
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
    @FXML
    ScrollPane panee;
    public void downloadOrUploadFinished(String filename){
        if(!fileToPane.keySet().contains(filename)){
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    panee.setVvalue(1);
                    fileToPane.get(filename).forEach(e->{e.setStyle("-fx-border-color: cyan;-fx-border-width: 0 1 2 1;");});
                    fileToProgressBar.get(filename).forEach(e->{e.setCursor(Cursor.HAND);});
                    fileToDownloadingLabel.get(filename).forEach(e->{e.setText("Finished");});
                    fileToProgressBar.get(filename).forEach(e->{e.setProgress(100);});
                    fileToProgressBar.get(filename).forEach(e->{e.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            File file = new File(filename);
                            try {
                                Desktop.getDesktop().open(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });});
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });


    }

    public void close() {
        ((Stage)((Node)panee).getScene().getWindow()).close();
    }
}
