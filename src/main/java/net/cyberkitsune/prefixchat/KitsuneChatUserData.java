package net.cyberkitsune.prefixchat;

import java.io.File;
import java.util.HashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class KitsuneChatUserData {

	private FileConfiguration userData = null;
	private File dataFile;

	private static KitsuneChatUserData instance = null;

	public KitsuneChatUserData() {
		dataFile = new File(KitsuneChat.getInstance().getDataFolder(), "users.yml");
	}

	public static KitsuneChatUserData getInstance() {
		if (instance == null)
			instance = new KitsuneChatUserData();

		return instance;
	}

	public void loadUserData() {
		if(!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (Exception e) {
				KitsuneChat.getInstance().mcLog.severe("[KitsuneChat] Unable to create user data file!");
				e.printStackTrace();
			}
		} else {
			userData = YamlConfiguration.loadConfiguration(dataFile);
		}
	}
	
	public String getPartyDataForUser(Player user) {
		loadUserData();
		String party = "";
		
		if(userData.getString(user.getName()+".party") != null) {
			party = userData.getString(user.getName()+".party");
		} else {
			party = "";
		}
		
		
		return party;
	}
	
	public void savePartyData(HashMap<Player, String> partyData) {
		for(Player plr : partyData.keySet()) {
			userData.set(plr.getName()+".party", partyData.get(plr));
		}
		try {
			userData.save(dataFile);
		} catch (Exception ex) {
			KitsuneChat.getInstance().mcLog.severe("[KitsuneChat] Unable to save user data!!");
			ex.printStackTrace();
		}
	}
	
	public void setUserParty(Player target, String party) {
		userData.set(target.getName()+".party", party);
		try {
			userData.save(dataFile);
		} catch (Exception ex) {
			KitsuneChat.getInstance().mcLog.severe("[KitsuneChat] Unable to save user data!!");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Changes a user's channel to the specified prefix.
	 * @param target Player that is going to be changed.
	 * @param channel Prefix of the channel that needs changing to.
	 */
	public void setUserChannel(Player target, String channel) {
		userData.set(target.getName()+".channel", channel);
		try {
			userData.save(dataFile);
		} catch (Exception ex) {
			KitsuneChat.getInstance().mcLog.severe("[KitsuneChat] Unable to save user data file!");
		}
	}
	
	public String getUserChannel(Player target) {
		return userData.getString(target.getName()+".channel");
	}
	
	
	
}
