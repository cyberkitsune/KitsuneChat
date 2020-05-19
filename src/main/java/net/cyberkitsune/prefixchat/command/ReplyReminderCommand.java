package net.cyberkitsune.prefixchat.command;

import net.cyberkitsune.prefixchat.KitsuneChatUserData;
import net.cyberkitsune.prefixchat.LocalizedString;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ReplyReminderCommand implements KCommand {

    @Override
    public String getName() {
        return "reminder";
    }

    @Override
    public List<String> getSubCommands() {
        return Arrays.asList("off", "on");
    }

    @Override
    public String getHelp() {
        return "Disable or enable the reminder when receiving your first private message.";
    }

    @Override
    public String getHelpForSubcommand(String subCommand) {
        switch (subCommand) {
            case "off":
                return "/kc reminder off - Disables reply reminder after receiving a message.";
            case "on":
                return "/kc remidner on - Enables reply reminder after receiving a message.";
            default:
                return "";
        }
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public boolean runCommand(CommandSender sender, String subCommand, String[] subCommandArgs) {
        if (subCommand == null)
            return false;

        switch (subCommand) {
            case "off":
                // Disable /r warning when player gets a /msg until enabled
                KitsuneChatUserData.getInstance().setReplyReminderSetting((Player) sender, true);
                sender.sendMessage(LocalizedString.get("commands.reminder.disabled"));
                return true;
            case "on":
                // Re-enable /r warning
                KitsuneChatUserData.getInstance().setReplyReminderSetting((Player) sender, false);
                sender.sendMessage(LocalizedString.get("commands.reminder.enabled"));
                return true;
        }
        return false;
    }
}
