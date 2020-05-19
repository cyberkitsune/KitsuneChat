package net.cyberkitsune.prefixchat;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LocalizedString {

    public static FileConfiguration getLocaleFile()
    {
        InputStream inputStream = KitsuneChat.getInstance().getResource("i18n.yml");
        if(inputStream == null)
        {
            KitsuneChat.getInstance().mcLog.severe("[KitsuneChat] Localization resource not found!!!");
            return null;
        }
        InputStreamReader reader = new InputStreamReader(inputStream);

        return YamlConfiguration.loadConfiguration(reader);

    }

    public static String get(String key, String locale, boolean colorized)
    {
        FileConfiguration lf = getLocaleFile();

        if(lf == null)
            return "";

        if(!lf.contains(locale))
            locale = "en_us";
        String message;
        if(lf.contains(locale+"."+key))
            message = lf.getString(locale+"."+key);
        else if(KitsuneChat.getInstance().config.getBoolean("missing-translation-fallback"))
        {
            locale = "en_us";
            message = lf.getString(locale+"."+key);
        }
        else
            message = String.format("&4&l[MISSING %s %s]&r", locale, key);

        if(colorized) {
            assert message != null;
            return ChatColor.translateAlternateColorCodes('&', message);
        }
        else
            return message;

    }

    public static String get(String key, String locale)
    {
        return get(key, locale, true);
    }

    public static String get(String key)
    {
        return get(key, "en_US");
    }

}
