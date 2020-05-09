package net.cyberkitsune.prefixchat.channels;

import net.cyberkitsune.prefixchat.ChatParties;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;

public class PartyChannel implements KitsuneChannel {
    @Override
    public Collection<? extends Player> getRecipients(String message, AsyncPlayerChatEvent evt) {
        return ChatParties.getInstance().getPartyMembers(ChatParties.getInstance().getPartyName(evt.getPlayer()));
    }

    @Override
    public boolean onMessage(String message, AsyncPlayerChatEvent context) {
        boolean permissionsCheck = KitsuneChannel.super.onMessage(message, context);
        if (!permissionsCheck)
            return false;
        if(!ChatParties.getInstance().isInAParty(context.getPlayer()))
        {
            context.getPlayer().sendMessage(ChatColor.YELLOW + "[KitsuneChat] You are not currently in a channel.");
            return false;
        }

        return true;
    }

    @Override
    public String getChannelName() {
        return "party";
    }

    @Override
    public void postMessage(String message, AsyncPlayerChatEvent evt) {

    }

    @Override
    public boolean willCancel() {
        return true;
    }
}
