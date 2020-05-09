package net.cyberkitsune.prefixchat.tags;

import com.sun.org.apache.bcel.internal.generic.LASTORE;
import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface ChatTag {
    public enum TagPriority
    {
        FIRST,
        NORMAL,
        LAST
    };
    String getPlaceholder();
    String getReplacement(String message, KitsuneChannel channel, AsyncPlayerChatEvent context);
    default String formatPlaceholder(String message, KitsuneChannel channel, AsyncPlayerChatEvent context)
    {
        return message.replace("{" + getPlaceholder() + "}", getReplacement(message, channel, context));
    }
    default TagPriority getPriority()
    {
        return TagPriority.NORMAL;
    }
}
