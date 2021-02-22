package me.zombie_striker.qav.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

public class WorldGuardUtil {


	@SuppressWarnings("unchecked")
	public static boolean isAllowed(Location loc, Object flag){
	    WorldGuard wGuard = WorldGuard.getInstance();
	    if(loc.getWorld()!=null)
		for (ProtectedRegion k : wGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld())).getRegions().values()) {
			if (k.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
				if (k.getFlag((Flag<State>) flag) == State.DENY)
					return false;
			}
		}
		return true;
	}
}
