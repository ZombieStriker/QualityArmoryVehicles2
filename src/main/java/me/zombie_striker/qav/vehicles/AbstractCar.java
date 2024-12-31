package me.zombie_striker.qav.vehicles;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.api.events.VehicleChangeSpeedEvent;
import me.zombie_striker.qav.api.events.VehicleTurnEvent;
import me.zombie_striker.qav.util.HeadPoseUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class AbstractCar extends AbstractVehicle {


	private HashMap<UUID, Long> lastSoundBreak = new HashMap<>();
	private HashMap<UUID, Long> lastSoundDrive = new HashMap<>();

	public AbstractCar(String name, int id) {
		super(name, id);
	}

	@Override
	public void handleTurnLeft(VehicleEntity ve, Player player) {
		VehicleTurnEvent e = new VehicleTurnEvent(ve,ve.getAngleRotation(), ve.getAngleRotation() + ve.getType().getRotationDelta());
		Bukkit.getPluginManager().callEvent(e);
		if(e.isCanceled())
			return;
		ve.setAngle((ve.getAngleRotation() + ve.getType().getRotationDelta()) * this.getRotationMultiplier());
		HeadPoseUtil.setHeadPoseUsingReflection(ve);
	}

	@Override
	public void handleTurnRight(VehicleEntity ve, Player player) {
		VehicleTurnEvent e = new VehicleTurnEvent(ve,ve.getAngleRotation(), ve.getAngleRotation() - ve.getType().getRotationDelta());
		Bukkit.getPluginManager().callEvent(e);
		if(e.isCanceled())
			return;
		ve.setAngle((ve.getAngleRotation() - ve.getType().getRotationDelta()) * this.getRotationMultiplier());
		HeadPoseUtil.setHeadPoseUsingReflection(ve);
	}

	@Override
	public void handleSpeedIncrease(VehicleEntity ve, Player player) {
		VehicleChangeSpeedEvent e = new VehicleChangeSpeedEvent(ve,ve.getSpeed(), Math.min(ve.getSpeed() + 0.1, ve.getType().getMaxSpeed()));
		Bukkit.getPluginManager().callEvent(e);
		if(e.isCanceled())
			return;
		if (!this.handleFuel(ve,player)) {
			return;
		}
		ve.setSpeed(Math.min(ve.getSpeed() + 0.1, ve.getType().getMaxSpeed()));
			if (!lastSoundDrive.containsKey(player.getUniqueId())
					|| System.currentTimeMillis() - lastSoundDrive.get(player.getUniqueId()) > 900) {
				lastSoundDrive.put(player.getUniqueId(), System.currentTimeMillis());
				ve.getDriverSeat().getLocation().getWorld().playSound(ve.getDriverSeat().getLocation(), getSound(), (float) getSoundVolume(), 1);
			}
	}

	@Override
	public void handleSpeedDecrease(VehicleEntity ve, Player player) {
		VehicleChangeSpeedEvent e = new VehicleChangeSpeedEvent(ve,ve.getSpeed(), Math.max(ve.getSpeed() - 0.1, -ve.getType().getMaxSpeed()));
		Bukkit.getPluginManager().callEvent(e);
		if(e.isCanceled())
			return;
		if (!this.handleFuel(ve,player)) {
			return;
		}
		ve.setSpeed(Math.max(ve.getSpeed() - 0.1, -ve.getType().getMaxBackupSpeed()));

	}

	@Override
	public void handleSpace(VehicleEntity ve, Player player) {
		if(ve.getSpeed()>0) {
			ve.setSpeed(Math.max(ve.getSpeed() - 0.1, -ve.getType().getMaxBackupSpeed()));
		}else{
			ve.setSpeed(Math.max(ve.getSpeed() + 0.1, -ve.getType().getMaxSpeed()));
		}

		if (canPlaySkidSounds()) {
			if (ve.getSpeed() > 0.2 || ve.getSpeed() <-0.2) {
				if (!lastSoundBreak.containsKey(player.getUniqueId())
						|| System.currentTimeMillis() - lastSoundBreak.get(player.getUniqueId()) > 2000) {
					lastSoundBreak.put(player.getUniqueId(), System.currentTimeMillis());
					try {
						player.getWorld().playSound(player.getLocation(),
								me.zombie_striker.qg.guns.utils.WeaponSounds.CARSKID.getSoundName(), (float) ve.getType().getSoundVolume(), (float) 1.3);
					} catch (Error | Exception e4) {
						player.getWorld().playSound(player.getLocation(), "carskid", 0.7f,
								(float) (2.3));
					}
				}
			}
		}
	}

	@Override
	public void tick(VehicleEntity vehicleEntity) {
		if (vehicleEntity.getDriverSeat() == null) return;

		basicDirections(vehicleEntity,canJump(),false);
		if(Main.destroyOnWater && vehicleEntity.isSubmerged()){
			vehicleEntity.deconstruct(null,"UnderWater");
			if (Main.enableGarage) {
				QualityArmoryVehicles.removeUnlockedVehicle(Bukkit.getPlayer(vehicleEntity.getOwner()), vehicleEntity.getType());
			}
		}
	}

}
