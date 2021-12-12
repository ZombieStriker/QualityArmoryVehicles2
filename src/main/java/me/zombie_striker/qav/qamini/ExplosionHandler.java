
package me.zombie_striker.qav.qamini;

import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;


public class ExplosionHandler {

	public static void handleExplosion(Location origin, int radius, int power) {
			origin.getWorld().createExplosion(origin, power);
	}
	
	public static void handleAOEExplosion(Entity shooter, Location loc, double damage, double radius) {
		for(Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
			if(e instanceof Damageable) {
				Damageable d = (Damageable) e;
				d.damage(damage*radius/e.getLocation().distanceSquared(loc), shooter);
			}
		}
	}
}
