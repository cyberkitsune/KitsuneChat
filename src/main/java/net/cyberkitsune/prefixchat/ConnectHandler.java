package net.cyberkitsune.prefixchat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectHandler implements Listener {
	public ConnectHandler() {}
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent evt) {
		if(ChatParties.getInstance().isInAParty(evt.getPlayer())) {
			ChatParties.getInstance().leaveParty(evt.getPlayer(), true);
		}
		
	}
	
	@EventHandler
	public void onConnect(PlayerJoinEvent evt) {
		if(!KitsuneChatUserData.getInstance().getPartyDataForUser(evt.getPlayer()).equalsIgnoreCase(""))  {
			ChatParties.getInstance().changeParty(evt.getPlayer(), KitsuneChatUserData.getInstance().getPartyDataForUser(evt.getPlayer()));
		}
		if(KitsuneChatUserData.getInstance().getUserChannel(evt.getPlayer()) == null) {
			KitsuneChatUserData.getInstance().setUserChannel(evt.getPlayer(), KitsuneChat.getInstance().getConfig().getString("channels.default"));
		}
	}

}
