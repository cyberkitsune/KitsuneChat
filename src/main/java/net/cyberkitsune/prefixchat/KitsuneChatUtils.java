package net.cyberkitsune.prefixchat;


import java.util.HashSet;
import java.util.Set;

import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.google.common.base.Joiner;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class KitsuneChatUtils {
	private static KitsuneChatUtils instance;
	public KitsuneChatUtils() {
	}

	public static KitsuneChatUtils getInstance()
	{
		if (instance == null)
			instance = new KitsuneChatUtils();
		return instance;
	}
	
	public Set<Player> getNearbyPlayers(int radius, Player target) {
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
	
	public Set<Player> getNearbyPlayers(int radius, Player target, AsyncPlayerChatEvent event) {
		// This function SHOULD be thread-safe.
		Set<Player> nearbyPlayers = new HashSet<>();
		nearbyPlayers.add(target);
		
		for(Entity ent : event.getRecipients())
		{
			if (ent.equals(target)) continue;
			if (!(ent.getWorld().equals(target.getWorld()))) continue;
			if(target.getLocation().distance(ent.getLocation()) <= radius) {
				nearbyPlayers.add((Player) ent);
			}
		}
		
		return nearbyPlayers;
		
	}
	public String colorizeString(String target) {
		String colorized = new String();
		colorized = ChatColor.translateAlternateColorCodes('&', target);
		
		return colorized;
	}

    public void chatWatcher(AsyncPlayerChatEvent event) {
		KitsuneChat.getInstance().mcLog.info(
				"KitsuneChat "+
				event.getEventName()+" "+
				event.getPlayer()+" "+
				Joiner.on(" ").join(event.getRecipients())+" "+
				event.getFormat()+" "+
				event.getMessage()
				);
	}

	public Player getPlayerFromDisplay(String displayName)
	{
		Player out = null;
		for(Player p : KitsuneChat.getInstance().getServer().getOnlinePlayers())
		{
			if(ChatColor.stripColor(p.getDisplayName()).equalsIgnoreCase(displayName))
				out = p;
		}

		return out;
	}
}
