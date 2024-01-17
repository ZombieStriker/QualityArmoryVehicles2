package me.zombie_striker.qav.hooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ProtectionHook {
    boolean canMove(Player player, Location location);
    boolean canPlace(Player player, Location location);
    boolean canRemove(Player player, Location location);
}
