package com.cyberkitsune.prefixchat;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

	private KitsuneChat plugin;
	private KitsuneChatUtils util;

	public ChatListener(KitsuneChat plugin) {
		this.plugin = plugin;
		util = new KitsuneChatUtils(plugin);
	}

	@EventHandler
	public void playerChat(AsyncPlayerChatEvent evt) {
		evt.setCancelled(true);
		String message = KitsuneChatUtils.colorizeString(evt.getMessage());
		boolean emote = false;
		for(String str : plugin.prefixes) {
			if(message.startsWith(str+plugin.getConfig().getString("emote.prefix"))) {
				emote = true;
			}
		}
		if(message.startsWith(plugin.getConfig().getString("emote.prefix"))) {
			emote = true;
		}
		if(emote) {
			message = message.replaceFirst("\\"+plugin.getConfig().getString("emote.prefix"), "");
		}

		if (evt.getMessage().startsWith(
				plugin.getConfig().getString("global.prefix"))) {
			Set<Player> everybody = evt.getRecipients();
			for (Player plr : everybody) {
				plr.sendMessage(util.formatChatPrefixes(message, plugin
						.getConfig().getString(emote ? "global.meformat" : "global.sayformat"), evt));
			}
		} else if (evt.getMessage().startsWith(
				plugin.getConfig().getString("world.prefix"))) {
			List<Player> worldPlayers = evt.getPlayer().getWorld().getPlayers();
			for (Player plr : worldPlayers) {
				plr.sendMessage(util.formatChatPrefixes(message, plugin
						.getConfig().getString(emote ? "world.meformat" : "world.sayformat"), evt));
			}
		} else if (evt.getMessage().startsWith(
				plugin.getConfig().getString("admin.prefix"))) {
			if (evt.getPlayer().hasPermission("kitsunechat.adminchat")) {
				for (Player plr : plugin.getServer().getOnlinePlayers()) {
					if (plr.hasPermission("kitsunechat.adminchat")) {
						plr.sendMessage(util.formatChatPrefixes(message, plugin
								.getConfig().getString(emote ? "admin.meformat" : "admin.sayformat"), evt));
					}
				}
			} else {
				evt.getPlayer()
						.sendMessage(
								ChatColor.RED
										+ "You do not have permissions to use admin chat.");
			}
		} else if (evt.getMessage().startsWith(
				plugin.getConfig().getString("party.prefix"))) {
			if (plugin.party.isInAParty(evt.getPlayer())) {
				Set<Player> channelPlayers = plugin.party
						.getPartyMembers(plugin.party.getPartyName(evt
								.getPlayer()));
				for (Player plr : channelPlayers) {
					plr.sendMessage(util.formatChatPrefixes(message, plugin
							.getConfig().getString(emote ? "party.meformat" : "party.sayformat"), evt));
				}
			} else {
				evt.getPlayer()
						.sendMessage(
								ChatColor.YELLOW
										+ "[KitsuneChat] You are not currently in a channel.");
			}

		} else if ((evt.getMessage().startsWith(plugin.getConfig().getString(
				"local.prefix")))) {
			Set<Player> local = KitsuneChatUtils.getNearbyPlayers(plugin
					.getConfig().getInt("local.radius"), evt.getPlayer());
			for (Player plr : local) {
				plr.sendMessage(util.formatChatPrefixes(message, plugin
						.getConfig().getString(emote ? "local.meformat" : "local.sayformat"), evt));
			}

		} else {
			List<String> prefixes = Arrays.asList(plugin.getConfig().getString("global.prefix"), plugin.getConfig().getString("local.prefix"), plugin.getConfig().getString("admin.prefix"), plugin.getConfig().getString("party.prefix"), plugin.getConfig().getString("world.prefix"));
			boolean pass = false;
			for(String str : prefixes ) {
				if(plugin.dataFile.getUserChannel(evt.getPlayer()).equals(str)) {
					pass = true;
				}
			}
			if(pass) {
				if(!emote) {
				evt.setMessage(plugin.dataFile.getUserChannel(evt.getPlayer())+message);
				} else {
					evt.setMessage(plugin.dataFile.getUserChannel(evt.getPlayer())+plugin.getConfig().getString("emote.prefix")+message);
				}
			} else {
				plugin.dataFile.setUserChannel(evt.getPlayer(), plugin.getConfig().getString("local.prefix"));
				
			}
			playerChat(evt);
			return;
		}
		
		
		plugin.mcLog.info(evt.getPlayer().getName()+" : "+message);

	}

}
