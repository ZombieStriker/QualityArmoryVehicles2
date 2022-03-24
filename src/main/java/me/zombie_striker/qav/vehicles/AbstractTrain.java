package me.zombie_striker.qav.vehicles;

import com.comphenix.protocol.events.PacketEvent;
import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.util.BlockCollisionUtil;
import me.zombie_striker.qav.util.HeadPoseUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rail;
import org.bukkit.material.PoweredRail;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class AbstractTrain extends AbstractVehicle {
	public AbstractTrain(String name, int id) {
		super(name,id);
	}

	@Override
	public void handleTurnLeft(VehicleEntity ve, PacketEvent event) {

	}

	@Override
	public void handleTurnRight(VehicleEntity ve, PacketEvent event) {

	}

	@Override
	public void handleSpeedIncrease(VehicleEntity ve, PacketEvent event) {
		if (!this.handleFuel(ve,event)) {
			return;
		}
		ve.setSpeed(Math.min(ve.getSpeed() + 0.1, ve.getType().getMaxSpeed()));
	}

	@Override
	public void handleSpeedDecrease(VehicleEntity ve, PacketEvent event) {
		if (!this.handleFuel(ve,event)) {
			return;
		}
		ve.setSpeed(Math.max(ve.getSpeed() - 0.1, -ve.getType().getMaxBackupSpeed()));
	}

	@Override
	public void handleSpace(VehicleEntity ve, PacketEvent event) {
		if(ve.getSpeed()>0) {
			ve.setSpeed(Math.max(ve.getSpeed() - 0.1, -ve.getType().getMaxBackupSpeed()));
		}else{
			ve.setSpeed(Math.max(ve.getSpeed() + 0.1, -ve.getType().getMaxSpeed()));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void tick(VehicleEntity ve) {
		Location loc = ve.getDriverSeat().getLocation();
		Block block = loc.getBlock();

		int direction = getDirectionFromRail(ve,block);
		int directionVeh = getDirectionInternalID(ve);

		if (direction != directionVeh) {
			float newAngle = (float) getAngleFromDirection(direction);
			ve.setAngle(newAngle);
			HeadPoseUtil.setHeadPoseUsingReflection(ve);
		}

		Vector velocity = ve.getDirection().clone();
		velocity.normalize().multiply(ve.getSpeed());

		if (!BlockCollisionUtil.isSolid(loc)) {
			velocity.setY(Math.max(-1,ve.getDriverSeat().getVelocity().getY() - 0.05));
		}

		if (!isOnRail(ve)) return;

		if (BlockCollisionUtil.getMaterial(loc).equals(XMaterial.POWERED_RAIL.parseMaterial())) {
			if (XMaterial.supports(13)) {
				if (!XBlock.isPowered(block)) {
					velocity = new Vector(0, 0, 0);
				}
			} else {
				PoweredRail rail = new PoweredRail(BlockCollisionUtil.getMaterial(loc), block.getData());
				if (!rail.isPowered()) {
					velocity = new Vector(0, 0, 0);
				}

			}
		}


		ve.getDriverSeat().setVelocity(velocity);
		handleOtherStands(ve,velocity);
	}

	@SuppressWarnings("deprecation")
	private int getDirectionFromRail(VehicleEntity ve, Block b) {
		int shape = -1;
		int direction = getDirectionInternalID(ve);

		try {
			if (b.getBlockData() instanceof org.bukkit.block.data.Rail) {
				Rail rail = (Rail) b.getBlockData();
				shape = getDirectionID(rail.getShape().name());
			}
		} catch (Throwable ignored) {
			shape = b.getData();
		}

		switch (shape) {
			case 2:
			case 3:
				if (direction == 1 || direction == 3)
					return 1;
				break;
			case 4:
			case 5:
				if (direction == 0 || direction == 2)
					return direction;
				break;
			case 1:
				if (ve.getAngleRotation() >= Math.PI / 2 && ve.getAngleRotation() < Math.PI * 3 / 2) {
					return 1;
				} else {
					return 3;
				}
			case 0:
				if (ve.getAngleRotation() < Math.PI / 2 || ve.getAngleRotation() > Math.PI * 3 / 2) {
					return 0;
				} else {
					return 2;
				}
			case 9:
				if (direction == 0) {
					return 3;
				}
				if (direction == 1) {
					return 2;
				}
				return direction;
			case 7:
				if (direction == 2) {
					return 1;
				}
				if (direction == 3) {
					return 0;
				}
				return direction;

			case 8:
				if (direction == 0) {
					return 1;
				}
				if (direction == 3) {
					return 2;
				}
				return direction;
			case 6:
				if (direction == 2) {
					return 3;
				}
				if (direction == 1) {
					return 2;
				}
				return direction;
			default:
				break;
		}

		return getDirectionInternalID(ve);
	}

	public static int getDirectionID(String shape) {
		switch (shape.toUpperCase()) {
			case "ASCENDING_WEST":
			case "ASCENDING_EAST":
				return 2;
			case "ASCENDING_NORTH":
				return 4;
			case "ASCENDING_SOUTH":
				return 5;
			case "EAST_WEST":
				return 1;
			case "NORTH_SOUTH":
				return 0;
			case "NORTH_EAST":
				return 9;
			case "SOUTH_WEST":
				return 7;
			case "NORTH_WEST":
				return 8;
			case "SOUTH_EAST":
				return 6;
			default:
				return -1;
		}
	}

	public static int getDirectionInternalID(VehicleEntity ve) {
		if (ve.getAngleRotation() <= Math.PI / 4)
			return 0;// South
		if (ve.getAngleRotation() <= Math.PI * 3 / 4)
			return 1; // East
		if (ve.getAngleRotation() <= Math.PI * 5 / 4)
			return 2; // North
		if (ve.getAngleRotation() <= Math.PI * 7 / 4)
			return 3; // West
		else
			return 0; // South
	}

	public static double getAngleFromDirection(int id) {
		if (id == 0)
			return 0;
		else if (id == 1)
			return Math.PI / 2;
		else if (id == 2)
			return Math.PI;
		else if (id == 3)
			return Math.PI * 3 / 2;
		return 0;
	}

	public boolean isOnRail(@NotNull VehicleEntity entity) {
		return isRail(entity.getDriverSeat().getLocation());
	}

	public boolean isRail(@NotNull Location location) {
		XMaterial material = XMaterial.matchXMaterial(BlockCollisionUtil.getMaterial(location));
		return material.equals(XMaterial.RAIL) || material.equals(XMaterial.ACTIVATOR_RAIL) || material.equals(XMaterial.POWERED_RAIL) || material.equals(XMaterial.DETECTOR_RAIL);
	}
}
