package net.cyberkitsune.prefixchat.command;

import net.cyberkitsune.prefixchat.KitsuneChat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReloadCommand implements KCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getHelp() {
        return "Reloads the plugin";
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public boolean runCommand(CommandSender sender, String command, String[] args) {
        KitsuneChat.getInstance().reload();
        sender.sendMessage(ChatColor.GRAY + "[KitsuneChat] KitsuneChat reloaded! >w<");
        return true;
    }

    @Override
    public List<String> getPossibleCompletions(CommandSender sender, String subCommand) {
        return Collections.emptyList();
    }
}
