package net.cyberkitsune.prefixchat;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserMessaging implements CommandExecutor, Listener, TabCompleter {
	
	private Map<String, String> replies;
	private List<String> replyReminded;
	
	public UserMessaging() {
		replies = new HashMap<String, String>();
		replyReminded = new ArrayList<String>();
	}
	
	/**
	 *  Utility function used to format messages
	 *  
	 * @param player The player whom this message is displayed to.
	 * @param otherPlayer The other player involved in this transaction.
	 * @param message The message being sent.
	 * @return A formatted message for use of being sent.
	 */
	private String formatMessage(String player, String otherPlayer, String message) {
		return ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"["+player+" -> "+otherPlayer+"] "+message);
	}
	
	/**
	 *  Utility function used to format actions
	 *  
	 * @param player The player whom this message is displayed to.
	 * @param otherPlayer The other player involved in this transaction.
	 * @param message The action occurring.
	 * @return A formatted action for use of being sent.
	 */
	private String formatAction(String player, String otherPlayer, String message) {
		return ChatColor.translateAlternateColorCodes('&', ChatColor.GRAY+"[-> "+otherPlayer+"] * "+player+" "+message);
	}
	
	/**
	 * Utility function used to send a message or action.
	 * 
	 * @param sender CommandSender for onCommand.
	 * @param target Player target for the message.
	 * @param player Player name who used command.
	 * @param otherPlayer Player target for message.
	 * @param message The message being send.
	 * @param isAction Is this message an action?
	 * @param fromconsole Is this being sent from a console?
	 * @param toconsole Is this being sent to a console?
	 */
	private boolean userMessage(CommandSender sender, Player target, String player, String otherPlayer, String message, boolean isAction, boolean fromconsole, boolean toconsole) {
		// Send the message & log
		if(!isAction) {
			// Send message to player
			if(toconsole) {
				KitsuneChat.getInstance().getServer().getConsoleSender().sendMessage(formatMessage(player, "Me", message));
			} else {
				target.sendMessage(formatMessage(player, "Me", message));
				// Let player know once they can /r to a /msg if they haven't been already.
				if (!replyReminded.contains(target.getDisplayName()) && !KitsuneChatUserData.getInstance().getReplyReminderSetting(target)) {
					target.sendMessage(LocalizedString.get("messaging.reminder", target.getLocale()));
					replyReminded.add(target.getDisplayName());
				}
			}
			
			// Display message to sender
			if(fromconsole) {
				KitsuneChat.getInstance().getServer().getConsoleSender().sendMessage(formatMessage("Me", "CONSOLE", message));
			} else {
				sender.sendMessage(formatMessage("Me", otherPlayer, message));
			}
			
			// Log message
			if(!toconsole && !fromconsole) {
				KitsuneChat.getInstance().mcLog.info(String.format("[%s -> %s] %s", player, otherPlayer, message));
			}
			
		// Send the action
		} else {
			// Send message to player
			if(toconsole) {
				KitsuneChat.getInstance().getServer().getConsoleSender().sendMessage(formatAction(player, "Me", message));
			} else {
				target.sendMessage(formatAction(player, "Me", message));
			}
			
			// Display message to sender
			if(fromconsole) {
				KitsuneChat.getInstance().getServer().getConsoleSender().sendMessage(formatAction("Me", otherPlayer, message));
			} else {
				sender.sendMessage(formatAction("Me", otherPlayer, message));
			}
			
			// Log message if not to and from console
			if(!toconsole && !fromconsole) {
				KitsuneChat.getInstance().mcLog.info(String.format("[%s -> %s] * %s %s", player, otherPlayer, player, message));
			}
		}
		
		// Reply functionality
		replies.put(player.toLowerCase(), otherPlayer.toLowerCase());
		replies.put(otherPlayer.toLowerCase(),  player.toLowerCase());
		
		return true;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// /msg and /r commands
		if(cmd.getName().equalsIgnoreCase("msg") || cmd.getName().equalsIgnoreCase("r")) {
			
			// Variables
			String player = null;			// The player name of the sender
			String otherPlayer = null;		// The player name of the receiver
			Player target = null;
			boolean toconsole = false;
			boolean fromconsole = false;
			boolean isAction = false;
			String[] message = null;
			String joined = null;
			String locale;

			// Are we from console? Get player name.
			if(!(sender instanceof Player)) {
				fromconsole = true;
				player = "CONSOLE";
				locale = "en_US";
			} else {
				player = ChatColor.stripColor(((Player) sender).getDisplayName());
				locale = ((Player) sender).getLocale();
			}
			
			// MSG checks
			if(cmd.getName().equalsIgnoreCase("msg")) {
				// Argument checks
				if(args.length < 2) {
					sender.sendMessage(LocalizedString.get("messaging.invalid", locale));
					return false;
				}
			
				// Self-talk check
				if(sender.getName().equalsIgnoreCase(args[0])) {
					sender.sendMessage(LocalizedString.get("messaging.yourself", locale));
					return true;
				}
				
				// Get the receiver
				otherPlayer = args[0];
				
				// Message to string form
				message = Arrays.copyOfRange(args, 1, args.length);
				joined = Joiner.on(" ").join(message);				
				
				
			// R checks
			} else if(cmd.getName().equalsIgnoreCase("r")) {
				// Argument checks
				if(args.length <= 0) {
					sender.sendMessage(LocalizedString.get("messaging.rusage", locale));
					return false;
				}
				
				// Get the receiver
				if(!replies.containsKey(player.toLowerCase())) {
					sender.sendMessage(LocalizedString.get("messaging.nobody", locale));
					return false;
				} else {
					otherPlayer = replies.get(player.toLowerCase());
				}
				
				// Message to string form
				message = Arrays.copyOfRange(args, 0, args.length);
				joined = Joiner.on(" ").join(message);
			}
			
			// Are we sending to console? Get other player and check.
			if(otherPlayer.equalsIgnoreCase("CONSOLE")) {
				otherPlayer = "CONSOLE"; // Proper-cased name
				toconsole = true;
				
			// Get the target if not console
			} else {
				target = KitsuneChatUtils.getInstance().getPlayerFromDisplay(otherPlayer);
				
				// Does the player exist/online?
				if((target == null || !target.isOnline())) {
					sender.sendMessage(String.format(LocalizedString.get("cantfind", locale), args[0]));
					return false;
				} else {
					otherPlayer = target.getDisplayName(); // Get proper-cased name
				}
			}
						
			// Is this message an action?
			if(joined.startsWith(KitsuneChat.getInstance().config.getString("emote.prefix"))) {
				joined = joined.substring(1);
				isAction = true;
			}
			
			return userMessage(sender, target, player, otherPlayer, joined, isAction, fromconsole, toconsole);
		}
		
		return false;
	}

	@EventHandler
	// Reset the player to be warned again about /r when they reconnect
	public void onLeave(PlayerQuitEvent evt) {
		replyReminded.remove(evt.getPlayer().getDisplayName());
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if(command.getName().equals("r"))
		{
			return Collections.emptyList();
		}

		if(command.getName().equals("msg"))
		{
			if(args.length < 2)
			{
				ArrayList<String> names = new ArrayList<>();
				for(Player p : KitsuneChat.getInstance().getServer().getOnlinePlayers())
					names.add(ChatColor.stripColor(p.getDisplayName()));

				return names;
			}
			else
			{
				return Collections.emptyList();
			}
		}

		return null;
	}
}
