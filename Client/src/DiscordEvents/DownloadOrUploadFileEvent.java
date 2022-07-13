package DiscordEvents;

public interface DownloadOrUploadFileEvent {
    void handleIt(String filename,boolean downloading);
}
