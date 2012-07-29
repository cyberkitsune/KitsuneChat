package com.cyberkitsune.prefixchat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectHandler implements Listener {
	private KitsuneChat plugin;
	public ConnectHandler(KitsuneChat plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent evt) {
		if(plugin.party.isInAParty(evt.getPlayer())) {
			plugin.party.leaveParty(evt.getPlayer());
		}
		
	}

}
