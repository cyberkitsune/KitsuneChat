package net.cyberkitsune.prefixchat.command;

import net.cyberkitsune.prefixchat.ChatParties;
import net.cyberkitsune.prefixchat.KitsuneChat;
import net.cyberkitsune.prefixchat.KitsuneChatUserData;
import net.cyberkitsune.prefixchat.LocalizedString;
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
    public String getHelp(String locale) {
        return LocalizedString.get("commands.party.help", locale);
    }

    @Override
    public String getHelpForSubcommand(String subCommand, String locale) {
        return LocalizedString.get("commands.party.subcommandhelp."+subCommand, locale);
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public boolean runCommand(CommandSender sender, String command, String[] args, String locale) {
        if (command == null)
            return false;

        switch (command)
        {
            case "join":
                if (args != null && args.length > 0)
                {
                    ChatParties.getInstance().changeParty((Player) sender, args[0].toLowerCase());
                    KitsuneChatUserData.getInstance().setUserChannel((Player) sender, KitsuneChat.getInstance().getConfig().getString("channels.party.prefix"));
                    sender.sendMessage(String.format(LocalizedString.get("commands.party.joined", locale), args[0].toLowerCase()));
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
                            ComponentBuilder cb = new ComponentBuilder(String.format(LocalizedString.get("commands.party.invited", locale),sender.getName()))
                                    .append(LocalizedString.get("commands.party.accept", locale))
                                    .color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kc party join " + ChatParties.getInstance().getPartyName((Player) sender)))
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(LocalizedString.get("commands.party.click", locale) + ChatParties.getInstance().getPartyName((Player) sender)).create()));
                            target.spigot().sendMessage(cb.create());
                            ChatParties.getInstance().notifyParty(ChatParties.getInstance().getPartyName((Player) sender),
                                    String.format(LocalizedString.get("commands.party.invitedtohere"), sender.getName(), target.getDisplayName()));
                        } else {
                            sender.sendMessage(LocalizedString.get("commands.party.notexist", locale));
                        }
                    } else {
                        sender.sendMessage(LocalizedString.get("commands.party.notinparty", locale));
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
                    sender.sendMessage((LocalizedString.get("commands.party.notinparty", locale)));
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

    @Override
    public boolean hasPermission(Player p, String subCommand) {
        return p.hasPermission("kitsunechat.partychat");
    }
}
