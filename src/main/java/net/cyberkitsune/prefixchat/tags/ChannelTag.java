package net.cyberkitsune.prefixchat.tags;

import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChannelTag implements ChatTag{
    @Override
    public String getPlaceholder() {
        return "channel";
    }

    @Override
    public String getReplacement(String message, KitsuneChannel channel, AsyncPlayerChatEvent context) {
        return channel.getChannelName();
    }
}
