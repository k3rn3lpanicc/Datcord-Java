package DiscordEvents;

import CallOB.CallRequest;
import DataTransmit.SocketDataTransfer;

public interface NewIncomingCall {
    void handle(CallRequest request, SocketDataTransfer IO);
}
