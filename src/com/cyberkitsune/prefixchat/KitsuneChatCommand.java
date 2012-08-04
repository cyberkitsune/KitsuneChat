package com.cyberkitsune.prefixchat;

import java.util.Arrays;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import com.google.common.base.Joiner;

public class KitsuneChatCommand implements CommandExecutor {

	private KitsuneChat plugin;

	public KitsuneChatCommand(KitsuneChat plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			if (command.getName().equalsIgnoreCase("kc")) {
				if (args.length > 0) {
					if(args[0].equalsIgnoreCase("party")) {
						if(args.length > 1) {
							if(args[1].equalsIgnoreCase("leave"))
							{
								plugin.party.leaveParty((Player) sender, false);
								return true;
							}
							plugin.party.changeParty((Player) sender, args[1]);
						} else {
							sender.sendMessage(ChatColor.RED+"[KitsuneChat] Please choose a party name!");
						}
					} else if(args[0].equalsIgnoreCase("leaveparty")) {
						plugin.party.leaveParty((Player) sender, false);
					} else if(args[0].equalsIgnoreCase("?")) {
						printHelp((Player) sender);
					} else {
						for(String str : plugin.prefixes) {
								if(args[0].equalsIgnoreCase(str)) {
								plugin.dataFile.setUserChannel((Player) sender, str);
								sender.sendMessage(ChatColor.YELLOW+"[KitsuneChat] Default chat now set to "+new KitsuneChatUtils(plugin).getChannelName(str, false));
								return true;
								}
						}
						sender.sendMessage(ChatColor.RED+"[KitsuneChat] Unknown or missing command. See /kc ? for help.");
					}
				} else {
					sender.sendMessage(ChatColor.RED+"[KitsuneChat] Unknown or missing command. See /kc ? for help.");
				}
			} else if(command.getName().equalsIgnoreCase("me")) {
				@SuppressWarnings("unchecked")
				AsyncPlayerChatEvent evt = new AsyncPlayerChatEvent(false, (Player)sender, plugin.getConfig().getString("emote.prefix")+Joiner.on(" ").join(args), (Set<Player>) Arrays.asList(plugin.getServer().getOnlinePlayers()));
				plugin.getServer().getPluginManager().callEvent(evt);
			}
			return true;
		}
		return false;
	}
	
	public void printHelp(Player target) {
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] KitsuneChat - Channeled Chat System Version "+plugin.getDescription().getVersion());
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] KitsuneChat Commands: ");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc ? - This command. ");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc party <name> - Join a party with name <name>. ");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc leaveparty - Leaves your current party. ");
		for(String str : plugin.prefixes) {
			target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc "+str+" - Change default channel to "+new KitsuneChatUtils(plugin).getChannelName(str, false)+".");
		}
	}

}
