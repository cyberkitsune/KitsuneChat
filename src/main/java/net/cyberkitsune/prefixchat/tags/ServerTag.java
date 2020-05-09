package net.cyberkitsune.prefixchat.tags;

import net.cyberkitsune.prefixchat.KitsuneChat;
import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ServerTag implements ChatTag {
    @Override
    public String getPlaceholder() {
        return "bungee-tag,server";
    }

    @Override
    public String getReplacement(String message, KitsuneChannel channel, AsyncPlayerChatEvent context) {
        return KitsuneChat.getInstance().getConfig().getString("bungee-tag");
    }
}
