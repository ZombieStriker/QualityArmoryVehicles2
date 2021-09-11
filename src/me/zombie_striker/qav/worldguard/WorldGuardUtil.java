package me.zombie_striker.qav.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardUtil {
	public static final StateFlag SPAWNING = new StateFlag("qav-allow-spawning", true);
	private static WorldGuardPlugin plugin;
	private static boolean initialized = false;

	public static void init() {
		if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
			initialized = true;
			plugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
			FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
			registry.register(SPAWNING);
			QualityArmoryVehicles.getPlugin().getLogger().info("WorldGuard support loaded.");
		} else {
			initialized = false;
		}
	}

	public static boolean isAllowed(Player player, Location loc, StateFlag flag){
		if (!initialized) return true;

		LocalPlayer localPlayer = plugin.wrapPlayer(player);
		com.sk89q.worldedit.util.Location location = BukkitAdapter.adapt(loc);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();

		return query.testState(location, localPlayer, flag);
	}
}
