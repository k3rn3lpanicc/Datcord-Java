package gui;

import DiscordEvents.ImageWaitingEvent;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;

public class ImagePool {
    private static HashMap<String, Image> userNameToImage = new HashMap<>();
    private static HashMap<String, ArrayList<ImageWaitingEvent>> usernameToEvent = new HashMap<>();
    public static void addListener(String username , ImageWaitingEvent listener){
        if(userNameToImage.keySet().contains(username)){
            listener.Finished("ProfilePics\\"+username+".jpg",username,userNameToImage.get(username));
        }
        else {
            if (usernameToEvent.keySet().contains(username)) {
                usernameToEvent.get(username).add(listener);
            } else {
                usernameToEvent.put(username, new ArrayList<>());
                usernameToEvent.get(username).add(listener);
            }
        }
    }
    public static synchronized void newImageArrived(String filename){
        if(filename.startsWith("ProfilePics\\")) {
            String username = filename.split("\\\\")[1].replace(".jpg", "");
            Image img = MainForm.generateProfilePicture(new Image("file:" + filename), 38, 38);
            userNameToImage.put(username, img);
            if (usernameToEvent.keySet().contains(username)) {
                usernameToEvent.get(username).forEach(e -> {
                    e.Finished(filename, username, img);
                });
               // usernameToEvent.clear();
            }
        }
    }

    

}
