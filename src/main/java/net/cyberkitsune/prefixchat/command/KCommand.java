package net.cyberkitsune.prefixchat.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import java.util.Collections;
import java.util.List;

public interface KCommand {
    String getName();
    default @NotNull List<String> getAliases()
    {
        return Collections.emptyList();
    }
    default List<String> getSubCommands()
    {
        return null;
    }
    default String getHelp()
    {
        return "";
    }
    default String getHelpForSubcommand(String subCommand)
    {
        return "";
    }
    boolean requiresPlayer();
    boolean runCommand(CommandSender sender, String subCommand, String[] subCommandArgs);
    default List<String> getPossibleCompletions(CommandSender sender, String subCommand)
    {
        if(getSubCommands() != null)
        {
            if (subCommand == null)
                return getSubCommands();
            else
                return Collections.emptyList();
        }

        return null;
    }

    default boolean hasPermission(Player p, String subCommand)
    {
        if(subCommand != null)
            return p.hasPermission("kitsunechat.command."+getName()+"."+subCommand);
        return p.hasPermission("kitsunechat.command." + getName());
    }

    default boolean senderCanRunCommand(CommandSender s, String subCommand)
    {
        boolean canRun = true;
        if(requiresPlayer() && !(s instanceof Player))
            canRun = false;

        if(s instanceof Player && !hasPermission((Player) s, subCommand))
            canRun = false;

        return canRun;
    }
}
