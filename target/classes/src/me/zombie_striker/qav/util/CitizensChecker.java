package me.zombie_striker.qav.util;

import org.bukkit.entity.Entity;

public class CitizensChecker {

	public static boolean isCitizens(Entity e) {
		try {
			return net.citizensnpcs.api.CitizensAPI.getNPCRegistry().getNPC(e) != null;
		} catch (Error | Exception e4) {
		}
		return false;
	}
}
