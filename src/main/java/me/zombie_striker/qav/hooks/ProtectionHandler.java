package me.zombie_striker.qav.hooks;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.hooks.implementation.TownyHook;
import me.zombie_striker.qav.hooks.implementation.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class ProtectionHandler {
    private final static Set<ProtectionHook> compatibilities = new HashSet<>();

    public static void init() {
        hook("WorldGuard", WorldGuardHook::new);
        hook("Towny", TownyHook::new);
    }

    public static boolean canBreak(Player player, Location target) {
        return compatibilities.stream().allMatch(compatibility -> compatibility.canBreak(player, target));
    }

    public static void hook(String plugin, CompatibilityConstructor constructor) {
        if (Bukkit.getPluginManager().isPluginEnabled(plugin) && (boolean) Main.a("hooks." + plugin, true)) {
            compatibilities.add(constructor.create());
        }
    }

    @FunctionalInterface
    private interface CompatibilityConstructor {
        ProtectionHook create();
    }
}
