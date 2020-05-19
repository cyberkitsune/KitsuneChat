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

		KitsuneChat.getInstance().getServer().getScheduler().scheduleSyncDelayedTask( KitsuneChat.getInstance(), new Runnable()
		{
			public void run()
			{
				KitsuneChat.getInstance().mcLog.info(String.format("[KitsuneChat] %s's locale is %s",evt.getPlayer().getDisplayName(),
						evt.getPlayer().getLocale()));
			}
		}, 100 );

		// A friendly reminder....
		String currentChannel = KitsuneChatUserData.getInstance().getUserChannel(evt.getPlayer());
		if(!currentChannel.equals(KitsuneChat.getInstance().getConfig().getString("channels.default")))
		{
			KitsuneChannel channel = KitsuneChat.getInstance().getChannelByPrefix(currentChannel);
			if (channel != null)
			{
				evt.getPlayer().sendMessage(String.format(LocalizedString.get("defwarning", evt.getPlayer().getLocale()),
						channel.getChannelName(), KitsuneChat.getInstance().getConfig().getString("channels.default")));

			}
		}
	}

}
