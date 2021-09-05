package me.zombie_striker.qav.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;

public class BlockCollisionUtil {

	private static final HashMap<Material,Double> customBlockHeights = new HashMap<>();

	static{
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
		Material type = b.getType();
		if (b.getType().name().contains("SLAB") || b.getType().name().contains("STEP")) {
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
		if(temp.getLocation().getY()+getHeight(temp)>loc.getY())
			return true;
		return false;
	}


	public static boolean isSolid(Location loc) {
		return loc.getBlock().getType().isSolid();
	}
}
