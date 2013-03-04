package me.cyberkitsune.prefixchat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NicknameChanger implements CommandExecutor{
	
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(!command.getName().equals("nick")) {
			return false;
		}
		
		if(!(sender instanceof Player)) {
			return false;
		}
		
		if(!(args.length > 0)) {
			return false;
		}
		
		Player plr = (Player) sender;
		if(!plr.hasPermission("kitsunechat.nick")) {
			plr.sendMessage(ChatColor.RED+"[KitsuneChat] You do not have permission to change your nickname.");
			return false;
		}
		plr.setDisplayName(args[1]);
		
		return true;
	}

}
