package net.cyberkitsune.prefixchat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatParties {
	
	private static ChatParties instance;
	public ChatParties() {
	}

	public static ChatParties getInstance() {
		if (instance == null)
			instance = new ChatParties();

		return instance;
	}

	public HashMap<Player, String> partyData = new HashMap<Player, String>();
	
	public void changeParty(Player target, String name) {
		if(name.equals("")) {
			if(isInAParty(target)) {
				partyData.remove(target);
				return;
			}
			return;
		}
		String locale = target.getLocale();
		if(!partyData.containsKey(target))
		{
			notifyPartyLocalized(name, "joinedparty", target.getDisplayName(), name);
		} else {

			target.sendMessage(String.format(LocalizedString.get("changepartyfrom", locale), partyData.get(target), name));
			notifyPartyLocalized(partyData.get(target), "hasleft", target.getDisplayName(), partyData.get(target));
			notifyPartyLocalized(name, "hasjoined", target.getDisplayName(), name);
			partyData.remove(target);

		}
		partyData.put(target, name);
		KitsuneChatUserData.getInstance().setUserParty(target, name);
		
	}
	
	public void notifyParty(String party, String message) {
		Set<Player> channelMembers = getKeysByValue(partyData, party);
		for(Player plr : channelMembers) {
			plr.sendMessage(message);
		}
	}

	public void notifyPartyLocalized(String party, String key, Object... args) {
		Set<Player> channelMembers = getKeysByValue(partyData, party);
		for(Player plr : channelMembers) {
			plr.sendMessage(String.format(LocalizedString.get(key, plr.getLocale()), args));
		}
	}
	
	public Set<Player> getPartyMembers(String party) {
		return getKeysByValue(partyData, party);
	}
	
	public String getPartyName(Player target) {
		return partyData.get(target);
	}
	
	public boolean isInAParty(Player target) {
		return partyData.containsKey(target);
	}
	
	public void leaveParty(Player target, boolean disconnect) {
		String party = getPartyName(target);

		if(disconnect) {
			KitsuneChatUserData.getInstance().setUserParty(target, getPartyName(target));
			partyData.remove(target);
			
		} else {
			notifyPartyLocalized(party, "hasleft", target.getDisplayName(), party);
			partyData.remove(target);
			KitsuneChatUserData.getInstance().setUserParty(target, "");
		}
	}
	
	public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
	     Set<T> keys = new HashSet<T>();
	     for (Entry<T, E> entry : map.entrySet()) {
	         if (value.equals(entry.getValue())) {
	             keys.add(entry.getKey());
	         }
	     }
	     return keys;
	}
	
}
