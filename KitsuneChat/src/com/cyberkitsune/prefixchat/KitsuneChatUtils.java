package com.cyberkitsune.prefixchat;


import java.util.HashSet;
import java.util.Set;


import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public class KitsuneChatUtils {
	KitsuneChat plugin;
	public KitsuneChatUtils(KitsuneChat plugin) {
		this.plugin = plugin;
	}
	
	public static Set<Player> getNearbyPlayers(int radius, Player target) {
		Set<Player> nearbyPlayers = new HashSet<Player>();
		nearbyPlayers.add(target);
		
		for(Entity ent : target.getNearbyEntities(radius, radius, radius))
		{
			if(ent instanceof Player) {
				nearbyPlayers.add((Player) ent);
			}
		}
		
		return nearbyPlayers;
		
	}
	
	public static String colorizeString(String target) {
		String colorized = new String();
		colorized = ChatColor.translateAlternateColorCodes('&', target);
		
		return colorized;
	}
	
	public String formatChatPrefixes(String target, String formatString, PlayerChatEvent context) {
		String output ="";
		output = formatString.replaceAll("{sender}", context.getPlayer().getDisplayName());
		output = output.replaceAll("{world}", context.getPlayer().getWorld().getName());
		output = output.replaceAll("{channel}", getChannelName(target, false));
		output = output.replaceAll("{prefix}", getChannelName(target, true));
		output = output.replaceAll("{party}", (plugin.chans.isInAChannel(context.getPlayer()) ? plugin.chans.getChannelName(context.getPlayer()) : ""));
		output = output.replaceAll("{message}", target);
		output = colorizeString(output);
		return output;
	}
	
	public String getChannelName(String chname, boolean displayPrefix) {
		if(!displayPrefix) {
			if(chname.startsWith(plugin.getConfig().getString("global.prefix"))) {
				return "global";
			} else if(chname.startsWith(plugin.getConfig().getString("admin.prefix"))) {
				return "admin";
			} else if(chname.startsWith(plugin.getConfig().getString("world.prefix"))) {
				return "world";
			} else if(chname.startsWith(plugin.getConfig().getString("party.prefix"))) {
				return "party";
			} else if(chname.startsWith(plugin.getConfig().getString("local.prefix"))) {
				return "local";
			} else {
				return "local";
			}
		} else {
			if(chname.startsWith(plugin.getConfig().getString("global.prefix"))) {
				return plugin.getConfig().getString("global.prefix");
			} else if(chname.startsWith(plugin.getConfig().getString("admin.prefix"))) {
				return plugin.getConfig().getString("admin.prefix");
			} else if(chname.startsWith(plugin.getConfig().getString("world.prefix"))) {
				return plugin.getConfig().getString("world.prefix");
			} else if(chname.startsWith(plugin.getConfig().getString("party.prefix"))) {
				return plugin.getConfig().getString("party.prefix");
			} else if(chname.startsWith(plugin.getConfig().getString("local.prefix"))) {
				return plugin.getConfig().getString("local.prefix");
			} else {
				return plugin.getConfig().getString("local.prefix");
			}
		}
		
	}
}
