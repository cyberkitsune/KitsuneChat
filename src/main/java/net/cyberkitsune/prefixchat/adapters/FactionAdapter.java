package net.cyberkitsune.prefixchat.adapters;

import net.cyberkitsune.prefixchat.KitsuneChat;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public interface FactionAdapter {
    boolean isPlayerInFaction(Player p);
    String getFactionNameFor(Player p);
    String getFactionTagFor(Player p);
    String getFactionRelationshipName(Player a, Player b);
    String getFactionRelationshipColor(Player a, Player b);

    static FactionAdapter getAnyLoadedFactions()
    {
        Reflections reflections = new Reflections("net.cyberkitsune.prefixchat.adapters");
        FactionAdapter adapter = null;
        for(Class<?> connectedClass : reflections.getTypesAnnotatedWith(FactionConnector.class))
        {
            String checkClassname = connectedClass.getAnnotation(FactionConnector.class).classname();
            try
            {
                Class.forName(checkClassname);
            }
            catch (ClassNotFoundException ex)
            {
                continue;
            }

            // Class found
            try
            {
                adapter = (FactionAdapter) connectedClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                KitsuneChat.getInstance().mcLog.warning(String.format("[KitsuneChat] Tried to create factions adapter %s but failed!?",
                        connectedClass.getSimpleName()));
                e.printStackTrace();
            }
        }
        return adapter;
    }
}
