package net.cyberkitsune.prefixchat.channels;

import net.cyberkitsune.prefixchat.KitsuneChat;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Collection;

public class StaffChannel implements KitsuneChannel {
    @Override
    public Collection<? extends Player> getRecipients(String message, AsyncPlayerChatEvent evt) {
        ArrayList<Player> currentAdmins = new ArrayList<>();
        for(Player p : KitsuneChat.getInstance().getServer().getOnlinePlayers())
        {
            if (p.hasPermission("kitsunechat.staffchat"))
                currentAdmins.add(p);
        }
        return currentAdmins;
    }

    @Override
    public String getChannelName() {
        return "staff";
    }

    @Override
    public void postMessage(String message, AsyncPlayerChatEvent evt) {

    }

    @Override
    public boolean willCancel() {
        return true;
    }
}
