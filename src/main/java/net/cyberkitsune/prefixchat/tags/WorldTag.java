package net.cyberkitsune.prefixchat.tags;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import net.cyberkitsune.prefixchat.KitsuneChat;
import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class WorldTag implements ChatTag{
    @Override
    public String getPlaceholder() {
        return "world";
    }

    @Override
    public String getReplacement(String message, KitsuneChannel channel, AsyncPlayerChatEvent context) {
        if (KitsuneChat.getInstance().multiVerse)
        {
            MultiverseCore mvPlug = KitsuneChat.getInstance().multiversePlugin;
            MultiverseWorld mvWorld = mvPlug.getMVWorldManager().getMVWorld(context.getPlayer().getWorld().getName());
            if(mvWorld != null)
                return mvWorld.getColoredWorldString();
        }
        return context.getPlayer().getWorld().getName();
    }
}
