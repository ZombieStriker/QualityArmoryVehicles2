package me.zombie_striker.qav;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.api.events.VehicleDamageEvent;
import me.zombie_striker.qav.api.events.VehicleDestroyEvent;
import me.zombie_striker.qav.api.events.VehicleRepairEvent;
import me.zombie_striker.qav.hooks.model.Animation;
import me.zombie_striker.qav.menu.OverviewMenu;
import me.zombie_striker.qav.perms.PermissionHandler;
import me.zombie_striker.qav.qamini.ParticleHandlers;
import me.zombie_striker.qav.util.ForksUtil;
import me.zombie_striker.qav.util.VehicleUtils;
import me.zombie_striker.qav.vehicles.AbstractCar;
import me.zombie_striker.qav.vehicles.AbstractHelicopter;
import me.zombie_striker.qav.vehicles.AbstractPlane;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class QAVListener implements Listener {

	private Main main;

	public QAVListener(Main main) {
		this.main = main;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClickVehicle(PlayerInteractEvent e) {
		if (e.getPlayer().getVehicle() != null) {
			return;
		}

		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Main.DEBUG("Player is interacting.");
			VehicleEntity ve = QualityArmoryVehicles.getVehiclePlayerLookingAt(e.getPlayer());
			if (ve == null) {
				return;
			}

			Main.DEBUG("Detected hitbox interaction.");

			e.setCancelled(true);

			ItemStack item = e.getPlayer().getInventory().getItemInMainHand();

			if (e.getPlayer().hasPermission("qualityarmoryvehicles.repair") && Main.repairItem.isItem(item)) {

				if (ve.getHealth() >= ve.getType().getMaxHealth()) {
					return;
				}

				VehicleRepairEvent event = new VehicleRepairEvent(e.getPlayer(), item, ve);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) return;

				ve.setHealth(ve.getType().getMaxHealth());
				e.getPlayer().sendMessage(Main.prefix + MessagesConfig.MESSAGE_REPAIR);
				return;
			}

			if (e.getPlayer().isSneaking() && ve.allowUserDriver(e.getPlayer().getUniqueId())) {
				new OverviewMenu(e.getPlayer(), ve).open();
				return;
			}

			if (ve.allowUserDriver(e.getPlayer().getUniqueId()) && ve.getDriverSeat().getPassenger() == null) {
				ve.getType().playAnimation(ve, Animation.AnimationType.ENTER, "driver");
				ve.getDriverSeat().setPassenger(e.getPlayer());
			}
		}

	}

	@EventHandler
	public void onPlace(PlayerInteractEvent e) {
		if(e.getHand() == EquipmentSlot.OFF_HAND)
			return;
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			AbstractVehicle vehicle = QualityArmoryVehicles.getVehicleByItem(e.getItem());

			if (vehicle != null) {
				e.setCancelled(true);
				VehicleEntity ve = QualityArmoryVehicles.spawnVehicle(vehicle,e.getClickedBlock().getRelative(BlockFace.UP).getLocation(), e.getPlayer());

				if (ve == null) {
					return;
				}

				if(e.getPlayer().getGameMode() != GameMode.CREATIVE){
					e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
				}
			}
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		if (!ForksUtil.isFlyKick(event)) return;
		if (event.getPlayer().getVehicle() == null) return;
		if (!QualityArmoryVehicles.isVehicle(event.getPlayer().getVehicle())) return;

		event.setCancelled(true);
		Main.DEBUG("Cancelled kick event for flying because player is on plane.");
	}

	@EventHandler
	public void oninteractEntity(PlayerInteractEntityEvent e) {
		if (e.getPlayer().getVehicle() == null) {
			VehicleEntity ve = QualityArmoryVehicles.getVehicleEntityByEntity(e.getRightClicked());

			if (ve == null && e.getRightClicked() instanceof Player && e.getRightClicked().getVehicle() != null) {
				ve = QualityArmoryVehicles.getVehicleEntityByEntity(e.getRightClicked().getVehicle());
			}

			if (ve != null) {
				e.setCancelled(true);
				if (e.getPlayer().isSneaking() && ve.allowUserDriver(e.getPlayer().getUniqueId())) {
					new OverviewMenu(e.getPlayer(), ve).open();
				} else {
					if (ve.allowUserDriver(e.getPlayer().getUniqueId()) && ve.getDriverSeat().getPassenger() == null) {
						ve.getType().playAnimation(ve, Animation.AnimationType.ENTER, "driver");
						ve.getDriverSeat().setPassenger(e.getPlayer());
					}
				}
			}
		}
	}

	@EventHandler
	public void onManipulate(PlayerArmorStandManipulateEvent e) {
		VehicleEntity ve = null;
		if (QualityArmoryVehicles.isPassager(e.getRightClicked())) {

			for (VehicleEntity ve2 : Main.vehicles) {
				if (ve2.getPassagers().containsValue(e.getRightClicked())) {
					ve = ve2;
					break;
				}
			}

		} else if (QualityArmoryVehicles.isVehicle(e.getRightClicked())) {
			ve = QualityArmoryVehicles.getVehicleEntityByEntity(e.getRightClicked());
		}

		if (ve == null) {
			return;
		}

		e.setCancelled(true);
		if (ve.allowUserPassager(e.getPlayer().getUniqueId())) {
			if (e.getPlayer().isSneaking() && ve.allowUserDriver(e.getPlayer().getUniqueId())) {
				if (e.getPlayer().hasPermission(PermissionHandler.PERM_OPEN_VEHICLE_GUI)) {
					new OverviewMenu(e.getPlayer(), ve).open();
				} else {
					e.getPlayer()
							.sendMessage(ChatColor.RED + " You do not have permission to use this vehicle.");
				}
			} else {
				if (e.getPlayer().hasPermission("qualityarmoryvehicles.use")) {
					QualityArmoryVehicles.addPlayerToCar(ve, e.getPlayer(),
							ve.allowUserDriver(e.getPlayer().getUniqueId()));
				} else {
					e.getPlayer()
							.sendMessage(ChatColor.RED + " You do not have permission to use this vehicle.");
				}
			}
		} else if (e.getPlayer().hasPermission(PermissionHandler.PERM_OVERRIDE_WHITELIST) || VehicleUtils.isOverrideWhitelisted(e.getPlayer().getUniqueId())) {
			if (ve.getDriverSeat().getPassenger() == null) {
				if (e.getPlayer().hasPermission("qualityarmoryvehicles.use")) {
					ve.getDriverSeat().setPassenger(e.getPlayer());
				} else {
					e.getPlayer().sendMessage(ChatColor.RED + " You do not have permission to use this vehicle.");
				}

				return;
			}

			QualityArmoryVehicles.addPlayerToCar(ve, e.getPlayer(),
					ve.allowUserDriver(e.getPlayer().getUniqueId()));
		}
	}


	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (!Main.enableVehiclePlayerCollision) {
			return;
		}

		for (Entity ent : e.getPlayer().getNearbyEntities(5, 5, 5)) {
			if (QualityArmoryVehicles.isVehicle(ent)) {
				VehicleEntity ve = QualityArmoryVehicles.getVehicleEntityByEntity(ent);
				if (ve == null) {
					return;
				}

				if (QualityArmoryVehicles.isWithinVehicle(e.getTo(), ve)
						&& !QualityArmoryVehicles.isWithinVehicle(e.getFrom(), ve)) {
					if (e.getTo().getX() != e.getFrom().getX() || e.getTo().getZ() != e.getFrom().getZ()) {
						if (e.getPlayer().getVelocity().getY() < -0.05) {
							e.getPlayer().setVelocity(e.getPlayer().getVelocity().setY(0.3));
						}
					}
					e.setCancelled(true);
					break;

				} else {
					Location to = e.getTo().clone().add(0, 1, 0);
					Location from = e.getFrom().clone().add(0, 1, 0);
					if (QualityArmoryVehicles.isWithinVehicle(to, ve)
							&& !QualityArmoryVehicles.isWithinVehicle(from, ve)) {
						if (e.getTo().getX() != e.getFrom().getX() || e.getTo().getZ() != e.getFrom().getZ()) {
							if (e.getPlayer().getVelocity().getY() < -0.05) {
								e.getPlayer().setVelocity(e.getPlayer().getVelocity().setY(0.3));
							}
						}
						e.setCancelled(true);
						break;

					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onDamage(EntityDamageEvent e) {
		handleDamage(e, e.getEntity(), e.getDamage(), e.getCause(), e::setDamage);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Entity vehicle = event.getPlayer().getVehicle();
		if (QualityArmoryVehicles.isVehicle(vehicle) || QualityArmoryVehicles.isPassager(vehicle)) {
			VehicleEntity entity = QualityArmoryVehicles.getVehicleEntityByEntity(vehicle);
			if (entity != null) {
				if (vehicle.equals(entity.getDriverSeat())) {
					entity.getDriverSeat().eject();

					if (Main.destroyVehicleONLEAVE) {
						entity.deconstruct(event.getPlayer(), "Quit");
					}

					if (Main.removeVehicleONLEAVE) {
						VehicleUtils.callback(entity, event.getPlayer(), "Quit");
					}
				}

				if (entity.getPassagerSeats().contains(vehicle)) {
					vehicle.eject();
					vehicle.remove();
				}
			}
		}
	}

	public static void handleDamage(Cancellable e, Entity entity, double damage, EntityDamageEvent.DamageCause cause, Consumer<Double> setDamage) {
		if (entity.getVehicle() != null && QualityArmoryVehicles.isVehicle(entity.getVehicle()) || QualityArmoryVehicles.isPassager(entity.getVehicle())) {
			VehicleEntity ve = QualityArmoryVehicles.getVehicleEntityByEntity(entity.getVehicle());
			if (ve != null && ve.getType() != null)
				if ((ve.getType() instanceof AbstractHelicopter || ve.getType() instanceof AbstractPlane
						|| ve.getType() instanceof AbstractCar) && cause == EntityDamageEvent.DamageCause.FALL) {
					e.setCancelled(true);
					return;
				}
		}

		VehicleEntity ve = null;
		// Get the vehicle
		if (QualityArmoryVehicles.isVehicle(entity)) {
			ve = QualityArmoryVehicles.getVehicleEntityByEntity(entity);
		} else if (entity.getVehicle() != null && QualityArmoryVehicles.isVehicle(entity.getVehicle())) {
			ve = QualityArmoryVehicles.getVehicleEntityByEntity(entity.getVehicle());
		}

		if (ve == null) {
			return;
		}

		if (!Main.enableVehicleDamage) {
			e.setCancelled(true);
			return;
		}

		if (cause == EntityDamageEvent.DamageCause.SUFFOCATION || cause == EntityDamageEvent.DamageCause.DROWNING
				|| cause == EntityDamageEvent.DamageCause.FALL) {
			e.setCancelled(true);
			return;
		}

		if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK && e instanceof EntityDamageByEntityEvent) {
			if (((EntityDamageByEntityEvent) e).getDamager().getType() == EntityType.ENDERMITE) {
				e.setCancelled(true);
				return;
			}
		}

		VehicleDamageEvent vde = new VehicleDamageEvent(ve, damage);
		Bukkit.getPluginManager().callEvent(vde);

		if (vde.isCanceled()) {
			e.setCancelled(true);
			return;
		}

		Main.DEBUG("Damaged vehicle: " + vde.getDamage() + " || Health= " + ve.getHealth() + " || Cause= "
				+ cause.name());

		setDamage.accept(vde.getDamage());
		ve.setHealth((float) (ve.getHealth() - vde.getDamage()));
		e.setCancelled(true);

		try {
			entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1);
		} catch (Error | Exception e4) {
			try {
				entity.getWorld().playSound(entity.getLocation(), Sound.valueOf("HURT"), 1f, 1);
			} catch (Error | Exception ignored) {
			}
		}

		if (ve.getHealth() <= 0) {

			VehicleDestroyEvent vehicleDestroyEvent = new VehicleDestroyEvent(ve);
			Bukkit.getPluginManager().callEvent(vehicleDestroyEvent);

			if (vehicleDestroyEvent.isCanceled()) {
				e.setCancelled(true);
				return;
			}

			if (!Main.freezeOnDestroy) {
				ve.deconstruct(null,"Destroy");
			}

			ve.getType().playAnimation(ve, Animation.AnimationType.BREAK);

			try {
				ParticleHandlers.spawnMushroomCloud(entity.getLocation());
			} catch (Error | Exception ignored) {}

			try {
				entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.5f,
						1);
			} catch (Error | Exception e4) {
				try {
					entity.getWorld().playSound(entity.getLocation(), Sound.valueOf("EXPLODE"), 2.5f,
							1);
				} catch (Error | Exception ignored) {
				}
			}
		}
	}
}
