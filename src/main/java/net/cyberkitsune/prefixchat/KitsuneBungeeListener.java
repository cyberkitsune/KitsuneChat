package net.cyberkitsune.prefixchat;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class KitsuneBungeeListener implements PluginMessageListener, Listener {
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if(!channel.equals("bungee:kitsunechat"))
            return;

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        String sender_server = "";
        String sender_message = "";
        try
        {
            sender_server = in.readUTF();
            sender_message = in.readUTF();
        }
        catch (IOException e)
        {
            KitsuneChat.getInstance().mcLog.warning("[KitsuneChat] Unable to receive bungee message!");
            e.printStackTrace();
            return;
        }

        //TODO This maybe should be more versatile
        if (!sender_server.equals(KitsuneChat.getInstance().getConfig().getString("bungee-id")))
        {
            for(Player p : KitsuneChat.getInstance().getServer().getOnlinePlayers())
                p.sendMessage(sender_message);
        }
    }
}
