package me.zombie_striker.qav.finput.inputs;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.finput.FInput;
import me.zombie_striker.qav.finput.FInputManager;
import me.zombie_striker.qav.qamini.ExplosionHandler;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class FTNTBomber implements FInput {

	public FTNTBomber() {
		FInputManager.add(this);
	}

	@Override
	public void onInput(VehicleEntity ve) {
		boolean found = false;
		try {
			me.zombie_striker.qg.ammo.Ammo ammo = me.zombie_striker.qg.api.QualityArmory.getAmmoByName("mininuke");
			if (ammo != null) {
				for (int i = 0; i < ve.getTrunk().getSize(); i++) {
					ItemStack temp = ve.getTrunk().getItem(i);
					if (temp != null && (me.zombie_striker.qg.api.QualityArmory.getAmmo(temp) == ammo)) {
						found = true;
						ve.getTrunk().setItem(i, null);
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
		if (found) {
			final Location s = ve.getDriverSeat().getLocation().add(QualityArmoryVehicles
							.rotateRelToCar(ve.getModelEntity(), ve.getType().getCenterFromControlSeat(), false))
					.subtract(0, 1.7, 0);
			@SuppressWarnings("deprecation")
			final Player player = (Player) ve.getDriverSeat().getPassenger();
			final Vector dir = new Vector(0, -0.1, 0);
			// final Vector dir
			Main.foliaLib.getScheduler().runAtEntityTimer(ve.getModelEntity(), new Consumer<WrappedTask>() {
				int distance = 300;

				@Override
				public void accept(WrappedTask task) {
					dir.setY(dir.getY() - 0.05);
					for (int tick = 0; tick < Math.abs(dir.getY()); tick++) {
						distance--;
						s.add(dir);
						ParticleHandlers.spawnParticle(1, 1, 1, s);// .spawnGunParticles(g, s);
						boolean entityNear = false;
						try {
							List<Entity> e2 = new ArrayList<>(s.getWorld().getNearbyEntities(s, 1, 1, 1));
							if (!e2.isEmpty())
								if (e2.size() > 1 || e2.get(0) != player && (!e2.contains(ve.getDriverSeat())
										|| e2.size() > 1 + ve.getPassagerSeats().size()))
									entityNear = true;
						} catch (Error ignored) {
						}
						boolean issolid;
						try {
							issolid = me.zombie_striker.qg.guns.utils.GunUtil.isSolid(s.getBlock(), s);
						} catch (Error | Exception e4) {
							issolid = BlockCollisionUtil.isSolid(s);
						}
						if (issolid || entityNear || distance < 0) {
							ExplosionHandler.handleAOEExplosion(player, s, 100, 8);
							ParticleHandlers.spawnExplosion(s);
							try {
								ve.getDriverSeat().getWorld().playSound(s, "warheadexplode", 10, 1.5f);
								ve.getDriverSeat().getWorld().playSound(s, Sound.ENTITY_GENERIC_EXPLODE, 8, 0.7f);
							} catch (Error e3) {
								s.getWorld().playEffect(s, Effect.valueOf("CLOUD"), 0);
								s.getWorld().playSound(s, Sound.valueOf("EXPLODE"), 8, 0.7f);
							}
							task.cancel();
							return;
						}
					}
				}
			}, 0, 1);
		}
	}

	@Override
	public String getName() {
		return FInputManager.TNTBOMBER;
	}

}
