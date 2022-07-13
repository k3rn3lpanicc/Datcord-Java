package gui;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoadingForm implements Initializable {
    @FXML
    Arc arc1;
    @FXML
    Arc arc2;
    @FXML
    Label typed;
    String toType = "Datcord is a free voice, video, and text chat app that's used by tens of millions of people ages 13+ to talk and hang out with their communities and friends.";
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ScaleTransition transition1 = new ScaleTransition();
        transition1.setNode(arc1);
        transition1.setDuration(Duration.millis(700));
        transition1.setFromX(1);
        transition1.setToX(1.2);
        transition1.setFromY(1);
        transition1.setToY(1.2);

        ScaleTransition transition11 = new ScaleTransition();
        transition11.setNode(arc1);
        transition11.setDuration(Duration.millis(800));
        transition11.setFromX(1.2);
        transition11.setToX(1);
        transition11.setFromY(1.2);
        transition11.setToY(1);

        ScaleTransition transition2 = new ScaleTransition();
        transition2.setNode(arc2);
        transition2.setDuration(Duration.millis(900));
        transition2.setFromX(1);
        transition2.setToX(1.2);
        transition2.setFromY(1);
        transition2.setToY(1.2);
        ScaleTransition transition22 = new ScaleTransition();
        transition22.setNode(arc2);
        transition22.setDuration(Duration.millis(800));
        transition22.setFromX(1.2);
        transition22.setToX(1);
        transition22.setFromY(1.2);
        transition22.setToY(1);


        transition1.setOnFinished(event -> {transition11.play();});
        transition11.setOnFinished(event -> {transition1.play();});
        transition2.setOnFinished(event -> transition22.play());
        transition22.setOnFinished(event -> transition2.play());
        transition1.play();
        transition22.play();

        Thread kk = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i<=toType.length();i++) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int finalI = i;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            typed.setText(toType.substring(0,finalI));
                        }
                    });
                }
                try {
                    Thread.sleep(4000);
                    loadMainForm();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        kk.start();

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
    private void loadMainForm() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ((Stage)arc1.getScene().getWindow()).close();
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainForm.fxml"));
                Scene scene = null;
                try {
                    Parent root = loader.load();
                    scene = new Scene(root);
                    stage.setScene(scene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setResizable(false);
                stage.setTitle("Datcord");
                stage.getIcons().add(new Image("file:discord.png"));
                //stage.initStyle(StageStyle.UNDECORATED);
                stage.initStyle(StageStyle.TRANSPARENT);
                scene.setFill(Color.TRANSPARENT);
                stage.setX(60);
                stage.setY(50);

                stage.show();

                //todo : open mainForm with animation


            }
        });
    }
}
