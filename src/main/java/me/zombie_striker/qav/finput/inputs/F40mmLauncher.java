package me.zombie_striker.qav.finput.inputs;

import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.finput.FInput;
import me.zombie_striker.qav.finput.FInputManager;
import me.zombie_striker.qav.qamini.ExplosionHandler;
import me.zombie_striker.qav.util.SchedulerUtil;
import me.zombie_striker.qav.qamini.ParticleHandlers;
import me.zombie_striker.qav.util.BlockCollisionUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class F40mmLauncher implements FInput {

	public F40mmLauncher() {
		FInputManager.add(this);
	}

	@Override
	public void onInput(VehicleEntity ve) {
		@SuppressWarnings("deprecation")
		Entity e = ve.getDriverSeat().getPassenger();
		if (!(e instanceof Player)) return;

		final Player player = (Player) e;
		final Vector dir = player.getLocation().getDirection().normalize();
		if (dir.getY() < 0) {
			dir.setY(0);
			dir.normalize();
		}
		onInput(ve, player, player.getEyeLocation().add(dir), dir);
	}

	@Override
	public void onInput(@NotNull VehicleEntity ve, @NotNull Player player, @NotNull Location origin, @NotNull Vector direction) {
		if (!takeAmmo(ve)) return;

		final Vector dir = direction.clone().normalize();
		final Location s = origin.clone();
		player.getWorld().playSound(s, "warheadlaunch", 10, 1.0f);
		// The projectile is handled by the region owning its current position
		SchedulerUtil.runAtMovingLocationTimer(s, new BooleanSupplier() {
			boolean resume = false;
			int distance = 100;
			final int ticks = 3;

			@Override
			public boolean getAsBoolean() {
				dir.setY(dir.getY() - 0.05);
				for (int tick = 0; tick < ticks; tick++) {
					if (!resume) {
						distance--;
						s.add(dir);
					}
					resume = false;
					if (!SchedulerUtil.isOwned(s)) {
						// Moved into another region: continue from there on the next tick
						resume = true;
						return true;
					}
					ParticleHandlers.spawnParticle(1, 1, 1, s);
					boolean entityNear = false;
					try {
						for (Entity near : s.getWorld().getNearbyEntities(s, 1, 1, 1)) {
							if (!isPartOf(ve, player, near)) {
								entityNear = true;
								break;
							}
						}
					} catch (Error ignored) {
					}

					boolean issolid;
					try {
						issolid = me.zombie_striker.qg.guns.utils.GunUtil.isSolid(s.getBlock(), s);
					}catch(Error|Exception e4) {
						issolid = BlockCollisionUtil.isSolid(s);
					}
					if (issolid || entityNear || distance < 0) {
						ExplosionHandler.handleAOEExplosion(SchedulerUtil.isOwned(player) ? player : null, s, 100, 3);
						ParticleHandlers.spawnExplosion(s);
						try {
							s.getWorld().playSound(s, "warheadexplode", 10, 1.5f);
							s.getWorld().playSound(s, Sound.ENTITY_GENERIC_EXPLODE, 8, 0.7f);
						} catch (Error e3) {
							s.getWorld().playEffect(s, Effect.valueOf("CLOUD"), 0);
							s.getWorld().playSound(s, Sound.valueOf("EXPLODE"), 8, 0.7f);
						}
						return false;
					}
				}
				return true;
			}
		});
	}

	/**
	 * @return true if the entity is the shooter, or a part or a rider of the vehicle firing
	 */
	private static boolean isPartOf(VehicleEntity ve, Player shooter, Entity entity) {
		if (entity == shooter) return true;
		if (QualityArmoryVehicles.getVehicleEntityByEntity(entity) == ve) return true;
		return entity.getVehicle() != null && QualityArmoryVehicles.getVehicleEntityByEntity(entity.getVehicle()) == ve;
	}

	private static boolean takeAmmo(VehicleEntity ve) {
		boolean found = false;
		try {
			me.zombie_striker.qg.ammo.Ammo ammo = me.zombie_striker.qg.api.QualityArmory.getAmmoByName("40mm");
			if (ammo != null) {
				for (int i = 0; i < ve.getTrunk().getSize(); i++) {
					ItemStack temp = ve.getTrunk().getItem(i);
					if (temp != null && (me.zombie_striker.qg.api.QualityArmory.getAmmo(temp) == ammo)) {
						found = true;
						if (temp.getAmount() > 1) {
							temp.setAmount(temp.getAmount() - 1);
						} else {
							temp = null;
						}
						ve.getTrunk().setItem(i, temp);
						break;
					}
				}
			}
		} catch (Error | Exception ignored) {
		}
		if (!found) {
			for (int i = 0; i < ve.getTrunk().getSize(); i++) {
				ItemStack temp = ve.getTrunk().getItem(i);
				if (temp != null && temp.getType() == Material.TNT) {
					found = true;
					if (temp.getAmount() > 1) {
						temp.setAmount(temp.getAmount() - 1);
					} else {
						temp = null;
					}
					ve.getTrunk().setItem(i, temp);
					break;
				}
			}
		}
		return found;
	}

	@Override
	public String getName() {
		return FInputManager.LAUNCHER_40mm;
	}

}
