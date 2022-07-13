package DiscordEvents;

import CallService.CallPacket;

import java.io.IOException;

public interface CallPacketArrived {
    void handle(CallPacket packet) throws IOException, InterruptedException;
}
