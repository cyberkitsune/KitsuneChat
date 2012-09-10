package me.cyberkitsune.prefixchat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UserMessaging implements CommandExecutor {
	
	private KitsuneChat plugin;
	public UserMessaging(KitsuneChat plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		return false;
	}

}
