package net.cyberkitsune.prefixchat.tags;

import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public interface ChatTag {
    /***
     * Check what the placeholders supported by this tag are
     * @return a string of possible placeholders to be handled by this ChatTag, comma seperated.
     */
    String getPlaceholder();

    /***
     * Get the string to replace the tag placeholder with
     * @param message Full message sent by player (not the format string)
     * @param channel Channel message is being sent in
     * @param context ChatEvent the message is being sent in
     * @param placeholder Placeholder being evaluated
     * @return what the placeholder should resolve to
     */
    String getReplacement(String message, KitsuneChannel channel, AsyncPlayerChatEvent context, String placeholder);
}
