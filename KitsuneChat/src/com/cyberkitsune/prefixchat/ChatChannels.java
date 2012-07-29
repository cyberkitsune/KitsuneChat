package com.cyberkitsune.prefixchat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatChannels {

	public HashMap<Player, String> channelData = new HashMap<Player, String>();
	
	public void changeChannel(Player target, String name) {
		if(!channelData.containsKey(target))
		{
			channelData.put(target, name);
			notifyChannel(name, ChatColor.YELLOW+"[KitsuneChat] "+target.getDisplayName()+" has joined the channel "+name);
			Set<Player> channelPeople = getKeysByValue(channelData, name);
			String memeberList = "";
			for(Player plr : channelPeople)
			{
				memeberList = memeberList + plr.getDisplayName()+", ";
			}
			target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] "+channelPeople.size()+((channelPeople.size() == 1) ? " person " : " people ")+"in the channel.");
			target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] They are: "+memeberList);
		} else {
			target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] Changing channel from "+channelData.get(target)+" to "+name);
			notifyChannel(channelData.get(target), ChatColor.YELLOW+"[KitsuneChat] "+target.getDisplayName()+" has left "+channelData.get(target));
			channelData.remove(target);
			channelData.put(target, name);
			notifyChannel(name, ChatColor.YELLOW+"[KitsuneChat] "+target.getDisplayName()+" has joined "+name);
			Set<Player> channelPeople = getKeysByValue(channelData, name);
			String memeberList = "";
			for(Player plr : channelPeople)
			{
				memeberList = memeberList + plr.getDisplayName()+", ";
			}
			target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] "+channelPeople.size()+((channelPeople.size() == 1) ? " person " : " people ")+"in the channel.");
			target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] They are: "+memeberList+".");
		}
	}
	
	public void notifyChannel(String channel, String message) {
		Set<Player> channelMembers = getKeysByValue(channelData, channel);
		for(Player plr : channelMembers) {
			plr.sendMessage(message);
		}
	}
	
	public Set<Player> getChannelMembers(String channel) {
		return getKeysByValue(channelData, channel);
	}
	
	public String getChannelName(Player target) {
		return channelData.get(target);
	}
	
	public boolean isInAChannel(Player target) {
		return channelData.containsKey(target);
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
