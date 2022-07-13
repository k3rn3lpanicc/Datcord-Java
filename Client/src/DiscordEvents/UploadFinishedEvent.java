package DiscordEvents;

public interface UploadFinishedEvent extends FileIOEvent {
    void uploadFinishedHandle(String filename);
}
