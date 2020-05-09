package net.cyberkitsune.prefixchat.tags;

import com.sun.org.apache.bcel.internal.generic.LASTORE;
import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface ChatTag {
    String getPlaceholder();
    String getReplacement(String message, KitsuneChannel channel, AsyncPlayerChatEvent context);
}
