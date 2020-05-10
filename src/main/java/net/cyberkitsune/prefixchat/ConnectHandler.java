package net.cyberkitsune.prefixchat;

import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import org.bukkit.ChatColor;
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

		// A friendly reminder....
		String currentChannel = KitsuneChatUserData.getInstance().getUserChannel(evt.getPlayer());
		if(!currentChannel.equals(KitsuneChat.getInstance().getConfig().getString("channels.default")))
		{
			KitsuneChannel channel = KitsuneChat.getInstance().getChannelByPrefix(currentChannel);
			if (channel != null)
			{
				evt.getPlayer().sendMessage(ChatColor.GRAY+String.format("[KitsuneChat] Current talking in: %s. Run /kc %s to return to default.",
						channel.getChannelName(), KitsuneChat.getInstance().getConfig().getString("channels.default")));

			}
		}
	}

}
