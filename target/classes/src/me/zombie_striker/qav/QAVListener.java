package me.zombie_striker.qav;

import com.comphenix.protocol.wrappers.EnumWrappers;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.menu.MenuHandler;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class QAVListener implements Listener {

	private Main main;

	public QAVListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onClickVehicle(PlayerInteractEvent e) {
		if (e.getPlayer().getVehicle() == null) {
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				VehicleEntity ve = QualityArmoryVehicles.getVehiclePlayerLookingAt(e.getPlayer());
				if (ve != null) {
					if (e.getPlayer().isSneaking()) {
						MenuHandler.openOverview(e.getPlayer(), ve);
					} else {
						ve.getDriverSeat().setPassenger(e.getPlayer());
						return;
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlace(PlayerInteractEvent e) {
		if(e.getHand() == EquipmentSlot.OFF_HAND)
			return;
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (QualityArmoryVehicles.isVehicleByItem(e.getItem())) {
				AbstractVehicle vehicle = QualityArmoryVehicles.getVehicleByItem(e.getItem());
				VehicleEntity ve = new VehicleEntity(vehicle, e.getClickedBlock().getRelative(BlockFace.UP).getLocation(), e.getPlayer().getUniqueId());
				ve.spawn();

				if(e.getPlayer().getGameMode() != GameMode.CREATIVE){
					e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
				}
			}
		}
	}

	@EventHandler
	public void oninteractEntity(PlayerInteractEntityEvent e) {
		if (e.getPlayer().getVehicle() == null) {
			VehicleEntity ve = QualityArmoryVehicles.getVehicleEntityByEntity(e.getRightClicked());
			if (ve != null) {
				e.setCancelled(true);
				if (e.getPlayer().isSneaking()) {
					MenuHandler.openOverview(e.getPlayer(), ve);
				} else {
					ve.getDriverSeat().setPassenger(e.getPlayer());
					return;
				}
			}
		}
	}

	@EventHandler
	public void onManipulate(PlayerArmorStandManipulateEvent e) {
		if (e.getPlayer().getVehicle() == null) {
			VehicleEntity ve = QualityArmoryVehicles.getVehicleEntityByEntity(e.getRightClicked());
			if (ve != null) {
				e.setCancelled(true);
				if (e.getPlayer().isSneaking()) {
					MenuHandler.openOverview(e.getPlayer(), ve);
				} else {
					ve.getDriverSeat().setPassenger(e.getPlayer());
					return;
				}
			}
		}
	}


	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (Main.enableVehiclePlayerCollision)
			for (Entity ent : e.getPlayer().getNearbyEntities(10, 10, 10)) {
				if (QualityArmoryVehicles.isVehicle(ent)) {
					VehicleEntity ve = QualityArmoryVehicles.getVehicleEntityByEntity(ent);
					if (ve != null) {
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
	}


}
