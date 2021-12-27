package me.zombie_striker.qav.hooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ProtectionHook {
    boolean canBreak(Player player, Location location);
}
