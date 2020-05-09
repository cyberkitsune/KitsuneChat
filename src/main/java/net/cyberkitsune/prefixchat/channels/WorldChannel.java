package net.cyberkitsune.prefixchat.channels;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;

public class WorldChannel implements KitsuneChannel {
    @Override
    public Collection<? extends Player> getRecipients(String message, AsyncPlayerChatEvent evt) {
        return evt.getPlayer().getWorld().getPlayers();
    }

    @Override
    public String getChannelName() {
        return "world";
    }

    @Override
    public void postMessage(String message, AsyncPlayerChatEvent evt) {

    }

    @Override
    public boolean willCancel() {
        return true;
    }
}
