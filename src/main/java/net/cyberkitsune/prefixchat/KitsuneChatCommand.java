package net.cyberkitsune.prefixchat;

import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import net.cyberkitsune.prefixchat.command.KCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class KitsuneChatCommand implements CommandExecutor, TabCompleter {


	private HashMap<String, KCommand> commands;
	public KitsuneChatCommand()
	{
		commands = new HashMap<>();
		loadCommands();
	}

	private void loadCommands()
	{
		commands.clear();
		// Reflect through all the commands, including aliases
		Reflections reflections = new Reflections("net.cyberkitsune.prefixchat.command");
		for(Class<? extends KCommand> cmdClass : reflections.getSubTypesOf(KCommand.class))
		{
			try {
				KCommand kc = cmdClass.getDeclaredConstructor().newInstance();
				if(!kc.getName().equals("?"))
					commands.put(kc.getName(), kc);
				for (String alias : kc.getAliases())
				{
					if(!alias.equals("?"))
						commands.put(alias, kc);
				}
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean onCommand(@NotNull CommandSender sender, Command command,
							 @NotNull String label, @NotNull String[] args) {
		if (command.getName().equalsIgnoreCase("kc")) {
			// Step one, check if a command was not requested, or if help was requested specifically.
			if (args.length < 1)
			{
				sender.sendMessage(ChatColor.RED + "[KitsuneChat] No command specified. Possible commands:");
				printHelp(sender);
			}
			else if (args[0].equals("?"))
			{
				sender.sendMessage(ChatColor.YELLOW+"[KitsuneChat] KitsuneChat - Channeled Chat System v"+KitsuneChat.getInstance().getDescription().getVersion()+" by CyberKitsune.");
				printHelp(sender);
			}
			else if (!commands.containsKey(args[0]))
			{
				sender.sendMessage(ChatColor.RED + "[KitsuneChat] Invalid command specified. Possible commands:");
				printHelp(sender);
			}
			else
			{
				KCommand cmd = commands.get(args[0]);

				String subCommand = null;
				String[] subCommandArgs = {};
				if(args.length > 1)
					subCommand = args[1];

				if(args.length > 2)
					subCommandArgs = Arrays.copyOfRange(args, 2, args.length);

				if(cmd.requiresPlayer() && !(sender instanceof Player))
				{
					sender.sendMessage(ChatColor.RED + "[KitsuneChat] This command must be run by a player.");
					return true;
				}

				// We're a player, so we should do a permission check
				if(sender instanceof Player && !cmd.hasPermission((Player) sender, subCommand))
				{
					sender.sendMessage(ChatColor.RED + "[KitsuneChat] You do not have permission to do that.");
					return true;
				}

				// OK we have perms, have collected arguments, now we run the command.
				boolean argsValid = cmd.runCommand(sender, subCommand, subCommandArgs);
				if(!argsValid)
				{
					sender.sendMessage(ChatColor.RED + String.format("[KitsuneChat] Invalid usage of /kc %s. Possible usages:", cmd.getName()));
					for(String sCmd : cmd.getSubCommands())
						sender.sendMessage(ChatColor.YELLOW + "[KitsuneChat] " + cmd.getHelpForSubcommand(sCmd));
				}


			}
			return true;
		} else return !command.getName().equalsIgnoreCase("me");
	}
	
	private void printHelp(CommandSender target) {
		target.sendMessage(ChatColor.YELLOW+"[KitsuneChat] /kc ? - Help, this command. ");
		ArrayList<String> checkedCommands = new ArrayList<>();
		for (KCommand cmd : commands.values())
		{
			if (checkedCommands.contains(cmd.getName()))
				continue;

			checkedCommands.add(cmd.getName());
			if(!cmd.senderCanRunCommand(target, null))
				continue;

			StringBuilder sb = new StringBuilder(ChatColor.YELLOW.toString());
			sb.append("[KitsuneChat] /kc ").append(cmd.getName());
			if(cmd.getAliases().size() > 0)
			{
				sb.append(" (or: ");
				sb.append(String.join(",", cmd.getAliases()));
				sb.append(")");
			}
			sb.append(" - ").append(cmd.getHelp());
			target.sendMessage(sb.toString());
		}
	}

	private List<String> getCommandNamesForSender(CommandSender s)
	{
		ArrayList<String> names = new ArrayList<>();
		for(Map.Entry<String, KCommand> cmdEntry : commands.entrySet())
		{
			KCommand cmd = cmdEntry.getValue();

			if(cmd.senderCanRunCommand(s, null))
				names.add(cmdEntry.getKey());
		}
		return names;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
		if (command.getName().equalsIgnoreCase("kc"))
		{
			ArrayList<String> possibleCompletions = new ArrayList<>();
			switch (args.length)
			{
				case 0:
				case 1:
					// No command specificed, or partial command.
					possibleCompletions.addAll(getCommandNamesForSender(commandSender));
					break;
				case 2:
				default:
					// Command specified. Use handler.
					if(commands.containsKey(args[0]))
					{
						KCommand cmd = commands.get(args[0]);
						String subCommand = null;

						if(args.length > 2)
							subCommand = args[1];
						if(cmd.senderCanRunCommand(commandSender, subCommand))
						{
							List<String> cmdCompletions = cmd.getPossibleCompletions(commandSender, subCommand);
							if(cmdCompletions == null)
								return null;
							else if(cmdCompletions.isEmpty())
								return cmdCompletions;
							else
								possibleCompletions.addAll(cmdCompletions);
						}

					}
					else
					{
						return Collections.emptyList();
					}
			}

			final List<String> completions = new ArrayList<>();

			StringUtil.copyPartialMatches(args[args.length - 1], possibleCompletions, completions);

			Collections.sort(completions);

			return completions;
		}
		else if(command.getName().equalsIgnoreCase("me"))
		{
			return Collections.emptyList();
		}
		else if(command.getName().equalsIgnoreCase("r"))
		{
			return Collections.emptyList();
		}

		return null;
	}
}
