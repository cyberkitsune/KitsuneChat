package net.cyberkitsune.bungeeproxy;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.stream.Collectors;

public class KCProxy extends Plugin implements Listener {
    @Override
    public void onEnable() {
        getProxy().registerChannel("bungee:kitsunechat");
        getProxy().getPluginManager().registerListener(this, this);
        getLogger().info("[KitsuneChat] KitsuneChat Proxy module enabled!");
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equalsIgnoreCase("bungee:kitsunechat"))
            return;


        ByteArrayInputStream stream = new ByteArrayInputStream(event.getData());
        DataInputStream in = new DataInputStream(stream);
        String sender_server = "";
        String sent_message = "";
        try
        {
            sender_server = in.readUTF();
            sent_message = in.readUTF();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        if(!sender_server.isEmpty() && !sent_message.isEmpty())
            echo_message(sender_server, sent_message);
    }

    private void echo_message(String sender_server, String sent_message)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        try {
            out.writeUTF(sender_server);
            out.writeUTF(sent_message);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        for (ServerInfo si : getProxy().getServers().values().stream().filter(si ->!si.getPlayers().isEmpty()).collect(Collectors.toList()))
        {
            si.sendData("bungee:kitsunechat", stream.toByteArray());
        }
    }
}
