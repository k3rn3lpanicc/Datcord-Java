package DiscordEvents;

import java.io.IOException;

public interface NewChatCreated {
    void onChatCreated() throws IOException, InterruptedException;
}
