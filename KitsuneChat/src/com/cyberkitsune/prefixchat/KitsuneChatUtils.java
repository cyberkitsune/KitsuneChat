package com.cyberkitsune.prefixchat;


import java.util.HashSet;
import java.util.Set;


import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class KitsuneChatUtils {
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
}
