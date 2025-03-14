package me.zombie_striker.qav.vehicles;

import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.events.VehicleTurnEvent;
import me.zombie_striker.qav.util.HeadPoseUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

public class AbstractPlane extends AbstractVehicle {
	public AbstractPlane(String name, int id) {
		super(name, id);
	}

	@Override
	public void handleTurnLeft(VehicleEntity ve, Player player) {
		VehicleTurnEvent e = new VehicleTurnEvent(ve, ve.getAngleRotation(), ve.getAngleRotation() + ve.getType().getRotationDelta());
		Bukkit.getPluginManager().callEvent(e);
		if (e.isCanceled())
			return;
		ve.setAngle((ve.getAngleRotation() + ve.getType().getRotationDelta()) * this.getRotationMultiplier());
		HeadPoseUtil.setHeadPoseUsingReflection(ve);
	}

	@Override
	public void handleTurnRight(VehicleEntity ve, Player player) {
		VehicleTurnEvent e = new VehicleTurnEvent(ve, ve.getAngleRotation(), ve.getAngleRotation() - ve.getType().getRotationDelta());
		Bukkit.getPluginManager().callEvent(e);
		if (e.isCanceled())
			return;
		ve.setAngle((ve.getAngleRotation() - ve.getType().getRotationDelta()) * this.getRotationMultiplier());
		HeadPoseUtil.setHeadPoseUsingReflection(ve);
	}

	@Override
	public void handleSpeedIncrease(VehicleEntity ve, Player player) {
		if (!this.handleFuel(ve,player)) {
			return;
		}
		
		// W should ONLY increase speed, no change in pitch
		ve.setSpeed(Math.min(ve.getSpeed() + 0.1, ve.getType().getMaxSpeed()));
	}

	@Override
	public void handleSpeedDecrease(VehicleEntity ve, Player player) {
		if (!this.handleFuel(ve,player)) {
			return;
		}
		
		// S should ONLY decrease speed, no change in pitch
		ve.setSpeed(Math.max(0, ve.getSpeed() - 0.05));
	}

	@Override
	public void handleSpace(VehicleEntity ve, Player player) {
		// Space bar should do nothing for planes
		return;
	}

	@Override
	public void tick(VehicleEntity vehicleEntity) {
		basicDirections(vehicleEntity, canJump(), false, true, true);
	}
}
