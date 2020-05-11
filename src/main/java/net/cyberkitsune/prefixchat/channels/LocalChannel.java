package net.cyberkitsune.prefixchat.channels;

import net.cyberkitsune.prefixchat.KitsuneChat;
import net.cyberkitsune.prefixchat.KitsuneChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;

public class LocalChannel implements KitsuneChannel {
    @Override
    public Collection<? extends Player> getRecipients(AsyncPlayerChatEvent evt) {
        return KitsuneChatUtils.getInstance().getNearbyPlayers(KitsuneChat.getInstance().getConfig().getInt("channels.local.radius"), evt.getPlayer(), evt);
    }

    @Override
    public String getChannelName() {
        return "local";
    }

    @Override
    public void postMessage(String message, AsyncPlayerChatEvent evt) {
        Collection<? extends Player> sentPlayers = getRecipients(evt);
        if (sentPlayers.size() <= 1 && KitsuneChat.getInstance().getConfig().getBoolean("channels.local.warnifalone"))
        {
            evt.getPlayer().sendMessage(ChatColor.GRAY+"(Nobody can hear you, try talking in a different channel. Use /kc ? for help.)");
        }
        for(Player p : KitsuneChat.getInstance().getServer().getOnlinePlayers())
        {
            if (p.hasPermission("kitsunechat.ignoreradius") && !sentPlayers.contains(p))
                p.sendMessage(message);
        }
    }

    @Override
    public boolean willCancel() {
        return true;
    }
}
