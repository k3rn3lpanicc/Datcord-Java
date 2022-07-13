package DiscordEvents;

import UserManagement.DiscordChat;
import UserManagement.Message;

import java.io.IOException;

public interface NewMessageArrivedEvent {
    void handle(DiscordChat chat , Message message) throws IOException;
}
