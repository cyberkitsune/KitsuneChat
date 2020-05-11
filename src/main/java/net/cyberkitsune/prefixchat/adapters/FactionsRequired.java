package net.cyberkitsune.prefixchat.adapters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Required for FactionAdapters to work.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FactionsRequired {
    /**
     * Gets the name of a class that can be used to check a factions plugin is present.
     * For example, "com.massivecraft.factions.RelationParticipator"
     * @return A string containing a class name used to check factions.
     */
    String classname();
}
