package net.cyberkitsune.prefixchat.adapters;

import net.cyberkitsune.prefixchat.KitsuneChat;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;

public interface FactionAdapter {
    /***
     * Checked weather or not a player is in a faction.
     * <p>
     *     Note: Do not return true if the player only belongs to a "default" faction, such as wilderness.
     * </p>
     * @param p Player to check.
     * @return true if a player is in a faction, false otherwise.
     *
     */
    boolean isPlayerInFaction(Player p);

    /***
     * Retrieve the faction name for a given player.
     *
     * @param p Player to get the faction name for
     * @return a String containing the player's faction name, or null if none.
     */
    String getFactionNameFor(Player p);

    /***
     * Retrieve the faction tag for a given player
     * <p>
     *     This particular api is not used and may be removed in future.
     * </p>
     * @param p Player to get the faction tag for.
     * @return A string containing the faction tag or null if there is none
     */
    String getFactionTagFor(Player p);

    /**
     * Retrieve the faction name for player b determined by the relationship to player a.
     * <p>
     *     This particular api is not used and may be removed in future.
     * </p>
     * @param a Player receiving the name
     * @param b Other player to check relationship with
     * @return A string containing the correct faction name for player b as viewed by player a. Or null if n/a
     */
    String getFactionRelationshipName(Player a, Player b);

    /**
     * Retrieve the faction ChatColor depending on the relationship of Player b to Player a
     * @param a The player receiving the faction information
     * @param b Other player to check relationship with
     * @return A string containing the correct ColorCode (typically) for player b's faction as viewed by player a. Or null.
     */
    String getFactionRelationshipColor(Player a, Player b);

}
