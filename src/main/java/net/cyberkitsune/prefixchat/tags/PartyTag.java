package net.cyberkitsune.prefixchat.tags;

import net.cyberkitsune.prefixchat.ChatParties;
import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PartyTag implements ChatTag {
    @Override
    public String getPlaceholder() {
        return "party";
    }

    @Override
    public String getReplacement(String message, KitsuneChannel channel, AsyncPlayerChatEvent context, String placeholder) {
        return ChatParties.getInstance().isInAParty(context.getPlayer()) ?
                ChatParties.getInstance().getPartyName(context.getPlayer()) : "";
    }
}
