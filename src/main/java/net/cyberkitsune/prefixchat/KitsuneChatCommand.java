package net.cyberkitsune.prefixchat;

import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class KitsuneChatCommand implements CommandExecutor, TabCompleter {
	
	public KitsuneChatCommand() {}

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
									sender.sendMessage(ChatColor.YELLOW + "[KitsuneChat] You have joined the party " +  args[2].toLowerCase() +"!");
								} else if (args[1].equalsIgnoreCase("invite")) {
									if (args.length > 2) {
										if (ChatParties.getInstance().isInAParty((Player) sender)) {
											Player target = KitsuneChat.getInstance().getServer().getPlayer(args[2]);
											if (target != null) {
												ComponentBuilder cb = new ComponentBuilder("[KitsuneChat] ").
														color(net.md_5.bungee.api.ChatColor.YELLOW).append(sender.getName())
														.color(net.md_5.bungee.api.ChatColor.YELLOW).append(" has invited you to a party! Click here to join: ")
														.color(net.md_5.bungee.api.ChatColor.YELLOW).append("[ACCEPT]")
														.color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kc party join "+ ChatParties.getInstance().getPartyName((Player) sender)))
														.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join "+ChatParties.getInstance().getPartyName((Player) sender)).create()));
												target.spigot().sendMessage(cb.create());
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
						} else if (args[0].equalsIgnoreCase("reload")) {
								if (sender.hasPermission("kitsunechat.reload"))
								{
									KitsuneChat.getInstance().reload();
									sender.sendMessage(ChatColor.GRAY+"[KitsuneChat] KitsuneChat reloaded! >w<");
								}
								else
								{
									sender.sendMessage(ChatColor.RED+"[KitsuneChat] You do not have permission to reload.");
								}
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
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] KitsuneChat - Channeled Chat System v"+KitsuneChat.getInstance().getDescription().getVersion()+" by CyberKitsune.");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc ? - Help, this command. ");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc party join <name> - Join a party with name <name>. ");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc party list - Lists who is in your party.");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc party leave - Leaves your current party. ");
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc party invite <player> - Invites <player> to your current party.");
		for(KitsuneChannel channel : KitsuneChat.getInstance().channels.values()) {
			if (channel.hasPermission(target))
				target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc "+channel.getPrefix()+" - Change default channel to "+channel.getChannelName()+".");
		}
		if (target.hasPermission("kitsunechat.reload"))
			target.sendMessage("[KitsuneChat] /kc reload - Reload KitsuneChat");
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
		if (command.getName().equalsIgnoreCase("kc"))
		{
			ArrayList<String> possibleCompletions = new ArrayList<>(Arrays.asList("?", "party"));
			if(commandSender.hasPermission("kitsunechat.reload"))
				possibleCompletions.add("reload");
			possibleCompletions.addAll(KitsuneChat.getInstance().channels.keySet());

			final List<String> completions = new ArrayList<>();
			int checkIndex = 0;
			if(strings[0].equals("party"))
			{
				possibleCompletions = new ArrayList<>(Arrays.asList("join", "list", "leave", "invite"));
				if(strings.length > 1)
				{
					checkIndex = 1;
					switch (strings[1]) {
						case "join":
							return Collections.singletonList("<party_name>");
						case "invite":
							return null;
						case "leave":
						case "list":
							return Collections.singletonList("");
					}
				}

			} else if(strings.length > 1)
			{
				possibleCompletions = new ArrayList<>();
			}


			StringUtil.copyPartialMatches(strings[checkIndex], possibleCompletions, completions);

			Collections.sort(completions);

			return completions;
		}
		else if(command.getName().equalsIgnoreCase("me"))
		{
			return Collections.singletonList("");
		}
		else if(command.getName().equalsIgnoreCase("r"))
		{
			return Collections.singletonList("<reply>");
		}

		return null;
	}
}
