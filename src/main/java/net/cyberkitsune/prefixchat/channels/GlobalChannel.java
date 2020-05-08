package net.cyberkitsune.prefixchat.channels;

import net.cyberkitsune.prefixchat.KitsuneChat;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;
import java.util.List;

public class GlobalChannel implements KitsuneChannel {

    private boolean shouldCancel = false;
    @Override
    public Collection<? extends Player> getRecipients(String message, AsyncPlayerChatEvent evt) {
        return KitsuneChat.getInstance().getServer().getOnlinePlayers();
    }

    @Override
    public boolean onMessage(String message, AsyncPlayerChatEvent context) {
        return false;
    }

    @Override
    public String getChannelName() {
        return "global";
    }

    @Override
    public String formatMessage(String message, AsyncPlayerChatEvent context, boolean emote) {
        shouldCancel = emote;
        return KitsuneChannel.super.formatMessage(message, context, emote);
    }

    @Override
    public void postMessage(String message, AsyncPlayerChatEvent evt) {
    }

    @Override
    public boolean willCancel() {
        // I'm not entirely sure this is thread safe.
        return shouldCancel;
    }
}
