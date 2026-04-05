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
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Rail.Shape;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PoweredRail;
import org.bukkit.material.Rails;
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
		if (ve.isDisplayOnly()) return;

		Location loc = ve.getDriverSeat().getLocation();
		double priorVy = ve.getDriverSeat().getVelocity().getY();
		Block railBlock = findRailBlock(loc);
		Block block = railBlock != null ? railBlock : loc.getBlock();

		Vector travelMotion = horizontalTravelMotion(ve);
		Vector railMotion = travelMotion.clone();
		if (ve.getSpeed() < 0)
			railMotion.multiply(-1);

		int direction = getDirectionFromRail(ve, block, railMotion);
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

		if (isUnpoweredPoweredRail(railBlock)) {
			velocity = new Vector(0, 0, 0);
		}

		if (priorVy > 0.1) {
			double jumpCarry = Math.min(priorVy * 0.38, 0.14);
			velocity.setY(velocity.getY() + jumpCarry);
		}

		ve.getDriverSeat().setVelocity(velocity);
		handleOtherStands(ve, velocity);
	}

	@SuppressWarnings("deprecation")
	private static boolean isUnpoweredPoweredRail(@NotNull Block railBlock) {
		if (!BlockCollisionUtil.getMaterial(railBlock.getLocation()).equals(XMaterial.POWERED_RAIL.parseMaterial())) {
			return false;
		}
		if (XReflection.supports(13)) {
			return !XBlock.isPowered(railBlock);
		}
		PoweredRail rail = new PoweredRail(BlockCollisionUtil.getMaterial(railBlock.getLocation()), railBlock.getData());
		return !rail.isPowered();
	}

	@SuppressWarnings("deprecation")
	private static boolean isRailBlock(@NotNull Block railBlock) {
		if (XReflection.supports(13)) {
			return railBlock.getBlockData() instanceof Rail;
		}
		return railBlock.getState().getData() instanceof Rails;
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
		if (!isRailBlock(railBlock)) return;

		if (priorVy > 0.1) return;

		Vector seat = ve.getType().getDriverSeat();
		double seatY = seat != null ? seat.getY() : 1.0;
		double targetY = railBlock.getLocation().getY() + seatY - 1.0;
		double currentY = ve.getDriverSeat().getLocation().getY();

		if (currentY - targetY > 2.35) return;
		if (currentY <= targetY + 0.06) return;


		double dy = targetY - currentY;
		double correction = Math.max(-0.55, dy * 0.45);
		if (correction >= -0.001) return;

		velocity.setY(velocity.getY() + correction);
	}

	@SuppressWarnings("deprecation")
	private void applyAscendingRailLift(@NotNull VehicleEntity ve, @NotNull Block railBlock, @NotNull Vector velocity) {
		if (!isRailBlock(railBlock)) return;

		int direction = getDirectionInternalID(ve);
		double slopeY;
		if (XReflection.supports(13)) {
			Shape shape = ((Rail) railBlock.getBlockData()).getShape();
			slopeY = slopeYForRailShape(shape, direction);
		} else {
			MaterialData md = railBlock.getState().getData();
			slopeY = slopeYForLegacyRails((Rails) md, direction);
		}
		if (slopeY == 0.0) return;

		double slopeAmount = Math.max(0.14, Math.abs(ve.getSpeed()) * 0.6);

		double travelSign = Math.signum(ve.getSpeed());
		if (travelSign == 0.0) return;

		velocity.setY(slopeY * slopeAmount * travelSign);
	}

	private static double slopeYForRailShape(@NotNull Shape shape, int direction) {
		if (shape == Shape.ASCENDING_EAST) return slopeSignForAscending(direction, 1, 3);
		if (shape == Shape.ASCENDING_WEST) return slopeSignForAscending(direction, 3, 1);
		if (shape == Shape.ASCENDING_NORTH) return slopeSignForAscending(direction, 2, 0);
		if (shape == Shape.ASCENDING_SOUTH) return slopeSignForAscending(direction, 0, 2);
		return 0.0;
	}

	private static double slopeYForLegacyRails(@NotNull Rails rails, int direction) {
		if (!rails.isOnSlope()) return 0.0;
		BlockFace face = rails.getDirection();
		switch (face) {
			case EAST:
				return slopeSignForAscending(direction, 1, 3);
			case WEST:
				return slopeSignForAscending(direction, 3, 1);
			case NORTH:
				return slopeSignForAscending(direction, 2, 0);
			case SOUTH:
				return slopeSignForAscending(direction, 0, 2);
			default:
				return 0.0;
		}
	}

	private static double slopeSignForAscending(int direction, int uphillDir, int downhillDir) {
		if (direction == uphillDir) return 1.0;
		if (direction == downhillDir) return -1.0;
		return 0.0;
	}

	private static @NotNull Vector horizontalTravelMotion(@NotNull VehicleEntity ve) {
		Vector facing = ve.getDirection().clone();
		facing.setY(0);

		if (facing.lengthSquared() < 1e-8) facing = new Vector(0, 0, 1);
		else facing.normalize();

		double sp = ve.getSpeed();
		double sig = sp > 0 ? 1.0 : sp < 0 ? -1.0 : 1.0;
		Vector intended = facing.clone().multiply(sig);

		Vector vel = ve.getDriverSeat().getVelocity().clone();
		vel.setY(0);

		if (vel.lengthSquared() > 0.0025) {
			Vector vn = vel.clone().normalize();
			if (vn.dot(intended) >= -0.25) return vn;
		}

		return intended;
	}

	private static double horizontalMotionLengthSq(@NotNull Vector motion) {
		return motion.getX() * motion.getX() + motion.getZ() * motion.getZ();
	}

	private static int directionFromMotionComparison(@NotNull Vector motion, @NotNull Vector v1, @NotNull Vector v2,
	                                                 int ifFirstGreaterOrEqual, int ifSecondGreater) {
		return motion.dot(v1) >= motion.dot(v2) ? ifFirstGreaterOrEqual : ifSecondGreater;
	}

	@SuppressWarnings("deprecation")
	private int getDirectionFromRail(@NotNull VehicleEntity ve, @NotNull Block b, @NotNull Vector motion) {
		int shape = -1;
		int direction = getDirectionInternalID(ve);

		if (XReflection.supports(13)) {
			if (b.getBlockData() instanceof org.bukkit.block.data.Rail) {
				Rail rail = (Rail) b.getBlockData();
				shape = getDirectionID(rail.getShape().name());
			}
		} else {
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
				if (horizontalMotionLengthSq(motion) < 1e-8) {
					if (ve.getAngleRotation() >= Math.PI / 2 && ve.getAngleRotation() < Math.PI * 3 / 2)
						return 1;

					return 3;
				}
				return motion.getX() <= 0 ? 1 : 3;
			case 0:
				if (horizontalMotionLengthSq(motion) < 1e-8) {
					if (ve.getAngleRotation() < Math.PI / 2 || ve.getAngleRotation() > Math.PI * 3 / 2)
						return 0;

					return 2;
				}
				return motion.getZ() >= 0 ? 0 : 2;
			case 9:
				return directionFromMotionComparison(motion, new Vector(0, 0, 1), new Vector(-1, 0, 0), 3, 2);
			case 7:
				return directionFromMotionComparison(motion, new Vector(0, 0, -1), new Vector(1, 0, 0), 1, 0);
			case 8:
				return directionFromMotionComparison(motion, new Vector(0, 0, 1), new Vector(1, 0, 0), 1, 2);
			case 6:
				return directionFromMotionComparison(motion, new Vector(0, 0, -1), new Vector(-1, 0, 0), 3, 0);
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
			if (QualityArmoryVehicles.isRailMaterial(b.getLocation()))
				return b;
		}

		Block above = world.getBlockAt(x, y + 1, z);
		if (QualityArmoryVehicles.isRailMaterial(above.getLocation())) return above;
		return null;
	}
}
