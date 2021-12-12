package me.zombie_striker.qav.finput;

import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.qamini.ParticleHandlers;
import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.armor.BulletProtectionUtil;
import me.zombie_striker.qg.boundingbox.AbstractBoundingBox;
import me.zombie_striker.qg.boundingbox.BoundingBoxManager;
import me.zombie_striker.qg.handlers.BulletWoundHandler;
import me.zombie_striker.qg.handlers.SoundHandler;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class FShootBullet implements FInput {

	public FShootBullet() {
		FInputManager.add(this);
	}
	@Override
	public void onInput(VehicleEntity ve) {
		boolean found = false;
		try {
			me.zombie_striker.qg.ammo.Ammo ammo = QualityArmory.getAmmoByName("556");
			if (ammo != null) {
				for (int i = 0; i < ve.getTrunk().getSize(); i++) {
					ItemStack temp = ve.getTrunk().getItem(i);
					if (temp != null && (QualityArmory.getAmmo(temp) == ammo)) {
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
		@SuppressWarnings("deprecation")
		final Player player = (Player) ve.getDriverSeat().getPassenger();
		if (player != null && found) {
			Vector frontV = QualityArmoryVehicles.rotateRelToCar(ve, ve.getModelEntity(), ve.getType().getCenterFromControlSeat().clone().add(new Vector(ve.getBoundingBox().getWidth()*ve.getDirection().getX(),ve.getDirection().getY(),ve.getDirection().getZ()*ve.getBoundingBox().getWidth())), false);
			Entity e = ve.getDriverSeat();
			Location front = e.getLocation().add(frontV).add((frontV.clone().normalize()));

			final Vector dir = player.getLocation().getDirection().normalize();
			if (dir.getY() < 0) {
				dir.setY(0);
				dir.normalize();
			}
			shootInstantVector(player, 0.3, 3, 2, 300);
			player.getWorld().playSound(front, "bulletbig", 10, 1.0f);

		}
	}

	@Override
	public String getName() {
		return FInputManager.LAUNCHER_556;
	}
	
	

	@SuppressWarnings("deprecation")
	public static void shootInstantVector(Player p, double sway, double damage, int shots, int range) {
		for (int i = 0; i < shots; i++) {
			Location start = p.getEyeLocation().clone();
			Vector normalizedDirection = p.getLocation().getDirection().normalize();
			normalizedDirection.add(new Vector((Math.random() * 2 * sway) - sway, (Math.random() * 2 * sway) - sway,
					(Math.random() * 2 * sway) - sway));
			Vector step = normalizedDirection.clone().multiply(0.2);

			/* Simple values to make it easier on the search
			 posX: go.getX() > 0;
			 posZ: go.getZ() > 0; */
			Entity hitTarget = null;

			boolean headShot = false;

			Location bulletHitLoc = null;

			int maxDistance = (int) getTargetedSolidMaxDistance(step, start, range) / 2;
			double dis2 = maxDistance;

			List<Location> blocksThatWillPLAYBreak = new ArrayList<>();
			List<Location> blocksThatWillBreak = new ArrayList<>();

			Location centerTest = start.clone().add(normalizedDirection.clone().multiply(maxDistance));

			for (Entity e : centerTest.getWorld().getNearbyEntities(centerTest, maxDistance, maxDistance,
					maxDistance)) {
				if (e instanceof Damageable) {
					if (QAMain.avoidTypes.contains(e.getType()))
						continue;

					if (e != p && e != p.getVehicle() && e != p.getPassenger()) {
						double dis = e.getLocation().distance(start);
						if (dis > dis2)
							continue;

						AbstractBoundingBox box = BoundingBoxManager.getBoundingBox(e);

						Location test = start.clone();
						// If the entity is close to the line of fire.
						if (e instanceof Player) {
							Player player = (Player) e;
							if (player.getGameMode() == GameMode.SPECTATOR) {
								continue;
							}
						}
						boolean hit = false;
						// Clear this to make sure
						for (int dist = 0; dist < dis / QAMain.bulletStep; dist++) {
							test.add(step);
							if (box.intersects(p, test, e)) {
								hit = true;
								break;
							}
						}
						if (hit) {
							bulletHitLoc = test;
							dis2 = dis;
							hitTarget = e;
							headShot = box.allowsHeadshots() && box.intersectsHead(test, e);
							if (headShot) {
								QAMain.DEBUG("Headshot!");
								if (QAMain.headshotPling) {
									try {
										p.playSound(p.getLocation(), QAMain.headshot_sound, 2, 1);
										if (!QAMain.isVersionHigherThan(1, 9))
											try {
												p.playSound(p.getLocation(), Sound.valueOf("LAVA_POP"), 6, 1);
											} catch (Error | Exception ignored) {
											}

									} catch (Error | Exception h4) {
										p.playSound(p.getLocation(), Sound.valueOf("LAVA_POP"), 1, 1);
									}
								}
							}
						}
						// }
					}
				}
			}
			if (hitTarget != null) {
				if (!(hitTarget instanceof Player) || QualityArmory.allowGunsInRegion(hitTarget.getLocation())) {

					boolean bulletProtection = false;

					double damageMAX = damage * 1
							* (headShot ? QAMain.HeadshotOneHit ? 50 : 2
									: 1);

					if (hitTarget instanceof Player) {
						bulletProtection = BulletProtectionUtil.stoppedBullet(p, bulletHitLoc, normalizedDirection);
					}


						if (hitTarget instanceof Player) {
							Player player = (Player) hitTarget;
							if (QAMain.enableArmorIgnore) {
								try {
									// damage = damage * ( 1 - min( 20, max( defensePoints / 5, defensePoints -
									// damage / ( toughness / 4 + 2 ) ) ) / 25 )
									double defensePoints = 0;
									double toughness = 0;
									for (ItemStack is : new ItemStack[] { player.getInventory().getHelmet(),
											player.getInventory().getChestplate(), player.getInventory().getLeggings(),
											player.getInventory().getBoots() }) {
										if (is != null) {
											if (is.getItemMeta().getAttributeModifiers(Attribute.GENERIC_ARMOR) != null && !Objects.requireNonNull(is.getItemMeta().getAttributeModifiers(Attribute.GENERIC_ARMOR))
													.isEmpty())
												for (AttributeModifier a : Optional.ofNullable(is.getItemMeta()
														.getAttributeModifiers(Attribute.GENERIC_ARMOR)).orElse(new ArrayList<>()))
													defensePoints += a.getAmount();
											for (AttributeModifier a : Optional.ofNullable(is.getItemMeta()
													.getAttributeModifiers(Attribute.GENERIC_ARMOR_TOUGHNESS)).orElse(new ArrayList<>()))
												toughness += a.getAmount();
										}
									}
									damageMAX = damageMAX * (1 - Math.min(20, Math.max(defensePoints / 5,
											defensePoints - damageMAX / (toughness / 4 + 2))) / 25);
								} catch (Error | Exception ignored) {

								}
							}

							if (!bulletProtection) {
								BulletWoundHandler.bulletHit((Player) hitTarget, 1);
							} else {
								hitTarget.sendMessage(QAMain.S_BULLETPROOFSTOPPEDBLEEDING);
							}
						}

						((Damageable) hitTarget).damage(damageMAX, p);
						if (hitTarget instanceof LivingEntity)
							((LivingEntity) hitTarget).setNoDamageTicks(0);
						QAMain.DEBUG("Damaging entity " + hitTarget.getName());

				}
			} else {
				QAMain.DEBUG("No enities hit.");
			}
			double smokeDistance = 0;
			List<Player> nonheard = start.getWorld().getPlayers();
			nonheard.remove(p);
			double distSqrt = dis2;// Math.sqrt(dis2);
			for (double dist = 0; dist < distSqrt/* (dis2 / Main.bulletStep) */; dist += QAMain.bulletStep) {
				start.add(step);

				boolean solid = isSolid(start);
				if ((solid) && !blocksThatWillPLAYBreak.contains(
						new Location(start.getWorld(), start.getBlockX(), start.getBlockY(), start.getBlockZ()))) {
					blocksThatWillPLAYBreak.add(
							new Location(start.getWorld(), start.getBlockX(), start.getBlockY(), start.getBlockZ()));
				}
				if (QAMain.destructableBlocks.contains(start.getBlock().getType())) {
					blocksThatWillBreak.add(start);
				}

				try {
					int control = 3;
					if (dist % control == 0) {
						List<Player> heard = new ArrayList<>();
						for (Player p2 : nonheard) {
							if (p2.getLocation().distance(start) < control * 2) {
								try {
									start.getWorld().playSound(start, Sound.BLOCK_DISPENSER_LAUNCH, 2, 3);
								} catch (Error e) {
									start.getWorld().playSound(start, Sound.valueOf("SHOOT_ARROW"), 2, 2);
								}
								heard.add(p2);
							}
						}
						for (Player p3 : heard) {
							nonheard.remove(p3);
						}
					}

				} catch (Error | Exception e53) {
					if (dist % 30 == 0) {
						try {
							start.getWorld().playSound(start, Sound.BLOCK_DISPENSER_LAUNCH, 2, 2);
							start.getWorld().playSound(start, Sound.BLOCK_FIRE_EXTINGUISH, 2, 2);
						} catch (Error e) {
							start.getWorld().playSound(start, Sound.valueOf("SHOOT_ARROW"), 2, 2);
							start.getWorld().playSound(start, Sound.valueOf("FIRE_IGNITE"), 2, 2);
						}
					}
				}
				if (!isSolid(start)) {
					if (QAMain.enableBulletTrails)
						if (smokeDistance >= QAMain.smokeSpacing * i) {
							try {
							ParticleHandlers.spawnParticle(1, 1, 1, start);
							}catch(Error|Exception e4432) {
								me.zombie_striker.qg.handlers.ParticleHandlers.spawnParticle(1, 1, 1, start);
							}
							smokeDistance = 0;
						} else {
							smokeDistance += QAMain.bulletStep;
						}
				} else {
					// start.getWorld().spawnParticle(Particle.BLOCK_DUST,start,start.getBlock().getTypeId());
					start.getWorld().playEffect(start, Effect.STEP_SOUND, start.getBlock().getType());
					break;
				}
			}

			for (Location l : blocksThatWillBreak) {
				l.getBlock().breakNaturally();
			}


			// Breaking texture
			if (QAMain.blockBreakTexture)
				for (@SuppressWarnings("unused")
				Location l : blocksThatWillPLAYBreak) {
					start.getWorld().playSound(start, SoundHandler.getSoundWhenShot(start.getBlock()), 2, 1);
				}
		}
	}

	public static double getTargetedSolidMaxDistance(Vector v, Location start, double maxDistance) {
		Location test = start.clone();
		for (int i = 0; i < maxDistance; i++) {
			if (test.getBlock().getType() != Material.AIR) {
				if (isSolid(test))
					return start.distance(test);
			}
			test.add(v);
		}
		return maxDistance;
	}
	public static boolean isSolid(Location loc) {
		if (loc.getBlock().getType().name().contains("RAIL"))
			return false;
		boolean solid;
		try {
			solid = me.zombie_striker.qg.guns.utils.GunUtil.isSolid(loc.getBlock(), loc);
		} catch (Error | Exception e4) {
			solid = loc.getBlock().getType().isSolid();
		}
		return loc.getBlock().getType().name().contains("LEAVES") || loc.getBlock().getType() == Material.GLASS
				|| solid;
	}
}
