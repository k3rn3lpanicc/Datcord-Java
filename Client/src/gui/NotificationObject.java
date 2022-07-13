package gui;

import javafx.util.Duration;

public class NotificationObject {
    private final String chat_id;
    private final String server_id;
    private String title;
    private String message;
    private Duration duration;
    private NotificationType type;
    private MainForm mainForm;

    public NotificationObject(String title, String message, Duration duration, NotificationType type, MainForm mainForm, String chat_id, String server_id) {
        this.title = title;
        this.message = message;
        this.duration = duration;
        this.type = type;
        this.mainForm = mainForm;
        this.server_id = server_id;
        this.chat_id = chat_id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public MainForm getMainForm() {
        return  mainForm;
    }

    public String getchatid() {
        return chat_id;
    }

    public String getserverid() {
        return server_id;
    }
}
