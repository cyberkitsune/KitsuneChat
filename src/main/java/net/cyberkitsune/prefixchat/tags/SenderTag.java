package net.cyberkitsune.prefixchat.tags;

import net.cyberkitsune.prefixchat.KitsuneChat;
import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SenderTag implements ChatTag {
    @Override
    public String getPlaceholder() {
        return "sender";
    }

    @Override
    public String getReplacement(String message, KitsuneChannel channel, AsyncPlayerChatEvent context) {
        if (KitsuneChat.getInstance().vaultEnabled)
        {
            return KitsuneChat.getInstance().vaultChat.getPlayerPrefix(context.getPlayer()) +
                    context.getPlayer().getDisplayName() + KitsuneChat.getInstance().vaultChat.getPlayerSuffix(context.getPlayer());
        }
        else
        {
            return context.getPlayer().getDisplayName();
        }
    }
}
