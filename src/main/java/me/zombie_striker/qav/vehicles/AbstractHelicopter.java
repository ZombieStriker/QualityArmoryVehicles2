package me.zombie_striker.qav.vehicles;

import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.events.VehicleTurnEvent;
import me.zombie_striker.qav.util.HeadPoseUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AbstractHelicopter extends AbstractVehicle {
	private double descentSpeed = -0.1;

	public AbstractHelicopter(String name, int id) {
		super(name,id);
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
		if (!this.handleFuel(ve,player)) {
			return;
		}
		ve.setSpeed(Math.min(ve.getSpeed() + 0.1, ve.getType().getMaxSpeed()));
	}

	@Override
	public void handleSpeedDecrease(VehicleEntity ve, Player player) {
		if (!this.handleFuel(ve,player)) {
			return;
		}
		ve.setSpeed(Math.max(ve.getSpeed() - 0.1, -ve.getType().getMaxBackupSpeed()));

	}

	@Override
	public void handleSpace(VehicleEntity ve, Player player) {
		if(player.getLocation().getPitch() > 0) {
			ve.setDirectionYHeight(-1);
		}else{
			ve.setDirectionYHeight(1);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void tick(VehicleEntity vehicleEntity) {
		if (vehicleEntity.getDriverSeat().getPassenger() == null || !hasFuel(vehicleEntity)) {
			vehicleEntity.setDirectionYHeight(-1);
		}

		basicDirections(vehicleEntity,false,false,false,false);
	}

	@Override
	public void applyModifiers(VehicleEntity entity, Vector vector) {
		super.applyModifiers(entity,vector);

		// TODO: descentSpeed
	}

	public double getDescentSpeed() {
		return descentSpeed;
	}

	public void setDescentSpeed(double descentSpeed) {
		this.descentSpeed = descentSpeed;
	}
}
