package me.zombie_striker.qav.hooks.implementation;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.zombie_striker.qav.hooks.ProtectionHook;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHook implements ProtectionHook {
    private final RegionContainer regionContainer;
    private final WorldGuard worldGuard;
    private final WorldGuardPlugin plugin;

    public WorldGuardHook() {
        worldGuard = WorldGuard.getInstance();
        regionContainer = worldGuard.getPlatform().getRegionContainer();
        plugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
    }

    @Override
    public boolean canBuild(Player player, Location location) {
        LocalPlayer localPlayer = wrapPlayer(player);

        return regionContainer.createQuery().testBuild(BukkitAdapter.adapt(location), localPlayer, Flags.BLOCK_PLACE)
                || worldGuard.getPlatform()
                .getSessionManager()
                .hasBypass(localPlayer, BukkitAdapter.adapt(player.getWorld())
                );
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        LocalPlayer localPlayer = wrapPlayer(player);
        return regionContainer.createQuery().testBuild(BukkitAdapter.adapt(location), localPlayer, Flags.BLOCK_BREAK)
                || worldGuard.getPlatform()
                .getSessionManager()
                .hasBypass(localPlayer, BukkitAdapter.adapt(player.getWorld())
                );
    }

    private LocalPlayer wrapPlayer(Player player) {
        return plugin.wrapPlayer(player);
    }
}
