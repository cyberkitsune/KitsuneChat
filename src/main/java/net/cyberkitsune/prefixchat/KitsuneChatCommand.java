package net.cyberkitsune.prefixchat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KitsuneChatCommand implements CommandExecutor, TabCompleter {
	
	public KitsuneChatCommand(){}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			if (command.getName().equalsIgnoreCase("kc")) {
				if (args.length > 0) {
					if(args[0].equalsIgnoreCase("party") || args[0].equalsIgnoreCase("p")) {
						if(args.length > 1) {
							if(args[1].equalsIgnoreCase("leave"))
							{
								ChatParties.getInstance().leaveParty((Player) sender, false);
								return true;
							} else if(args[1].equalsIgnoreCase("list")) {
								if(ChatParties.getInstance().isInAParty((Player) sender)) {
									String playerlist = "";
									int playerCount = 0;
									for(Player plr : ChatParties.getInstance().getPartyMembers(ChatParties.getInstance().getPartyName((Player) sender))) {
										playerlist = playerlist + plr.getDisplayName()+", ";
										playerCount++;
									}
									playerlist = playerlist.substring(0, playerlist.length() - 2)+".";
									sender.sendMessage(ChatColor.YELLOW+"[KitsuneChat] "+playerCount+((playerCount == 1) ? " person " : " people ")+"in the party.");
									sender.sendMessage(ChatColor.YELLOW+"[KitsuneChat] They are: "+playerlist+".");
									return true;
								} else {
									sender.sendMessage(ChatColor.RED+"[KitsuneChat] You are not in a party!");
									return true;
								}
							}
							ChatParties.getInstance().changeParty((Player) sender, args[1].toLowerCase());
							KitsuneChatUserData.getInstance().setUserChannel((Player) sender, KitsuneChat.getInstance().getConfig().getString("channels.party.prefix"));
							sender.sendMessage(ChatColor.YELLOW+"[KitsuneChat] You are now talking in party chat.");
						} else {
							sender.sendMessage(ChatColor.RED+"[KitsuneChat] Please choose a party name!");
						}
					} else if(args[0].equalsIgnoreCase("leaveparty")) {
						ChatParties.getInstance().leaveParty((Player) sender, false);
					} else if(args[0].equalsIgnoreCase("?")) {
						printHelp((Player) sender);
					} else if(args[0].equalsIgnoreCase("invite")) {
						if(args.length > 1) {
							if(ChatParties.getInstance().isInAParty((Player)sender)) {
								Player target = KitsuneChat.getInstance().getServer().getPlayer(args[1]);
								if(target != null) {
									target.sendMessage(ChatColor.GREEN+"[KitsuneChat] "+sender.getName()+" has invited you to a party! Type /kc p "+ChatParties.getInstance().getPartyName((Player) sender)+" to join!");
									ChatParties.getInstance().notifyParty(ChatParties.getInstance().getPartyName((Player) sender), ChatColor.GREEN+"[KitsuneChat] "+sender.getName()+" invited "+target.getDisplayName()+ChatColor.GREEN+" to the party.");
								} else {
									sender.sendMessage(ChatColor.RED+"[KitsuneChat] That player does not exist!");
								}
							} else {
								sender.sendMessage(ChatColor.RED+"[KitsuneChat] You aren't in a party!");
							}
						} else {
							sender.sendMessage(ChatColor.RED+"[KitsuneChat] You didn't specify a player!");
						}
					} else if(args[0].equalsIgnoreCase("null")) { // Dummy command for the /me full stop.
						return true;
					} else {
						for(String str : KitsuneChat.getInstance().prefixes) {
								if(args[0].equalsIgnoreCase(str)) {
									if(sender.hasPermission("kitsunechat.nodefault."+KitsuneChatUtils.getInstance().getChannelName(str, false)) && !KitsuneChatUtils.getInstance().getChannelName(str, false).equalsIgnoreCase("local")) //Failsafe
									{
										sender.sendMessage(ChatColor.RED+"[KitsuneChat] You do not have permission to use "+KitsuneChatUtils.getInstance().getChannelName(str, false)+" as your default channel.");
										sender.sendMessage(ChatColor.RED+"[KitsuneChat] Try prefixing your message with "+KitsuneChatUtils.getInstance().getChannelName(str, true)+" instead.");
										return true;
									}
								KitsuneChatUserData.getInstance().setUserChannel((Player) sender, str);
								sender.sendMessage(ChatColor.YELLOW+"[KitsuneChat] Default chat now set to "+KitsuneChatUtils.getInstance().getChannelName(str, false));
								return true;
								}
						}
						sender.sendMessage(ChatColor.RED+"[KitsuneChat] Unknown or missing command. See /kc ? for help.");
					}
				} else {
					sender.sendMessage(ChatColor.RED+"[KitsuneChat] Unknown or missing command. See /kc ? for help.");
				}
			} else if(command.getName().equalsIgnoreCase("me")) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	public void printHelp(Player target) {
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] KitsuneChat - Channeled Chat System Version "+KitsuneChat.getInstance().getDescription().getVersion()+" by CyberKitsune.");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] KitsuneChat Commands: ");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc ? - This command. ");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc party (or p) <name> - Join a party with name <name>. ");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc party list - Lists who is in your party.");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc leaveparty - Leaves your current party. ");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc invite <player> - Invites <player> to your current party.");
		for(String str : KitsuneChat.getInstance().prefixes) {
			target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc "+str+" - Change default channel to "+KitsuneChatUtils.getInstance().getChannelName(str, false)+".");
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
		ArrayList<String> possibleCompletions = new ArrayList<>(Arrays.asList("?", "party", "leaveparty", "invite"));
		possibleCompletions.addAll(KitsuneChat.getInstance().prefixes);

		final List<String> completions = new ArrayList<>();
		int checkIndex = 0;
		if(strings[0].equals("party"))
		{
			possibleCompletions = new ArrayList<>(Arrays.asList("list", "leave", "invite"));
			if(strings.length > 1)
				checkIndex = 1;
		} else if(strings.length > 1)
		{
			possibleCompletions = new ArrayList<>();
		}


		StringUtil.copyPartialMatches(strings[checkIndex], possibleCompletions, completions);

		Collections.sort(completions);

		return completions;
	}
}
