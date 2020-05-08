package net.cyberkitsune.prefixchat.channels;

import net.cyberkitsune.prefixchat.KitsuneChat;
import net.cyberkitsune.prefixchat.KitsuneChatUserData;
import net.cyberkitsune.prefixchat.KitsuneChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;
import java.util.List;

//if (evt.getMessage().startsWith(plugin.getConfig().getString("channels.global.prefix"))) {
//			//plugin.mcLog.info(plugin.util.formatChatPrefixes(message, plugin.getConfig().getString(emote ? "global.meformat" : "global.sayformat"), evt));
//			if (emote) { // This here is not the ideal way to handle it, but its the way that works.
//				for(Player plr : plugin.getServer().getOnlinePlayers()) {
//					plr.sendMessage(plugin.util.formatChatPrefixes(message, plugin.getConfig().getString(emote ? "channels.global.meformat" : "channels.global.sayformat"), evt));
//				}
//				evt.setCancelled(true); // Nobody else should see this as a chat event. EVER.
//				return ; // Now GTFO my listener.
//
//			}
public interface KitsuneChannel {
    Collection<? extends Player> getRecipients(String message, AsyncPlayerChatEvent evt);
    String getChannelName();
    void postMessage(String message, AsyncPlayerChatEvent evt);
    boolean willCancel();
    default boolean onMessage(String message, AsyncPlayerChatEvent context)
    {
        boolean hasPerms = hasPermission(context.getPlayer());
        if (!hasPerms)
        {
            // Step 1, see if the user was trying to talk here by default, move em.
            if(KitsuneChatUserData.getInstance().getUserChannel(context.getPlayer()).equals(getPrefix()))
            {
                context.getPlayer().sendMessage(
                        ChatColor.GRAY+"(You do not have permission to talk in " + getChannelName() + " by default. Changing you to default chat.)");
                KitsuneChatUserData.getInstance().setUserChannel(context.getPlayer(),
                        KitsuneChat.getInstance().getConfig().getString("channels.default"));
            }
            else
            {
                context.getPlayer().sendMessage(ChatColor.RED+ "You do not have permissions to use "+ getChannelName() +" chat.");
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
            formatted = KitsuneChatUtils.getInstance().formatChatPrefixes(message, KitsuneChat.getInstance().getConfig()
                    .getString("channels."+getChannelName()+".meformat"), context);
        }
        else
        {
            formatted = KitsuneChatUtils.getInstance().formatChatPrefixes(message, KitsuneChat.getInstance().getConfig()
                    .getString("channels."+getChannelName()+".sayformat"), context);
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
