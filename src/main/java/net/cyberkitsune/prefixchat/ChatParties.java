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
		if(name == "" || name == null) {
			if(isInAParty(target)) {
				partyData.remove(target);
				return;
			}
			return;
		}
		if(!partyData.containsKey(target))
		{
			
			notifyParty(name, ChatColor.YELLOW+"[KitsuneChat] "+target.getDisplayName()+" has joined the party "+name);
			partyData.put(target, name);
			Set<Player> channelPeople = getKeysByValue(partyData, name);
			String memberList = "";
			for(Player plr : channelPeople)
			{
				memberList = memberList + plr.getDisplayName()+", ";
			}
			memberList = memberList.substring(0, memberList.length() - 2)+".";
			target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] "+channelPeople.size()+((channelPeople.size() == 1) ? " person " : " people ")+"in the party.");
			target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] They are: "+memberList);
		} else {
			target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] Changing party from "+partyData.get(target)+" to "+name);
			partyData.remove(target);
			notifyParty(partyData.get(target), ChatColor.YELLOW+"[KitsuneChat] "+target.getDisplayName()+" has left "+partyData.get(target));
			notifyParty(name, ChatColor.YELLOW+"[KitsuneChat] "+target.getDisplayName()+" has joined "+name);
			partyData.put(target, name);
			Set<Player> channelPeople = getKeysByValue(partyData, name);
			String memberList = "";
			for(Player plr : channelPeople)
			{
				memberList = memberList + plr.getDisplayName()+", ";
			}
			memberList = memberList.substring(0, memberList.length() - 2)+".";
			target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] "+channelPeople.size()+((channelPeople.size() == 1) ? " person " : " people ")+"in the party.");
			target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] They are: "+memberList+".");
		}
		KitsuneChatUserData.getInstance().setUserParty(target, name);
		
	}
	
	public void notifyParty(String party, String message) {
		Set<Player> channelMembers = getKeysByValue(partyData, party);
		for(Player plr : channelMembers) {
			plr.sendMessage(message);
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
			partyData.remove(target);
			KitsuneChatUserData.getInstance().setUserParty(target, "");
		}
		notifyParty(party, ChatColor.YELLOW+"[KitsuneChat] "+target.getDisplayName()+" has left "+party+".");
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
