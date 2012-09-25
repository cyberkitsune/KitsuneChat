package me.cyberkitsune.prefixchat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

public class UserMessaging implements CommandExecutor {
	
	private Map<String, String> replies;
	private KitsuneChat plugin;
	
	public UserMessaging(KitsuneChat plugin) {
		this.plugin = plugin;
		replies = new HashMap<String, String>();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("msg")) {
			if(args.length < 2) {
				sender.sendMessage(ChatColor.RED+"[KitsuneChat] Invalid format, use: /msg [Player] [Message]");
				return false;
			}
			Player target = plugin.getServer().getPlayer(args[0]);
			if(target == null || !target.isOnline()) {
				sender.sendMessage(ChatColor.RED+"[KitsuneChat] I can't find "+args[0]+"! D:");
				return false;
			}
			String[] message = Arrays.copyOfRange(args, 1, args.length);
			String joined = Joiner.on(" ").join(message);
			target.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"["+((Player) sender).getDisplayName()+" -> Me] "+joined));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"[Me -> "+target.getDisplayName()+"] "+joined));
			
			plugin.mcLog.info(String.format("[%s -> %s] %s", ((Player) sender).getDisplayName(), target.getDisplayName(), joined));

			replies.put(sender.getName(), target.getName());
			replies.put(target.getName(), sender.getName());
			return true;
		} else if(cmd.getName().equalsIgnoreCase("r")) {
			if(!replies.containsKey(sender.getName())) {
				sender.sendMessage(ChatColor.RED+"[KitsuneChat] You have nobody to reply to.");
				return false;
			}
			if(args.length <= 0) {
				sender.sendMessage(ChatColor.RED+"[KitsuneChat] Usage: /r [message]");
				return false;
			}
			Player target = plugin.getServer().getPlayer(replies.get(sender.getName()));
			if(target == null) {
				sender.sendMessage(ChatColor.RED+"[KitsuneChat] I can't find"+replies.get(sender.getName())+"! D:");
				return false;
			}
			String joined = Joiner.on(" ").join(args);
			target.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"["+((Player) sender).getDisplayName()+" -> Me] "+joined));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"[Me -> "+target.getDisplayName()+"] "+joined));
			
			plugin.mcLog.info(String.format("[%s -> %s] %s", ((Player) sender).getDisplayName(), target.getDisplayName(), joined));
		}
		return false;
	}

}
