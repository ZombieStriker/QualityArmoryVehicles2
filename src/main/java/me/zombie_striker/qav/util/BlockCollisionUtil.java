package me.zombie_striker.qav.util;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class BlockCollisionUtil {
	private static final ExpiringMap<Location,Material> CACHE;
	private static final HashMap<Material,Double> customBlockHeights = new HashMap<>();

	static{
		 CACHE = ExpiringMap.builder()
				.expiration(5, TimeUnit.MINUTES)
				.expirationPolicy(ExpirationPolicy.CREATED)
				.build();

		for(Material m : Material.values()){
			if(m.name().endsWith("_WALL"))
				customBlockHeights.put(m,1.5);
			if(m.name().endsWith("_FENCE_GATE")||m.name().endsWith("_FENCE"))
				customBlockHeights.put(m,1.5);
			if(m.name().endsWith("_BED"))
				customBlockHeights.put(m,0.5);
			if(m.name().endsWith("_SLAB")||m.name().endsWith("_FENCE"))
				customBlockHeights.put(m,0.5);
			if(m.name().endsWith("DAYLIGHT_DETECTOR"))
				customBlockHeights.put(m,0.4);
			if(m.name().endsWith("CARPET"))
				customBlockHeights.put(m,0.1);
			if(m.name().endsWith("TRAPDOOR"))
				customBlockHeights.put(m,0.2);
			if(m.name().endsWith("RAIL"))
				customBlockHeights.put(m,0.0);
		}
	}

	public static double getHeight(Block b){
		Material type = getMaterial(b.getLocation());
		if (type.name().contains("SLAB") || type.name().contains("STEP")) {
			if (b.getData() == 0)
				return 0.5;
			if (b.getData() == 1)
				return 1;
		}
		if(customBlockHeights.containsKey(type))
			return customBlockHeights.get(type);
		return type.isSolid()?1:0;
	}

	public static boolean isSolidAt(Location loc){
		Block b = loc.getBlock();
		if(b.getLocation().getY()+getHeight(b)>loc.getY())
			return true;
		Block temp = b.getRelative(0,-1,0);
		return temp.getLocation().getY() + getHeight(temp) > loc.getY();
	}


	public static @NotNull Material getMaterial(Location location) {
		if (CACHE.containsKey(location)) {
			return CACHE.get(location);
		}

		Material material = location.getBlock().getType();
		CACHE.put(location,material);
		return material;
	}

	public static boolean isSolid(Location loc) {
		return isSolid(getMaterial(loc));
	}

	public static boolean isSolid(Material material) {
		return material.isSolid();
	}
}
