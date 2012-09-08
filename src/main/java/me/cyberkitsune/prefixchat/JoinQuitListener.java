package me.cyberkitsune.prefixchat;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener{

	private KitsuneChat plugin;
	public JoinQuitListener(KitsuneChat plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent evt) {
		if(plugin.vaultEnabled) {
			String playername = ChatColor.translateAlternateColorCodes('&', plugin.vaultChat.getPlayerPrefix(evt.getPlayer())+evt.getPlayer().getDisplayName()+plugin.vaultChat.getPlayerSuffix(evt.getPlayer()));
			evt.setJoinMessage(ChatColor.YELLOW+playername+ChatColor.YELLOW+" has joined the game.");
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent evt) {
		if(plugin.vaultEnabled) {
			String playername = ChatColor.translateAlternateColorCodes('&', plugin.vaultChat.getPlayerPrefix(evt.getPlayer())+evt.getPlayer().getDisplayName()+plugin.vaultChat.getPlayerSuffix(evt.getPlayer()));
			evt.setQuitMessage(ChatColor.YELLOW+playername+ChatColor.YELLOW+" has quit the game.");
		}
	}
	
	
}
