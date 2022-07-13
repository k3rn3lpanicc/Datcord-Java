package DiscordEvents;

public interface DownloadFininshedEvent extends FileIOEvent{
    void fileDownloadFinishedHandler(String filename);

}
