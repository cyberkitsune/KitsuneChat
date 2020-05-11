package net.cyberkitsune.prefixchat.command;

import net.cyberkitsune.prefixchat.ChatParties;
import net.cyberkitsune.prefixchat.KitsuneChat;
import net.cyberkitsune.prefixchat.KitsuneChatUserData;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PartyCommand implements KCommand {
    @Override
    public String getName() {
        return "party";
    }

    @Override
    public @NotNull List<String> getAliases() {
        return Collections.singletonList("p");
    }

    @Override
    public List<String> getSubCommands() {
        return Arrays.asList("join", "leave", "invite", "list");
    }

    @Override
    public String getHelp() {
        return "Allows you to create, join, leave, or invite people to a chat party.";
    }

    @Override
    public String getHelpForSubcommand(String subCommand) {
        switch (subCommand)
        {
            case "join":
                return "/kc party join <name> - Joins a chat party by name.";
            case "leave":
                return "/kc party leave - Leaves a chat party";
            case "invite":
                return "/kc party invite <name> - Invites a user to join your chat party.";
            case "list":
                return "/kc party list - Shows who is in your current chat party.";
            default:
                return "";
        }
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public boolean runCommand(CommandSender sender, String command, String[] args) {
        if (command == null)
            return false;

        switch (command)
        {
            case "join":
                if (args != null && args.length > 0)
                {
                    ChatParties.getInstance().changeParty((Player) sender, args[0].toLowerCase());
                    KitsuneChatUserData.getInstance().setUserChannel((Player) sender, KitsuneChat.getInstance().getConfig().getString("channels.party.prefix"));
                    sender.sendMessage(ChatColor.YELLOW + "[KitsuneChat] You have joined the party " + args[0].toLowerCase() + "!");
                    return true;
                }
                else
                {
                    return false;
                }
            case "leave":
                ChatParties.getInstance().leaveParty((Player) sender, false);
                return true;
            case "invite":
                if (args != null && args.length > 0)
                {
                    if (ChatParties.getInstance().isInAParty((Player) sender)) {
                        Player target = KitsuneChat.getInstance().getServer().getPlayer(args[0]);
                        if (target != null) {
                            ComponentBuilder cb = new ComponentBuilder("[KitsuneChat] ").
                                    color(net.md_5.bungee.api.ChatColor.YELLOW).append(sender.getName())
                                    .color(net.md_5.bungee.api.ChatColor.YELLOW).append(" has invited you to a party! Click here to join: ")
                                    .color(net.md_5.bungee.api.ChatColor.YELLOW).append("[ACCEPT]")
                                    .color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kc party join " + ChatParties.getInstance().getPartyName((Player) sender)))
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join " + ChatParties.getInstance().getPartyName((Player) sender)).create()));
                            target.spigot().sendMessage(cb.create());
                            ChatParties.getInstance().notifyParty(ChatParties.getInstance().getPartyName((Player) sender), ChatColor.GREEN + "[KitsuneChat] " + sender.getName() + " invited " + target.getDisplayName() + ChatColor.GREEN + " to the party.");
                        } else {
                            sender.sendMessage(ChatColor.RED + "[KitsuneChat] That player does not exist!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "[KitsuneChat] You aren't in a party!");
                    }
                    return true;
                }
                else
                {
                    return false;
                }
            case "list":
                if (ChatParties.getInstance().isInAParty((Player) sender)) {
                    StringBuilder playerlist = new StringBuilder();
                    Set<Player> partyMembers = ChatParties.getInstance().getPartyMembers(ChatParties.getInstance().getPartyName((Player) sender));
                    for (Player plr : partyMembers) {
                        playerlist.append(plr.getDisplayName()).append(", ");
                    }
                    playerlist = new StringBuilder(playerlist.substring(0, playerlist.length() - 2) + ".");
                    sender.sendMessage(ChatColor.YELLOW + "[KitsuneChat] " + partyMembers.size() + ((partyMembers.size() == 1) ? " person " : " people ") + "in the party.");
                    sender.sendMessage(ChatColor.YELLOW + "[KitsuneChat] They are: " + playerlist + ".");
                } else {
                    sender.sendMessage(ChatColor.RED + "[KitsuneChat] You are not in a party!");
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public List<String> getPossibleCompletions(CommandSender sender, String subcommand) {
        if (subcommand != null)
        {
            switch (subcommand)
            {
                case "join":
                    return Collections.singletonList("<party>");
                case "leave":
                case "list":
                    return Collections.emptyList();
                case "invite":
                    return null;
            }
        }
        return KCommand.super.getPossibleCompletions(sender, subcommand);
    }
}
