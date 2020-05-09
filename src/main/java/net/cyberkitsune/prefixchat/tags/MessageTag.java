package net.cyberkitsune.prefixchat.tags;

import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageTag implements ChatTag {
    @Override
    public String getPlaceholder() {
        return "message";
    }

    @Override
    public String getReplacement(String message, KitsuneChannel channel, AsyncPlayerChatEvent context) {
        return message;
    }

    @Override
    public String formatPlaceholder(String message, KitsuneChannel channel, AsyncPlayerChatEvent context) {
        if(context.isCancelled()) {
            return message.replace("{"+getPlaceholder()+"}", getReplacement(message, channel, context));
        } else {
            return message.replace("{"+getPlaceholder()+"}", "%2\\$s");
        }
    }
}
