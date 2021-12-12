package me.zombie_striker.qav.hooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ProtectionHook {
    boolean canBuild(Player player, Location location);
    boolean canBreak(Player player, Location location);
}
