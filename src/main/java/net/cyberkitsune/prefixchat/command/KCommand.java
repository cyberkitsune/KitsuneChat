package net.cyberkitsune.prefixchat.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An interface representing a command to be run under the /kc master command
 */
public interface KCommand {
    /**
     * Retrieve the name for this command (the /kc is implied)
     * @return A string with the command name
     */
    String getName();

    /**
     * Get any additional names the command can be called by
     * @return A list containing additional command names.
     */
    default @NotNull List<String> getAliases()
    {
        return Collections.emptyList();
    }

    /**
     * Get any subcommands this command supports. A subcommand is any argument after the command.
     * For example, in  "/kc party join", "join" is a subcommand. The list should contain all possible, regardless of permissions.
     * @return A list of all possible subcommands, or null if there is none
     */
    default List<String> getSubCommands()
    {
        return null;
    }

    /**
     * Retrieve the help information for the command (not any subcommands)
     * @return A string with command help
     */
    String getHelp();

    /**
     * Retrieve the usage information for a given command's subcommand.
     * eg for subCommand of "join" under party, it should start with "/kc party join" and then explain what the subcommand does.
     * @param subCommand The subcommand string to get help for
     * @return The help text, or empty string
     */
    default String getHelpForSubcommand(String subCommand)
    {
        return "";
    }

    /**
     * Check if the command requires a Player CommandSender
     * @return true if only a player can run the command
     */
    boolean requiresPlayer();

    /**
     * Run the command.
     * @param sender CommandSender running the command
     * @param subCommand The given subCommand to run, if there is one, else null
     * @param subCommandArgs The given subCommand args to use, if there are any, else empty array.
     * @return true if the usage was correct, false if not (usage help will be displayed)
     */
    boolean runCommand(CommandSender sender, String subCommand, String[] subCommandArgs, String locale);

    /**
     * Get a list of possible tab-completions for this command. Default implementation checks permissions for subcommands.
     * @param sender CommandSender sending the command
     * @param subCommand subCommand to tab complete, or null if none.
     * @return A list of possible completions for the given sender and subcommand
     */
    default List<String> getPossibleCompletions(CommandSender sender, String subCommand)
    {
        if(getSubCommands() != null)
        {
            if (subCommand == null)
            {
                ArrayList<String> validSubcommands = new ArrayList<>();
                for(String sc : getSubCommands())
                {
                    if(!senderCanRunCommand(sender, sc))
                        continue;

                    validSubcommands.add(sc);
                }
                return validSubcommands;
            }
            else
                return Collections.emptyList();
        }

        return null;
    }

    /**
     * Check if a player has permission to run this command (and subcommand)
     * @param p Player to check for
     * @param subCommand subcommand to check for, or null if none
     * @return true if the player has permissions, else false.
     */
    default boolean hasPermission(Player p, String subCommand)
    {
        if(subCommand != null)
            return p.hasPermission("kitsunechat.command."+getName()+"."+subCommand);
        return p.hasPermission("kitsunechat.command." + getName());
    }

    /**
     * Check if a given CommandSender can run this command (or subcommand). Checks the sender class AND permissions if it's a player.
     * @param s CommandSender to check
     * @param subCommand Subcommand to check
     * @return true if the sender can run the command, else false.
     */
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
