package com.cyberkitsune.prefixchat;

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
			if(message.startsWith(str+plugin.getConfigVal("emote.prefix"))) {
				emote = true;
			}
		}
		if(message.startsWith((String) plugin.getConfigVal("emote.prefix"))) {
			emote = true;
		}
		if(emote) {
			message = message.replaceFirst("\\"+plugin.getConfigVal("emote.prefix"), "");
		}

		if (evt.getMessage().startsWith(
				(String) plugin.getConfigVal("global.prefix"))) {
			Set<Player> everybody = evt.getRecipients();
			for (Player plr : everybody) {
				plr.sendMessage(util.formatChatPrefixes(message, (String) plugin
						.getConfigVal(emote ? "global.meformat" : "global.sayformat"), evt));
			}
		} else if (evt.getMessage().startsWith(
				(String) plugin.getConfigVal("world.prefix"))) {
			List<Player> worldPlayers = evt.getPlayer().getWorld().getPlayers();
			for (Player plr : worldPlayers) {
				plr.sendMessage(util.formatChatPrefixes(message, (String) plugin
						.getConfigVal(emote ? "world.meformat" : "world.sayformat"), evt));
			}
		} else if (evt.getMessage().startsWith(
				(String) plugin.getConfigVal("admin.prefix"))) {
			if (plugin.hasAdminPermission(evt.getPlayer())) {
				for (Player plr : plugin.getServer().getOnlinePlayers()) {
					if (plugin.hasAdminPermission(plr)) {
						plr.sendMessage(util.formatChatPrefixes(message, (String) plugin
								.getConfigVal(emote ? "admin.meformat" : "admin.sayformat"), evt));
					}
				}
			} else {
				evt.getPlayer()
						.sendMessage(
								ChatColor.RED
										+ "You do not have permissions to use admin chat.");
			}
		} else if (evt.getMessage().startsWith(
				(String) plugin.getConfigVal("party.prefix"))) {
			if (plugin.party.isInAParty(evt.getPlayer())) {
				Set<Player> channelPlayers = plugin.party
						.getPartyMembers(plugin.party.getPartyName(evt
								.getPlayer()));
				for (Player plr : channelPlayers) {
					plr.sendMessage(util.formatChatPrefixes(message, (String) plugin
							.getConfigVal(emote ? "party.meformat" : "party.sayformat"), evt));
				}
			} else {
				evt.getPlayer()
						.sendMessage(
								ChatColor.YELLOW
										+ "[KitsuneChat] You are not currently in a channel.");
			}

		} else if ((evt.getMessage().startsWith((String) plugin.getConfigVal(
				"local.prefix")))) {
			Set<Player> local = KitsuneChatUtils.getNearbyPlayers(plugin
					.getConfig().getInt("local.radius"), evt.getPlayer());
			for (Player plr : local) {
				plr.sendMessage(util.formatChatPrefixes(message, (String) plugin
						.getConfigVal(emote ? "local.meformat" : "local.sayformat"), evt));
			}

		} else {
			boolean pass = false;
			for(String str : plugin.prefixes ) {
				if(plugin.dataFile.getUserChannel(evt.getPlayer()).equals(str)) {
					pass = true;
				}
			}
			if(pass) {
				if(!emote) {
				evt.setMessage(plugin.dataFile.getUserChannel(evt.getPlayer())+message);
				} else {
					evt.setMessage(plugin.dataFile.getUserChannel(evt.getPlayer())+plugin.getConfigVal("emote.prefix")+message);
				}
			} else {
				plugin.dataFile.setUserChannel(evt.getPlayer(), (String) plugin.getConfigVal("local.prefix"));
				
			}
			playerChat(evt);
			return;
		}
		
		
		plugin.mcLog.info(evt.getPlayer().getName()+" : "+message);

	}

}
