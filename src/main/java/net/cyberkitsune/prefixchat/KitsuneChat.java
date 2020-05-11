package net.cyberkitsune.prefixchat;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;

import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import net.cyberkitsune.prefixchat.command.KCommand;
import net.milkbowl.vault.chat.Chat;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.reflections.Reflections;

public class KitsuneChat extends JavaPlugin {

	private static KitsuneChat instance = null;
	public HashMap<String, KitsuneChannel> channels;
	public HashMap<String, KCommand> commands;
	public Logger mcLog = Logger.getLogger("Minecraft");
	public KitsuneChatCommand exec = new KitsuneChatCommand();
	public UserMessaging msgExec = new UserMessaging();
	public Chat vaultChat = null;
	public boolean vaultEnabled = false;
	public boolean multiVerse = false;
	public MultiverseCore multiversePlugin = null;
	public Configuration config = null;

	public KitsuneChat()
	{
		if (instance == null)
			instance = this;
		channels = new HashMap<>();
	}

	public static KitsuneChat getInstance()
	{
		return instance;
	}

	@Override
	public void onEnable() {
		mcLog.info("[KitsuneChat] Enabling KitsuneChat version "+ this.getDescription().getVersion());
		if(!(new File(getDataFolder()+"/config.yml").exists())) {
			mcLog.info("[KitsuneChat] KitsuneChat config does not exist! Creating default config...");
			if(!getDataFolder().exists()) {
				boolean succ = getDataFolder().mkdir();
				if(!succ)
					mcLog.severe("[KitsuneChat] KitsuneChat could not make data folder...");
			}
			try {
				boolean succ = new File(getDataFolder()+"/config.yml").createNewFile();
				if(!succ)
					mcLog.warning("[KitsuneChat] Config file already exists...");
			} catch (Exception e) {
				mcLog.severe("[KitsuneChat] KitsuneChat could NOT create config file!!");
				e.printStackTrace();
			}
		}
		loadConfig();
		setDefaults();
		mcLog.info("[KitsuneChat] KitsuneChat config loaded!");
		if(setupVaultChat()) {
			vaultEnabled = true;
			mcLog.info("[KitsuneChat] Successfully linked to Vault for chat! Prefix / Suffix support enabled!");
		} else {
			vaultEnabled = false;
			mcLog.info("[KitsuneChat] Unable to link to Vault for chat! Is it installed? Prefix / Suffix support disabled!");
		}
		if(checkMultiverse()) {
			multiVerse = true;
			mcLog.info("[KitsuneChat] Multiverse found! Enabling world aliases!");
		} else {
			multiVerse = false;
			mcLog.info("[KitsuneChat] Did not find Multiverse. Not enabling world aliases.");
		}
		// Channels
		initChannels();

		// Listeners
		getServer().getPluginManager().registerEvents(new ChatListener(), this);
		getServer().getPluginManager().registerEvents(new ConnectHandler(), this);
		if (getConfig().getBoolean("modify-join-leave"))
			getServer().getPluginManager().registerEvents(new JoinQuitListener(), this);

		// Bungee
		getServer().getMessenger().registerOutgoingPluginChannel(this, "bungee:kitsunechat");

		if(getConfig().getBoolean("bungee-receive"))
		{
			getServer().getMessenger().registerIncomingPluginChannel(this, "bungee:kitsunechat", new KitsuneBungeeListener());
		}

		// Commands
		Objects.requireNonNull(getCommand("kc")).setExecutor(exec);
		Objects.requireNonNull(getCommand("kc")).setTabCompleter(exec);
		PluginCommand meCmd = getCommand("me");
		PluginCommand msgCmd = getCommand("msg");
		PluginCommand repCmd = getCommand("r");
		
		if(meCmd == null)
		{
			mcLog.info("[KitsuneChat] Unable to hook /me ! Contact whoever broke your shit.");
		} else {
			meCmd.setExecutor(exec);
		}
		
		if(msgCmd == null || repCmd == null) {
			mcLog.info("[KitsuneChat] Unable to allocate /msg or /r, disabling PM support.");
		} else {
			msgCmd.setExecutor(msgExec);
			repCmd.setExecutor(msgExec);
		}

		KitsuneChatUserData.getInstance().loadUserData();

		//Put any online users back into their parties (in the event of a reload)
		for(Player plr : getServer().getOnlinePlayers()) {
			ChatParties.getInstance().changeParty(plr, KitsuneChatUserData.getInstance().getPartyDataForUser(plr));
		}

	}
	
	@Override
	public void onDisable() {
		// TODO Disable stuff?
		mcLog.info("[KitsuneChat] KitsuneChat disabled! You made CK cry ;~;");
	}

	public void reload()
	{
		mcLog.info("[KitsuneChat] Reloading KitsuneChat >w<");
		HandlerList.unregisterAll(this);
		getServer().getMessenger().unregisterOutgoingPluginChannel(this);
		getServer().getMessenger().unregisterIncomingPluginChannel(this);
		onEnable();
	}

	private void loadConfig() {
		try {
			this.getConfig().load(getDataFolder()+"/config.yml");
		} catch (Exception e) {
			mcLog.severe("KitsuneChat could not load the config file!!");
			e.printStackTrace();
		} 
	}
	
	
	//TODO Cache config!
	private void setDefaults() {
		config = this.getConfig();
		config.options().copyDefaults(true);
		this.saveConfig();
	}
	
	private boolean setupVaultChat() {
		if(getServer().getPluginManager().getPlugin("Vault") == null || getConfig().getBoolean("skipvault")) {
			return false;
		}
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
		if(chatProvider != null) {
			vaultChat = chatProvider.getProvider();
		}
		return (vaultChat != null);
	}
	
	private boolean checkMultiverse() {
		if(getServer().getPluginManager().getPlugin("Multiverse-Core") == null) {
			return false;
		} else {
			multiversePlugin = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
			return true;
		}
	}

	private void initChannels()
	{
		channels.clear();
		// Is reflection the best place to go here?
		Reflections reflections = new Reflections("net.cyberkitsune.prefixchat.channels");
		for(Class<? extends KitsuneChannel> channel : reflections.getSubTypesOf(KitsuneChannel.class))
		{
			try {
				KitsuneChannel ci = channel.getDeclaredConstructor().newInstance();
				if(getConfig().getBoolean("channels."+ ci.getChannelName() + ".enabled")) {
					channels.put(ci.getPrefix(), ci);
				}
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

		if (channels.size() == 0)
			mcLog.warning("[KitsuneChat] No channels were loaded, and nobody will be able to talk." +
					" Do you have any enabled in config.yml?");
		else
		{
			StringJoiner sd = new StringJoiner(", ");
			for (KitsuneChannel c : channels.values())
				sd.add(c.getChannelName());
			mcLog.info("[KitsuneChat] Loaded and enabled channels: " + sd);
		}

		// Check default
		String defaultChannel = getConfig().getString("channels.default");
		if (!channels.containsKey(defaultChannel))
			mcLog.warning("[KitsuneChat] Your set default channel does not exist or is not enabled. Make sure to " +
					"fix this in the config.yml");
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
				commands.put(kc.getName(), kc);
				for (String alias : kc.getAliases())
				{
					commands.put(alias, kc);
				}
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

	public KitsuneChannel getChannelByPrefix(String prefix)
	{
		if (channels.containsKey(prefix))
		{
			return channels.get(prefix);
		}

		return null;
	}

}
