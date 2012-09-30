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
			if(sender.getName().equalsIgnoreCase(args[0])) {
				sender.sendMessage(ChatColor.GRAY+"Talking to yourself is a sign of insanity.");
				return true;
			}
			boolean toconsole = false;
			Player target = null;
			if(args[0].equalsIgnoreCase("CONSOLE")) {
				toconsole = true;
			} else {
			target = plugin.getServer().getPlayer(args[0]);
			}
			boolean fromconsole = false;
			if(!(sender instanceof Player)) {
				fromconsole = true;
			}
			if((target == null || !target.isOnline()) && !toconsole) {
				sender.sendMessage(ChatColor.RED+"[KitsuneChat] I can't find "+args[0]+"! D:");
				return false;
			}
			String[] message = Arrays.copyOfRange(args, 1, args.length);
			String joined = Joiner.on(" ").join(message);
			if(toconsole) {
				plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"["+((Player) sender).getDisplayName()+" -> Me] "+joined));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"[Me -> CONSOLE] "+joined));
			} else if(fromconsole){
				target.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"[CONSOLE -> Me] "+joined));
				plugin.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"[ Me -> "+((Player) target).getDisplayName()+"] "+joined));
			} else {
				target.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"["+((Player) sender).getDisplayName()+" -> Me] "+joined));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"[Me -> "+target.getDisplayName()+"] "+joined));
			}
			
			if(!toconsole && !fromconsole) {
				plugin.mcLog.info(String.format("[%s -> %s] %s", ((Player) sender).getDisplayName(), target.getDisplayName(), joined));
			}

			if(!toconsole && !fromconsole) {
				replies.put(sender.getName(), target.getName());
				replies.put(target.getName(), sender.getName());
			}
			return true;
		} else if(cmd.getName().equalsIgnoreCase("r")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED+"[KitsuneChat] Do not run /r as console! :C");
				return false;
			}
			boolean logtoconsole = true;
			if(!replies.containsKey(sender.getName())) {
				sender.sendMessage(ChatColor.RED+"[KitsuneChat] You have nobody to reply to.");
				return false;
			}
			if(args.length <= 0) {
				sender.sendMessage(ChatColor.RED+"[KitsuneChat] Usage: /r [message]");
				return false;
			}

			Player target;
			target = plugin.getServer().getPlayer(replies.get(sender.getName()));
			if(target == null) {
				sender.sendMessage(ChatColor.RED+"[KitsuneChat] I can't find"+replies.get(sender.getName())+"! D:");
				return false;
			}
			String joined = Joiner.on(" ").join(args);
			if(!(sender instanceof Player)) {
				target.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"[CONSOLE -> Me] "+joined));
			} else {
				target.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"["+((Player) sender).getDisplayName()+" -> Me] "+joined));
			}
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"[Me -> "+target.getDisplayName()+"] "+joined));
			if(logtoconsole) {
				plugin.mcLog.info(String.format("[%s -> %s] %s", ((Player) sender).getDisplayName(), target.getDisplayName(), joined));
			}
		}
		return false;
	}

}