package net.cyberkitsune.prefixchat.adapters;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import org.bukkit.entity.Player;

@FactionConnector(classname = "com.massivecraft.factions.RelationParticipator")
public class Factions3Adapter implements FactionAdapter {

    @Override
    public boolean isPlayerInFaction(Player p) {
        MPlayer mp = MPlayer.get(p.getUniqueId());
        return !mp.getFaction().isNone();
    }

    @Override
    public String getFactionNameFor(Player p) {
        MPlayer mp = MPlayer.get(p.getUniqueId());
        Faction fac = mp.getFaction();
        return fac.getName();
    }

    @Override
    public String getFactionTagFor(Player p) {
        return ""; // Not supported
    }

    @Override
    public String getFactionRelationshipName(Player a, Player b) {
        return ""; // Not supported
    }

    @Override
    public String getFactionRelationshipColor(Player a, Player b) {
        MPlayer ma = MPlayer.get(a.getUniqueId());
        MPlayer mb = MPlayer.get(b.getUniqueId());
        if(!ma.getFaction().isNone())
        {
            Faction fac = ma.getFaction();
            return fac.getColorTo(mb).toString();
        }
        else
        {
            return "";
        }

    }
}
