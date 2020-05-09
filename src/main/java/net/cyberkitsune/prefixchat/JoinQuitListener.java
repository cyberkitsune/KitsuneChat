package net.cyberkitsune.prefixchat;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener{

	public JoinQuitListener() {
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent evt) {
		if(KitsuneChat.getInstance().vaultEnabled) {
			String playername = ChatColor.translateAlternateColorCodes('&', KitsuneChat.getInstance().vaultChat.getPlayerPrefix(evt.getPlayer())+evt.getPlayer().getDisplayName()+KitsuneChat.getInstance().vaultChat.getPlayerSuffix(evt.getPlayer()));
			evt.setJoinMessage(ChatColor.YELLOW+playername+ChatColor.YELLOW+" has joined the game.");
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent evt) {
		if(KitsuneChat.getInstance().vaultEnabled) {
			String playername = ChatColor.translateAlternateColorCodes('&', KitsuneChat.getInstance().vaultChat.getPlayerPrefix(evt.getPlayer())+evt.getPlayer().getDisplayName()+KitsuneChat.getInstance().vaultChat.getPlayerSuffix(evt.getPlayer()));
			evt.setQuitMessage(ChatColor.YELLOW+playername+ChatColor.YELLOW+" has quit the game.");
		}
	}
	
	
}
