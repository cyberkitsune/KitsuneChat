package net.cyberkitsune.prefixchat.channels;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.cyberkitsune.prefixchat.KitsuneChat;
import net.cyberkitsune.prefixchat.MessageTagger;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        // It's bungee time -- Use bungee specific formatting
        if(KitsuneChat.getInstance().getConfig().getBoolean("channels.global.bungee.send"))
        {
            // Format the message...
            String bungee_formatted = "";
            if(emote)
            {
                bungee_formatted = MessageTagger.getInstance().formatMessage(Objects.requireNonNull(KitsuneChat.getInstance().getConfig()
                        .getString("channels.global.bungee.meformat")), message, this, context);
            }
            else
            {
                bungee_formatted = MessageTagger.getInstance().formatMessage(Objects.requireNonNull(KitsuneChat.getInstance().getConfig()
                        .getString("channels.global.bungee.sayformat")), message, this, context);
            }
            String sender_server;
            sender_server = KitsuneChat.getInstance().getConfig().getString("bungee-tag");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(stream);
            if (sender_server == null)
            {
                KitsuneChat.getInstance().mcLog.warning("[KitsuneChat] Server tag not set! Not sending bungee message...");
                return KitsuneChannel.super.formatMessage(message, context, emote);
            }

            try {
                out.writeUTF(sender_server);
                out.writeUTF(bungee_formatted);
            }
            catch (IOException e)
            {
                KitsuneChat.getInstance().mcLog.warning("[KitsuneChat] Error preparing bungee message!");
                e.printStackTrace();
                return KitsuneChannel.super.formatMessage(message, context, emote);
            }
            try
            {
                context.getPlayer().sendPluginMessage(KitsuneChat.getInstance(), "bungee:kitsunechat", stream.toByteArray());
            }
            catch (Exception e)
            {
                KitsuneChat.getInstance().mcLog.warning("[KitsuneChat] Unable to dispatch bungee message!");
                e.printStackTrace();
            }
        }
        return KitsuneChannel.super.formatMessage(message, context, emote);
    }

    @Override
    public void postMessage(String message, AsyncPlayerChatEvent context) {

    }

    @Override
    public boolean willCancel() {
        // I'm not entirely sure this is thread safe.
        return shouldCancel;
    }
}
