package me.cyberkitsune.prefixchat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

public class UserMessaging implements CommandExecutor {
	
	private Map<String, String> replies;
	private KitsuneChat plugin;
	
	public UserMessaging(KitsuneChat plugin) {
		this.plugin = plugin;
		replies = new HashMap<String, String>();
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
	 * @param action The action occurring.
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
				plugin.getServer().getConsoleSender().sendMessage(formatMessage(player, "Me", message));
			} else {
				target.sendMessage(formatMessage(player, "Me", message));
			}
			
			// Display message to sender
			if(fromconsole) {
				plugin.getServer().getConsoleSender().sendMessage(formatMessage("Me", otherPlayer, message));
			} else {
				sender.sendMessage(formatMessage("Me", otherPlayer, message));
			}
			
			// Log message
			if(!toconsole && !fromconsole) {
				plugin.mcLog.info(String.format("[%s -> %s] %s", player, otherPlayer, message));
			}
			
		// Send the action
		} else {
			// Send message to player
			if(toconsole) {
				plugin.getServer().getConsoleSender().sendMessage(formatAction(player, otherPlayer, message));
			} else {
				target.sendMessage(formatAction(player, otherPlayer, message));
			}
			
			// Display message to sender
			if(fromconsole) {
				plugin.getServer().getConsoleSender().sendMessage(formatAction(player, otherPlayer, message));
			} else {
				sender.sendMessage(formatAction(player, otherPlayer, message));
			}
			
			// Log message if not to and from console
			if(!toconsole && !fromconsole) {
				plugin.mcLog.info(String.format("[%s -> %s] * %s %s", player, otherPlayer, player, message));
			}
		}
		
		// Reply functionality
		replies.put(player, otherPlayer);
		replies.put(otherPlayer,  player);
		
		return true;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		// /msg and /memsg commands
		if(cmd.getName().equalsIgnoreCase("msg") || cmd.getName().equalsIgnoreCase("memsg")) {
			
			if(args.length < 2) {
				if(cmd.getName().equalsIgnoreCase("msg")) {
					sender.sendMessage(ChatColor.RED+"[KitsuneChat] Invalid format, use: /msg [Player] [Message]");
				} else {
					sender.sendMessage(ChatColor.RED+"[KitsuneChat] Invalid format, use: /memsg [Player] [Action]");
				}
				return false;
			}
			
			if(sender.getName().equalsIgnoreCase(args[0])) {
				if(cmd.getName().equalsIgnoreCase("msg")) {
					sender.sendMessage(ChatColor.GRAY+"Talking to yourself is a sign of insanity.");
				} else {
					sender.sendMessage(ChatColor.GRAY+"Is this the real life? This is just fantasy.");
				}
				return true;
			}
			
			// Variables
			String player = null;			// The player name of the sender
			String otherPlayer = null;		// The player name of the receiver
			Player target = null;
			boolean toconsole = false;
			boolean fromconsole = false;
			
			// Set player name
			if(!(sender instanceof Player)) {
				fromconsole = true;
				player = "CONSOLE";
			} else {
				player = ((Player) sender).getDisplayName();
			}
			
			// Set otherPlayer name
			if(args[0].equalsIgnoreCase("CONSOLE")) {
				toconsole = true;
				otherPlayer = "CONSOLE";
				
			// Get the player
			} else {
				target = plugin.getServer().getPlayer(args[0]);
				
				// Does the player exist/online?
				if((target == null || !target.isOnline())) {
					sender.sendMessage(ChatColor.RED+"[KitsuneChat] I can't find "+args[0]+"! D:");
					return false;
				}
				
				otherPlayer = target.getDisplayName();
			}
						
			// Message to string form
			String[] message = Arrays.copyOfRange(args, 1, args.length);
			String joined = Joiner.on(" ").join(message);
			
			// Is the message an action [simplifies checks later to just boolean comparison]
			boolean isAction = false;
			if(cmd.getName().equalsIgnoreCase("memsg")) {
				isAction = true;
			}
			
			return userMessage(sender, target, player, otherPlayer, joined, isAction, fromconsole, toconsole);
			
		// /r and /mer commands
		} else if(cmd.getName().equalsIgnoreCase("r") || cmd.getName().equalsIgnoreCase("mer")) {
			
			// Variables
			String player = null;			// The player name of the sender
			String otherPlayer = null;		// The player name of the receiver
			Player target = null;
			boolean toconsole = false;
			boolean fromconsole = false;
			boolean isAction = false;
			
			// Is it an action reply?
			if(cmd.getName().equalsIgnoreCase("mer")) {
				isAction = true;
			}
			
			// Incorrect usage catch
			if(args.length <= 0) {
				if(!isAction) {
					sender.sendMessage(ChatColor.RED+"[KitsuneChat] Usage: /r [message]");
				} else {
					sender.sendMessage(ChatColor.RED+"[KitsuneChat] Usage: /mer [action]");
				}
				return false;
			}
			
			// Is the sender console?			
			if(!(sender instanceof Player)) {
				fromconsole = true;
				player = "CONSOLE";
			} else {
				player = sender.getName();
			}

			
			// Get the receiver
			if(!replies.containsKey(player)) {
				sender.sendMessage(ChatColor.RED+"[KitsuneChat] You have nobody to reply to.");
				return false;
			} else {
				otherPlayer = replies.get(player);
				if(otherPlayer.equalsIgnoreCase("CONSOLE")) {
					toconsole = true;
				}
			}

			// If we're not sending to a console, check if online
			if(!toconsole) {
				target = plugin.getServer().getPlayer(otherPlayer);
			
				if(target == null) {
					sender.sendMessage(ChatColor.RED+"[KitsuneChat] I can't find "+otherPlayer+"! D:");
					return false;
				}
			}
			
			
			String joined = Joiner.on(" ").join(args);
			
			return userMessage(sender, target, player, otherPlayer, joined, isAction, fromconsole, toconsole);
		}
		
		return false;
	}

}
