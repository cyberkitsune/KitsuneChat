package net.cyberkitsune.prefixchat;

import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
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
	
	public KitsuneChatCommand()
	{}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof Player) {
			if (command.getName().equalsIgnoreCase("kc")) {
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("party") || args[0].equalsIgnoreCase("p")) {
							if (args.length > 1) {
								if (args[1].equalsIgnoreCase("leave")) {
									ChatParties.getInstance().leaveParty((Player) sender, false);
									return true;
								} else if (args[1].equalsIgnoreCase("list")) {
									if (ChatParties.getInstance().isInAParty((Player) sender)) {
										String playerlist = "";
										int playerCount = 0;
										for (Player plr : ChatParties.getInstance().getPartyMembers(ChatParties.getInstance().getPartyName((Player) sender))) {
											playerlist = playerlist + plr.getDisplayName() + ", ";
											playerCount++;
										}
										playerlist = playerlist.substring(0, playerlist.length() - 2) + ".";
										sender.sendMessage(ChatColor.YELLOW + "[KitsuneChat] " + playerCount + ((playerCount == 1) ? " person " : " people ") + "in the party.");
										sender.sendMessage(ChatColor.YELLOW + "[KitsuneChat] They are: " + playerlist + ".");
										return true;
									} else {
										sender.sendMessage(ChatColor.RED + "[KitsuneChat] You are not in a party!");
										return true;
									}
								} else if (args[1].equalsIgnoreCase("join")) {
									if (args.length < 3) {
										sender.sendMessage(ChatColor.RED + "[KitsuneChat] Usage: /kc party join <party_name>");
										return true;
									}
									ChatParties.getInstance().changeParty((Player) sender, args[2].toLowerCase());
									KitsuneChatUserData.getInstance().setUserChannel((Player) sender, KitsuneChat.getInstance().getConfig().getString("channels.party.prefix"));
									sender.sendMessage(ChatColor.YELLOW + "[KitsuneChat] You are now talking in party chat.");
								} else if (args[1].equalsIgnoreCase("invite")) {
									if (args.length > 2) {
										if (ChatParties.getInstance().isInAParty((Player) sender)) {
											Player target = KitsuneChat.getInstance().getServer().getPlayer(args[1]);
											if (target != null) {
												target.sendMessage(ChatColor.GREEN + "[KitsuneChat] " + sender.getName() + " has invited you to a party! Type /kc p " + ChatParties.getInstance().getPartyName((Player) sender) + " to join!");
												ChatParties.getInstance().notifyParty(ChatParties.getInstance().getPartyName((Player) sender), ChatColor.GREEN + "[KitsuneChat] " + sender.getName() + " invited " + target.getDisplayName() + ChatColor.GREEN + " to the party.");
											} else {
												sender.sendMessage(ChatColor.RED + "[KitsuneChat] That player does not exist!");
											}
										} else {
											sender.sendMessage(ChatColor.RED + "[KitsuneChat] You aren't in a party!");
										}
									} else {
										sender.sendMessage(ChatColor.RED + "[KitsuneChat] You didn't specify a player!");
									}
								} else {
									sender.sendMessage(ChatColor.RED + "[KitsuneChat] Usage: /kc party (join|leave|invite|list)");
								}
							} else {
							sender.sendMessage(ChatColor.RED + "[KitsuneChat] Usage: /kc party (join|leave|invite|list)");
						}
						} else if (args[0].equalsIgnoreCase("?")) {
							printHelp((Player) sender);
						} else if (args[0].equalsIgnoreCase("null")) { // Dummy command for the /me full stop.
							return true;
						} else {
							for (KitsuneChannel channel : KitsuneChat.getInstance().channels.values()) {
								if (args[0].equalsIgnoreCase(channel.getPrefix())) {
									if (!channel.hasPermission((Player)sender))
									{
										sender.sendMessage(ChatColor.RED + "[KitsuneChat] You do not have permission to use the " + channel.getChannelName() + " channel.");
										return true;
									}
									KitsuneChatUserData.getInstance().setUserChannel((Player) sender, channel.getPrefix());
									sender.sendMessage(ChatColor.YELLOW + "[KitsuneChat] Default chat now set to " + channel.getChannelName());
									return true;
								}
							}
							sender.sendMessage(ChatColor.RED + "[KitsuneChat] Unknown or missing command. See /kc ? for help.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "[KitsuneChat] Unknown or missing command. See /kc ? for help.");
					}
				} else return !command.getName().equalsIgnoreCase("me");
				return true;
			}
			return false;
		}
	
	public void printHelp(Player target) {
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] KitsuneChat - Channeled Chat System Version "+KitsuneChat.getInstance().getDescription().getVersion()+" by CyberKitsune.");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] KitsuneChat Commands: ");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc ? - This command. ");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc party join <name> - Join a party with name <name>. ");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc party list - Lists who is in your party.");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc party leave - Leaves your current party. ");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc party invite <player> - Invites <player> to your current party.");
		for(KitsuneChannel channel : KitsuneChat.getInstance().channels.values()) {
			if (channel.hasPermission(target))
				target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc "+channel.getPrefix()+" - Change default channel to "+channel.getChannelName()+".");
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
		ArrayList<String> possibleCompletions = new ArrayList<>(Arrays.asList("?", "party"));
		possibleCompletions.addAll(KitsuneChat.getInstance().prefixes);

		final List<String> completions = new ArrayList<>();
		int checkIndex = 0;
		if(strings[0].equals("party"))
		{
			possibleCompletions = new ArrayList<>(Arrays.asList("join", "list", "leave", "invite"));
			if(strings.length > 1)
			{
				checkIndex = 1;
				if(strings[1].equals("join"))
					return Collections.singletonList("[<party_name>]");
			}

		} else if(strings.length > 1)
		{
			possibleCompletions = new ArrayList<>();
		}


		StringUtil.copyPartialMatches(strings[checkIndex], possibleCompletions, completions);

		Collections.sort(completions);

		return completions;
	}
}
