package net.cyberkitsune.prefixchat;

import java.util.*;

import net.cyberkitsune.prefixchat.channels.GlobalChannel;
import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {

	private HashMap<Player, String> bufs;

	public ChatListener() {
		this.bufs = new HashMap<Player, String>();
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void playerEmote(PlayerCommandPreprocessEvent evt) {
		if (!evt.getMessage().toLowerCase().startsWith("/me "))
			return;

		String buf = new String(KitsuneChat.getInstance().getConfig().getString("emote.prefix") + evt.getMessage().substring(4));
		AsyncPlayerChatEvent newevt =
				new AsyncPlayerChatEvent(false, evt.getPlayer(), buf,
						new HashSet<Player>(KitsuneChat.getInstance().getServer().getOnlinePlayers()));

		KitsuneChat.getInstance().getServer().getPluginManager().callEvent(newevt);

		evt.setCancelled(true);
	}

	// LOW priority makes this event fire before NORMAL priority, so that we can properly rewrite event messages..
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void playerChat(AsyncPlayerChatEvent evt) {
		evt.setCancelled(true);

		if (evt.getMessage().endsWith("--")) {
			if (bufs.get(evt.getPlayer()) == null) {
				bufs.put(evt.getPlayer(), evt.getMessage().substring(0, evt.getMessage().length() - 2));
			} else {
				bufs.put(evt.getPlayer(),
						bufs.get(evt.getPlayer()) + " " + evt.getMessage().substring(0, evt.getMessage().length() - 2));
			}
			evt.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC
					+ "(Your message ended with --, will append next message to it.)");
			return;
		} else {
			if (bufs.get(evt.getPlayer()) != null)
				evt.setMessage(bufs.get(evt.getPlayer()) + " " + evt.getMessage());
			bufs.put(evt.getPlayer(), null);
		}
		String message = KitsuneChatUtils.getInstance().colorizeString(evt.getMessage());
		boolean emote = false;
		String channel_prefix = "";
		// Try and see if our message prefix means to use a channel
		for (String prefix : KitsuneChat.getInstance().channels.keySet()) {
			if (message.startsWith(prefix))
				channel_prefix = prefix;
		}

		// If no prefix was found, defer to default, else strip prefix from message.
		if (channel_prefix.isEmpty()) {
			// Do something here if the user does not have a channel set (or maybe in datafile?)
			channel_prefix = KitsuneChatUserData.getInstance().getUserChannel(evt.getPlayer());
		} else {
			message = message.replaceFirst(channel_prefix, "");
		}

		// Now, check if we're an emote message. If yes, flag and strip. (Should this logic go here?)
		if (message.startsWith(Objects.requireNonNull(KitsuneChat.getInstance().getConfig().getString("emote.prefix")))) {
			emote = true;
			message = message.replaceFirst(String.format("\\%s", KitsuneChat.getInstance().getConfig().getString("emote.prefix")), "");
		}


		// Finally, process the message. (Should probably throw an exception if the channel isn't there anymore.)
		KitsuneChannel target_channel = KitsuneChat.getInstance().channels.get(channel_prefix);

		if (target_channel == null)
		{
			// Try and move the user into the default
			String default_prefix = KitsuneChat.getInstance().getConfig().getString("channels.default");
			KitsuneChannel default_channel = KitsuneChat.getInstance().channels.get(default_prefix);
			if (default_channel == null)
			{
				KitsuneChat.getInstance().mcLog.severe("[KitsuneChat] Default channel doesn't actually exist! Check your config!");
				evt.getPlayer().sendMessage(ChatColor.RED + "[KitsuneChat] You tried to talk in a channel that doesn't exist, and the default doesn't exist either." +
						" This is a configuration issue, ask your server admin.");
				evt.setCancelled(true);
				return;
			}
			KitsuneChatUserData.getInstance().setUserChannel(evt.getPlayer(), default_channel.getPrefix());
			target_channel = default_channel;
		}

		if (target_channel.onMessage(message, evt)) {
			message = target_channel.formatMessage(message, evt, emote);
			if (target_channel.willCancel()) {
				for (Player p : target_channel.getRecipients(message, evt))
					p.sendMessage(message);
				evt.setCancelled(true);
			} else {
				// Send in a vanilla way, so we can pass the message to other plugins.
				evt.setFormat(message.replace("%", "%%"));   // Not sure why, old KC had the replace.
				evt.setMessage(message);                                        // Ensure compatibility.
				evt.setCancelled(false);                                        // Shouldn't cancel
			}

			target_channel.postMessage(message, evt);
		} else {
			// OnMessage should handle any permissions / other warnings
			evt.setCancelled(true);

		}
	}
}

