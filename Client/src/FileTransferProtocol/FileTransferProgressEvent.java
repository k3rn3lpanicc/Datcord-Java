package FileTransferProtocol;

import javafx.scene.shape.Arc;

public interface FileTransferProgressEvent {
    void updateProgress(double percent, String fileName, Arc arc);
}
