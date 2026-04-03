package me.zombie_striker.qav.vehicles;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.reflection.XReflection;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.util.BlockCollisionUtil;
import me.zombie_striker.qav.util.HeadPoseUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Rail.Shape;
import org.bukkit.entity.Player;
import org.bukkit.material.PoweredRail;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class AbstractTrain extends AbstractVehicle {
	public AbstractTrain(String name, int id) {
		super(name,id);
	}

	@Override
	public void handleTurnLeft(VehicleEntity ve, Player player) {

	}

	@Override
	public void handleTurnRight(VehicleEntity ve, Player player) {

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
		double priorVy = ve.getDriverSeat().getVelocity().getY();
		Block railBlock = findRailBlock(loc);
		Block block = railBlock != null ? railBlock : loc.getBlock();

		int direction = getDirectionFromRail(ve,block);
		int directionVeh = getDirectionInternalID(ve);

		if (direction != directionVeh) {
			float newAngle = (float) getAngleFromDirection(direction);
			ve.setAngle(newAngle);
			HeadPoseUtil.setHeadPoseUsingReflection(ve);
		}

		Vector velocity = ve.getDirection().clone();
		velocity.normalize().multiply(ve.getSpeed());

		if (railBlock == null) {
			applyOffRailGravity(ve);
			return;
		}
		applyAscendingRailLift(ve, railBlock, velocity);
		applyRailVerticalSnap(ve, railBlock, velocity, priorVy);

		if (BlockCollisionUtil.getMaterial(railBlock.getLocation()).equals(XMaterial.POWERED_RAIL.parseMaterial())) {
			if (XReflection.supports(13)) {
				if (!XBlock.isPowered(railBlock)) velocity = new Vector(0, 0, 0);
			} else {
				PoweredRail rail = new PoweredRail(BlockCollisionUtil.getMaterial(railBlock.getLocation()), railBlock.getData());
				if (!rail.isPowered()) velocity = new Vector(0, 0, 0);
			}
		}

		if (priorVy > 0.1) {
			double jumpCarry = Math.min(priorVy * 0.38, 0.14);
			velocity.setY(velocity.getY() + jumpCarry);
		}

		ve.getDriverSeat().setVelocity(velocity);
		handleOtherStands(ve, velocity);
	}

	private void applyOffRailGravity(@NotNull VehicleEntity ve) {
		Vector velocity = ve.getDriverSeat().getVelocity().clone();
		velocity.setX(velocity.getX() * 0.98);
		velocity.setZ(velocity.getZ() * 0.98);
		velocity.setY(Math.max(-1, velocity.getY() - 0.05));
		ve.getDriverSeat().setVelocity(velocity);
		handleOtherStands(ve, velocity);
	}

	private void applyRailVerticalSnap(@NotNull VehicleEntity ve, @NotNull Block railBlock, @NotNull Vector velocity,
	                                   double priorVy) {
		if (!(railBlock.getBlockData() instanceof Rail)) return;

		if (priorVy > 0.1) {
			return;
		}

		Vector seat = ve.getType().getDriverSeat();
		double seatY = seat != null ? seat.getY() : 1.0;
		double targetY = railBlock.getLocation().getY() + seatY - 1.0;
		double currentY = ve.getDriverSeat().getLocation().getY();

		if (currentY - targetY > 2.35) return;
		if (currentY <= targetY + 0.06) return;


		double dy = targetY - currentY;
		double correction = Math.max(-0.55, dy * 0.45);
		if (correction >= -0.001) {
			return;
		}
		velocity.setY(velocity.getY() + correction);
	}

	private void applyAscendingRailLift(@NotNull VehicleEntity ve, @NotNull Block railBlock, @NotNull Vector velocity) {
		if (!(railBlock.getBlockData() instanceof Rail)) return;

		Shape shape = ((Rail) railBlock.getBlockData()).getShape();
		int direction = getDirectionInternalID(ve);
		double slopeY = 0.0;
		if (shape == Shape.ASCENDING_EAST) {
			if (direction == 1) slopeY = 1.0;
			else if (direction == 3) slopeY = -1.0;
		} else if (shape == Shape.ASCENDING_WEST) {
			if (direction == 3) slopeY = 1.0;
			else if (direction == 1) slopeY = -1.0;
		} else if (shape == Shape.ASCENDING_NORTH) {
			if (direction == 2) slopeY = 1.0;
			else if (direction == 0) slopeY = -1.0;
		} else if (shape == Shape.ASCENDING_SOUTH) {
			if (direction == 0) slopeY = 1.0;
			else if (direction == 2) slopeY = -1.0;
		}
		if (slopeY == 0.0) return;

		double slopeAmount = Math.max(0.14, Math.abs(ve.getSpeed()) * 0.6);

		double travelSign = Math.signum(ve.getSpeed());
		if (travelSign == 0.0) return;

		velocity.setY(slopeY * slopeAmount * travelSign);
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
		return findRailBlock(entity.getDriverSeat().getLocation()) != null;
	}

	public boolean isRail(@NotNull Location location) {
		return findRailBlock(location) != null;
	}

	private Block findRailBlock(@NotNull Location location) {
		World world = location.getWorld();
		if (world == null) return null;

		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();

		for (int dy = 0; dy <= 6; dy++) {
			Block b = world.getBlockAt(x, y - dy, z);
			if (QualityArmoryVehicles.isRailMaterial(b.getLocation())) {
				return b;
			}
		}

		Block above = world.getBlockAt(x, y + 1, z);
		if (QualityArmoryVehicles.isRailMaterial(above.getLocation())) return above;
		return null;
	}
}
