package net.cyberkitsune.prefixchat.channels;

import net.cyberkitsune.prefixchat.ChatParties;
import net.cyberkitsune.prefixchat.LocalizedString;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;

public class PartyChannel implements KitsuneChannel {
    @Override
    public Collection<? extends Player> getRecipients(AsyncPlayerChatEvent evt) {
        return ChatParties.getInstance().getPartyMembers(ChatParties.getInstance().getPartyName(evt.getPlayer()));
    }

    @Override
    public boolean onMessage(String message, AsyncPlayerChatEvent context) {
        boolean permissionsCheck = KitsuneChannel.super.onMessage(message, context);
        if (!permissionsCheck)
            return false;
        if(!ChatParties.getInstance().isInAParty(context.getPlayer()))
        {
            context.getPlayer().sendMessage(LocalizedString.get("channel.party.notin", context.getPlayer().getLocale()));
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
    public boolean hasPermission(Player p) {
        return p.hasPermission("kitsunechat.partychat");
    }

    @Override
    public boolean willCancel() {
        return true;
    }
}
