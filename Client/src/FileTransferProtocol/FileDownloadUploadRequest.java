package FileTransferProtocol;

import DiscordEvents.FileIOEvent;

public class FileDownloadUploadRequest {
    boolean download;
    String filename;
    String dest;
    FileIOEvent eventListener;
    FileTransferProgressEvent progressListener;
    public FileDownloadUploadRequest(boolean download, String filename, String dest,FileIOEvent eventListener,FileTransferProgressEvent progressListener) {
        this.download = download;
        this.filename = filename;
        this.dest = dest;
        this.eventListener = eventListener;
        this.progressListener = progressListener;
    }
    FileDownloadUploadRequest(){}

    public FileIOEvent getEventListener() {
        return eventListener;
    }

    public FileTransferProgressEvent getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(FileTransferProgressEvent progressListener) {
        this.progressListener = progressListener;
    }

    public void setEventListener(FileIOEvent eventListener) {
        this.eventListener = eventListener;
    }

    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }
}
