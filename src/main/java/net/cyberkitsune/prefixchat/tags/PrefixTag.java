package net.cyberkitsune.prefixchat.tags;

import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PrefixTag implements ChatTag {
    @Override
    public String getPlaceholder() {
        return "prefix";
    }

    @Override
    public String getReplacement(String message, KitsuneChannel channel, AsyncPlayerChatEvent context) {
        return channel.getPrefix();
    }
}
