package net.cyberkitsune.prefixchat.command;

import net.cyberkitsune.prefixchat.KitsuneChat;
import net.cyberkitsune.prefixchat.KitsuneChatUserData;
import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChannelCommand implements KCommand {
    @Override
    public String getName() {
        return "channel";
    }

    @NotNull
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("c");
    }

    @Override
    public List<String> getSubCommands() {
        return new ArrayList<String>(KitsuneChat.getInstance().channels.keySet());
    }

    @Override
    public String getHelp() {
        return "Changes your current default chat channel.";
    }

    @Override
    public String getHelpForSubcommand(String subCommand) {
        return String.format("/kc channel %s - Changes your chat channel to %s", subCommand,
                KitsuneChat.getInstance().channels.get(subCommand).getChannelName());
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public boolean runCommand(CommandSender sender, String subCommand, String[] args) {
        if (subCommand == null)
            return false;

        if (!KitsuneChat.getInstance().channels.containsKey(subCommand))
            return false;

        KitsuneChannel ch = KitsuneChat.getInstance().channels.get(subCommand);
        KitsuneChatUserData.getInstance().setUserChannel((Player) sender, ch.getPrefix());
        sender.sendMessage(ChatColor.YELLOW + "[KitsuneChat] Default chat now set to " + ch.getChannelName());
        return true;
    }

    @Override
    public boolean hasPermission(Player p, String subCommand) {
        if (subCommand != null)
        {
            if(KitsuneChat.getInstance().channels.containsKey(subCommand))
            {
                KitsuneChannel ch = KitsuneChat.getInstance().channels.get(subCommand);
                return !p.hasPermission("kitsunechat.no." + ch.getChannelName());
            }
        }
        return true;
    }
}
