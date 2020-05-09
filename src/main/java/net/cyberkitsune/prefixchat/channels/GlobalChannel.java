package net.cyberkitsune.prefixchat.channels;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
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
    public String getChannelName() {
        return "global";
    }

    @Override
    public String formatMessage(String message, AsyncPlayerChatEvent context, boolean emote) {
        shouldCancel = emote;
        return KitsuneChannel.super.formatMessage(message, context, emote);
    }

    @Override
    public void postMessage(String message, AsyncPlayerChatEvent context) {
        // It's bungee time
        if(KitsuneChat.getInstance().getConfig().getBoolean("channels.global.bungee-send"))
        {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(KitsuneChat.getInstance().getConfig().getString("bungee-id"));
            out.writeUTF(message);
            try
            {
                context.getPlayer().sendPluginMessage(KitsuneChat.getInstance(), "bungee:kitsunechat", out.toByteArray());
            }
            catch (Exception e)
            {
                KitsuneChat.getInstance().mcLog.warning("[KitsuneChat] Unable to dispatch bungee message!");
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean willCancel() {
        // I'm not entirely sure this is thread safe.
        return shouldCancel;
    }
}
