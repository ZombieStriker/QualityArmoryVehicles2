package me.zombie_striker.qav.vehicles;

import com.comphenix.protocol.events.PacketEvent;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.events.VehicleChangeSpeedEvent;
import me.zombie_striker.qav.api.events.VehicleTurnEvent;
import me.zombie_striker.qav.util.HeadPoseUtil;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class AbstractCar extends AbstractVehicle {


	private HashMap<UUID, Long> lastSoundBreak = new HashMap<>();
	private HashMap<UUID, Long> lastSoundDrive = new HashMap<>();

	public AbstractCar(String name, int id) {
		super(name, id);
	}

	@Override
	public void handleTurnLeft(VehicleEntity ve, PacketEvent event) {
		VehicleTurnEvent e = new VehicleTurnEvent(ve,ve.getAngleRotation(), ve.getAngleRotation() + ve.getType().getRotationDelta());
		Bukkit.getPluginManager().callEvent(e);
		if(e.isCanceled())
			return;
		ve.setAngle((ve.getAngleRotation() + ve.getType().getRotationDelta()) * this.getRotationMultiplier());
		HeadPoseUtil.setHeadPoseUsingReflection(ve);
	}

	@Override
	public void handleTurnRight(VehicleEntity ve, PacketEvent event) {
		VehicleTurnEvent e = new VehicleTurnEvent(ve,ve.getAngleRotation(), ve.getAngleRotation() - ve.getType().getRotationDelta());
		Bukkit.getPluginManager().callEvent(e);
		if(e.isCanceled())
			return;
		ve.setAngle((ve.getAngleRotation() - ve.getType().getRotationDelta()) * this.getRotationMultiplier());
		HeadPoseUtil.setHeadPoseUsingReflection(ve);
	}

	@Override
	public void handleSpeedIncrease(VehicleEntity ve, PacketEvent event) {
		VehicleChangeSpeedEvent e = new VehicleChangeSpeedEvent(ve,ve.getSpeed(), Math.min(ve.getSpeed() + 0.1, ve.getType().getMaxSpeed()));
		Bukkit.getPluginManager().callEvent(e);
		if(e.isCanceled())
			return;
		if (!this.handleFuel(ve,event)) {
			return;
		}
		ve.setSpeed(Math.min(ve.getSpeed() + 0.1, ve.getType().getMaxSpeed()));
			if (!lastSoundDrive.containsKey(event.getPlayer().getUniqueId())
					|| System.currentTimeMillis() - lastSoundDrive.get(event.getPlayer().getUniqueId()) > 900) {
				lastSoundDrive.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
				ve.getDriverSeat().getLocation().getWorld().playSound(ve.getDriverSeat().getLocation(), getSound(), (float) getSoundVolume(), 1);
			}
	}

	@Override
	public void handleSpeedDecrease(VehicleEntity ve, PacketEvent event) {
		VehicleChangeSpeedEvent e = new VehicleChangeSpeedEvent(ve,ve.getSpeed(), Math.max(ve.getSpeed() - 0.1, -ve.getType().getMaxSpeed()));
		Bukkit.getPluginManager().callEvent(e);
		if(e.isCanceled())
			return;
		if (!this.handleFuel(ve,event)) {
			return;
		}
		ve.setSpeed(Math.max(ve.getSpeed() - 0.1, -ve.getType().getMaxBackupSpeed()));

	}

	@Override
	public void handleSpace(VehicleEntity ve, PacketEvent event) {
		if(ve.getSpeed()>0) {
			ve.setSpeed(Math.max(ve.getSpeed() - 0.1, -ve.getType().getMaxSpeed()));
		}else{
			ve.setSpeed(Math.max(ve.getSpeed() + 0.1, -ve.getType().getMaxSpeed()));
		}

		if (canPlaySkidSounds()) {
			if (ve.getSpeed() > 0.2 || ve.getSpeed() <-0.2) {
				if (!lastSoundBreak.containsKey(event.getPlayer().getUniqueId())
						|| System.currentTimeMillis() - lastSoundBreak.get(event.getPlayer().getUniqueId()) > 2000) {
					lastSoundBreak.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
					try {
						event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(),
								me.zombie_striker.qg.guns.utils.WeaponSounds.CARSKID.getSoundName(), (float) ve.getType().getSoundVolume(), (float) 1.3);
					} catch (Error | Exception e4) {
						event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), "carskid", 0.7f,
								(float) (2.3));
					}
				}
			}
		}
	}

	@Override
	public void tick(VehicleEntity vehicleEntity) {
		basicDirections(vehicleEntity,canJump(),false);
		if(vehicleEntity.isSubmerged()){
			vehicleEntity.deconstruct(null,"UnderWater");
		}
	}

}
