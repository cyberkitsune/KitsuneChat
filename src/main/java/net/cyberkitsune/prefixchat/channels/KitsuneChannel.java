package net.cyberkitsune.prefixchat.channels;

import net.cyberkitsune.prefixchat.KitsuneChat;
import net.cyberkitsune.prefixchat.KitsuneChatUserData;
import net.cyberkitsune.prefixchat.LocalizedString;
import net.cyberkitsune.prefixchat.MessageTagger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;
import java.util.Objects;

/**
 * A interface representing a unique chat "channel" in KitsuneChat
 */
public interface KitsuneChannel {
    /**
     * Retrieve the players a message sent to the channel should go to
     * @param evt Chat event for context (eg, sending Player)
     * @return A collection of Players to send the message to
     */
    Collection<? extends Player> getRecipients(AsyncPlayerChatEvent evt);

    /**
     * Return the name of this channel. Should be lowercase and typically corrosponds to the entry for the channel in config.yml
     * eg, channels.global in the config means this should return "global"
     * @return String containing the channel message
     */
    String getChannelName();

    /**
     * Do any post-processing of a channel message in this routine
     * @param message Fully formatted message that was sent
     * @param evt Chat event the message was sent in
     */
    void postMessage(String message, AsyncPlayerChatEvent evt);

    /**
     * Check weather or not this channel will cancel a PlayerChatEvent. (eg, everything except global chat)
     * @return true if the channel will cancel, false otherwise.
     */
    boolean willCancel();

    /**
     * Called on sending a message. Unless you need to do something specific this shouldn't need overloading.
     * @param message Fully-formatted message to send
     * @param context Chat event for context
     * @return true if the message was sent successfully.
     */
    default boolean onMessage(String message, AsyncPlayerChatEvent context)
    {
        boolean hasPerms = hasPermission(context.getPlayer());
        if (!hasPerms)
        {
            // Step 1, see if the user was trying to talk here by default, move em.
            if(KitsuneChatUserData.getInstance().getUserChannel(context.getPlayer()).equals(getPrefix()))
            {
                context.getPlayer().sendMessage(String.format(LocalizedString.get("channel.nodefperms",
                        context.getPlayer().getLocale()), getChannelName()));

                KitsuneChatUserData.getInstance().setUserChannel(context.getPlayer(),
                        KitsuneChat.getInstance().getConfig().getString("channels.default"));
            }
            else
            {
                context.getPlayer().sendMessage(String.format(LocalizedString.get("channel.noperms",
                        context.getPlayer().getLocale()), getChannelName()));
            }
        }
        return hasPerms;
    }
    default String getPrefix()
    {
        return KitsuneChat.getInstance().getConfig().getString(String.format("channels.%s.prefix", getChannelName()));
    }

    /***
     * Formats a string for a given chat channel. String should not contain any prefixes, but can contain placeholders.
     * @param message unformatted string to format.
     * @param context chat event used for context
     * @param emote true if the message is a "/me" style message
     * @return formatted message, ready to send to players
     */
    default String formatMessage(String message, AsyncPlayerChatEvent context, boolean emote)
    {
        String formatted;
        if(emote)
        {
            formatted = MessageTagger.getInstance().formatMessage(Objects.requireNonNull(KitsuneChat.getInstance().getConfig()
                    .getString("channels." + getChannelName() + ".meformat")), message, this, context);
        }
        else
        {
            formatted = MessageTagger.getInstance().formatMessage(Objects.requireNonNull(KitsuneChat.getInstance().getConfig()
                    .getString("channels." + getChannelName() + ".sayformat")), message, this, context);
        }
        return formatted;
    }

    /***
     * Checks weather or not a player has permission to talk in this channel.
     * @param p Player to check perms on
     * @return true if a player can use this channel
     */
    default boolean hasPermission(Player p)
    {
        return  !(p.hasPermission("kitsunechat.no." + getChannelName()));
    }

}
