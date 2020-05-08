package net.cyberkitsune.prefixchat;


import java.util.HashSet;
import java.util.Set;

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
	
	public String stripPrefixes(String target) {
		return (String) target.replaceFirst("\\\\"+getChannelName(target, true), "");
	}
	
	public String formatChatPrefixes(String target, String formatString, AsyncPlayerChatEvent context) {
		String output="";
		if(KitsuneChat.getInstance().vaultEnabled) {
			output = formatString.replace("{sender}",
					KitsuneChat.getInstance().vaultChat.getPlayerPrefix(
							context.getPlayer()) + context.getPlayer().getDisplayName() +
							KitsuneChat.getInstance().vaultChat.getPlayerSuffix(context.getPlayer()));
		} else {
			output = formatString.replace("{sender}", context.getPlayer().getDisplayName());
		}
		if(KitsuneChat.getInstance().multiVerse) {
			MultiverseCore mvPlug = KitsuneChat.getInstance().multiversePlugin;
			MultiverseWorld mvWorld = mvPlug.getMVWorldManager().getMVWorld(context.getPlayer().getWorld().getName());
			if(mvWorld == null) {
				output = output.replace("{world}", context.getPlayer().getWorld().getName());	
			} else {
				output = output.replace("{world}", mvWorld.getColoredWorldString());
			}
		} else {
			output = output.replace("{world}", context.getPlayer().getWorld().getName());	
		}
		output = output.replace("{channel}", getChannelName(target, false));
		output = output.replace("{prefix}", getChannelName(target, true));
		output = output.replace("{party}", (ChatParties.getInstance().isInAParty(context.getPlayer()) ?
				ChatParties.getInstance().getPartyName(context.getPlayer()) : ""));
		target = target.replaceFirst("\\\\"+getChannelName(target, true), "");
		//target = target.replaceAll("\\$", "\\\\$"); //Friggen dollar signs.
		if(context.isCancelled()) {
			output = output.replace("{message}", target);
		} else {
			output = output.replace("{message}", "%2\\$s");
		}
		output = colorizeString(output);
		return output;
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
	public String getChannelName(String target, boolean displayPrefix) {
		if(!displayPrefix) {
			if(target.startsWith(KitsuneChat.getInstance().getConfig().getString("global.prefix"))) {
				return "global";
			} else if(target.startsWith(KitsuneChat.getInstance().getConfig().getString("admin.prefix"))) {
				return "admin";
			} else if(target.startsWith(KitsuneChat.getInstance().getConfig().getString("staff.prefix"))) {
				return "staff";
			} else if(target.startsWith(KitsuneChat.getInstance().getConfig().getString("world.prefix"))) {
				return "world";
			} else if(target.startsWith(KitsuneChat.getInstance().getConfig().getString("party.prefix"))) {
				return "party";
			} else if(target.startsWith(KitsuneChat.getInstance().getConfig().getString("local.prefix"))) {
				return "local";
			} else {
				return "local";
			}
		} else {
			if(target.startsWith(KitsuneChat.getInstance().getConfig().getString("global.prefix"))) {
				return KitsuneChat.getInstance().getConfig().getString("global.prefix");
			} else if(target.startsWith(KitsuneChat.getInstance().getConfig().getString("admin.prefix"))) {
				return KitsuneChat.getInstance().getConfig().getString("admin.prefix");
			} else if(target.startsWith(KitsuneChat.getInstance().getConfig().getString("staff.prefix"))) {
				return KitsuneChat.getInstance().getConfig().getString("staff.prefix");
			} else if(target.startsWith(KitsuneChat.getInstance().getConfig().getString("world.prefix"))) {
				return KitsuneChat.getInstance().getConfig().getString("world.prefix");
			} else if(target.startsWith(KitsuneChat.getInstance().getConfig().getString("party.prefix"))) {
				return KitsuneChat.getInstance().getConfig().getString("party.prefix");
			} else if(target.startsWith(KitsuneChat.getInstance().getConfig().getString("local.prefix"))) {
				return KitsuneChat.getInstance().getConfig().getString("local.prefix");
			} else {
				return KitsuneChat.getInstance().getConfig().getString("local.prefix");
			}
		}
		
	}
}
