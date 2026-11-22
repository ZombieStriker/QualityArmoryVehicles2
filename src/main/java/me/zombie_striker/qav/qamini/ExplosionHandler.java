
package me.zombie_striker.qav.qamini;

import me.zombie_striker.qav.util.SchedulerUtil;
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
				// Folia: entities owned by another region can't be accessed
				if (!SchedulerUtil.isOwned(e)) continue;

				Damageable d = (Damageable) e;
				double amount = damage*radius/e.getLocation().distanceSquared(loc);
				if (shooter != null) d.damage(amount, shooter);
				else d.damage(amount);
			}
		}
	}
}
