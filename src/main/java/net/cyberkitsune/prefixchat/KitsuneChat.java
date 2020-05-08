package net.cyberkitsune.prefixchat;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;

import net.cyberkitsune.prefixchat.channels.KitsuneChannel;
import net.milkbowl.vault.chat.Chat;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.reflections.Reflections;

public class KitsuneChat extends JavaPlugin {

	private static KitsuneChat instance = null;
	public HashMap<String, KitsuneChannel> channels;
	public Logger mcLog = Logger.getLogger("Minecraft");
	public KitsuneChatCommand exec = new KitsuneChatCommand();
	public UserMessaging msgExec = new UserMessaging();
	public NicknameChanger nickExec = new NicknameChanger();
	public List<String> prefixes;
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
		getServer().getPluginManager().registerEvents(new JoinQuitListener(), this);

		// Commands
		Objects.requireNonNull(getCommand("kc")).setExecutor(exec);
		Objects.requireNonNull(getCommand("kc")).setTabCompleter(exec);
		PluginCommand meCmd = getCommand("me");
		PluginCommand msgCmd = getCommand("msg");
		PluginCommand repCmd = getCommand("r");
		PluginCommand nickCmd = getCommand("nick");
		
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
		
		if(nickCmd == null) {
			mcLog.info("[KitsuneChat] Unable to hook /nick! Disabling nickname support");
		} else {
			nickCmd.setExecutor(nickExec);
		}
		

		KitsuneChatUserData.getInstance().loadUserData();

		//Put any online users back into their parties (in the event of a reload)
		for(Player plr : getServer().getOnlinePlayers()) {
			ChatParties.getInstance().changeParty(plr, KitsuneChatUserData.getInstance().getPartyDataForUser(plr));
		}
		List<String> channelList = Arrays.asList("global" , "local" , "staff" , "admin" , "party" , "world");
		prefixes = new ArrayList<>();
		for(String channel : channelList) {
			if(!channel.equals("global")) {
				if(getConfig().getBoolean("channels."+ channel + ".enabled")) {
					prefixes.add(getConfig().getString("channels." + channel + ".prefix"));
				}
			} else {
				prefixes.add(getConfig().getString("channels." + channel + ".prefix"));
			}
		}
	}
	
	@Override
	public void onDisable() {
		// TODO Disable stuff?
		mcLog.info("[KitsuneChat] KitsuneChat disabled! You made CK cry ;~;");
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
		if(!config.isSet("channels.global.prefix")) {
			config.set("channels.global.prefix", "!");
			config.set("channels.world.prefix", "#");
			config.set("channels.admin.prefix", "@");
			config.set("channels.staff.prefix", "^");
			config.set("channels.party.prefix", "$");
			config.set("channels.local.prefix", "%");
			config.set("channels.party.cost", 0);
			config.set("channels.local.radius", 128);
			config.set("skipvault", false);
		}
		if(!config.isSet("channels.global.sayformat")) {
			config.set("channels.global.sayformat", "[{world}] {sender}: {message}");
			config.set("channels.admin.sayformat", "[{prefix}] {sender}: {message}");
			config.set("channels.staff.sayformat", "[{prefix}] {sender}: {message}");
			config.set("channels.world.sayformat", "[{prefix}] {sender}: {message}");
			config.set("channels.party.sayformat", "[{party}] {sender}: {message}");
			config.set("channels.local.sayformat", "{sender}: {message}");
		}
		
		if(!config.isSet("channels.global.meformat")) {
			config.set("channels.global.meformat", "[{world}] * {sender} {message}");
			config.set("channels.admin.meformat", "[{prefix}] * {sender} {message}");
			config.set("channels.staff.meformat", "[{prefix}] * {sender} {message}");
			config.set("channels.world.meformat", "[{prefix}] * {sender} {message}");
			config.set("channels.party.meformat", "[{party}] * {sender} {message}");
			config.set("channels.local.meformat", "* {sender} {message}");
		}
		
		if(!config.isSet("emote.prefix")) {
			config.set("emote.prefix", "|");
		}
		
		if(!config.isSet("default")) {
			config.set("default", "%");
		}
		
		if(!config.isSet("channels.local.warnifalone")) {
			config.set("channels.local.warnifalone", true);
		}
		
		if(!config.isSet("channels.local.enabled")) {
			config.set("channels.local.enabled", true);
			config.set("channels.world.enabled", true);
			config.set("channels.staff.enabled", true);
			config.set("channels.admin.enabled", true);
			config.set("channels.party.enabled", true);
		}
		
		config.set("version", this.getDescription().getVersion());
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
				if(getConfig().getBoolean(ci.getChannelName() + ".enabled")) {
					mcLog.info("[KitsuneChat] Adding channel type " + channel.getName());
					channels.put(ci.getPrefix(), ci);
				}
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

}
