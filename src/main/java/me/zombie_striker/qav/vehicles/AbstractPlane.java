package me.zombie_striker.qav.vehicles;

import com.comphenix.protocol.events.PacketEvent;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.events.VehicleTurnEvent;
import me.zombie_striker.qav.util.HeadPoseUtil;
import org.bukkit.Bukkit;
import org.bukkit.util.EulerAngle;

public class AbstractPlane extends AbstractVehicle {
	public AbstractPlane(String name, int id) {
		super(name, id);
	}

	@Override
	public void handleTurnLeft(VehicleEntity ve, PacketEvent event) {
		VehicleTurnEvent e = new VehicleTurnEvent(ve, ve.getAngleRotation(), ve.getAngleRotation() + ve.getType().getRotationDelta());
		Bukkit.getPluginManager().callEvent(e);
		if (e.isCanceled())
			return;
		ve.setAngle((ve.getAngleRotation() + ve.getType().getRotationDelta()) * this.getRotationMultiplier());
		HeadPoseUtil.setHeadPoseUsingReflection(ve);
	}

	@Override
	public void handleTurnRight(VehicleEntity ve, PacketEvent event) {
		VehicleTurnEvent e = new VehicleTurnEvent(ve, ve.getAngleRotation(), ve.getAngleRotation() - ve.getType().getRotationDelta());
		Bukkit.getPluginManager().callEvent(e);
		if (e.isCanceled())
			return;
		ve.setAngle((ve.getAngleRotation() - ve.getType().getRotationDelta()) * this.getRotationMultiplier());
		HeadPoseUtil.setHeadPoseUsingReflection(ve);
	}

	@Override
	public void handleSpeedIncrease(VehicleEntity ve, PacketEvent event) {
		ve.setDirectionYHeight(ve.getDirectionYheight() + 0.1);
		if (!this.handleFuel(ve,event)) {
			return;
		}

		double pitch = ve.getModelEntity().getHeadPose().getX();
		pitch -= AbstractVehicle.pitchIncrement;
		if (pitch > AbstractVehicle.maxAngle) {
			pitch = AbstractVehicle.maxAngle;
		} else if (pitch < -AbstractVehicle.maxAngle) {
			pitch = -AbstractVehicle.maxAngle;
		} else {
			ve.setDirectionYHeight(ve.getDirectionYheight() - 0.1);
		}
		ve.getModelEntity()
				.setHeadPose(new EulerAngle(pitch, ve.getModelEntity().getHeadPose().getY(), 0));
	}

	@Override
	public void handleSpeedDecrease(VehicleEntity ve, PacketEvent event) {
		if (!this.handleFuel(ve,event)) {
			return;
		}
		double pitch = ve.getModelEntity().getHeadPose().getX();
		pitch += AbstractVehicle.pitchIncrement;
		if (pitch > AbstractVehicle.maxAngle) {
			pitch = AbstractVehicle.maxAngle;
		} else if (pitch < -AbstractVehicle.maxAngle) {
			pitch = -AbstractVehicle.maxAngle;
		} else {
			ve.setDirectionYHeight(ve.getDirectionYheight() - 0.1);
		}
		ve.getModelEntity()
				.setHeadPose(new EulerAngle(pitch, ve.getModelEntity().getHeadPose().getY(), 0));
	}

	@Override
	public void handleSpace(VehicleEntity ve, PacketEvent event) {
		ve.setSpeed(Math.min(ve.getSpeed() + 0.1, ve.getType().getMaxSpeed()));

	}

	@Override
	public void tick(VehicleEntity vehicleEntity) {
		basicDirections(vehicleEntity, canJump(), false, true, true);
	}
}
