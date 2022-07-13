package gui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ImageViewer implements Initializable {

    @FXML
    ImageView imageView;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void setImageView(Image img){
        imageView.setImage(img);
        imageView.setCursor(Cursor.HAND);
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ((Stage)((Node)imageView).getScene().getWindow()).close();
            }
        });
    }
}
