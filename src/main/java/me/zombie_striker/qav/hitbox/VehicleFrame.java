package me.zombie_striker.qav.hitbox;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.ModelSize;
import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Snapshot of a vehicle's position and orientation, used to convert between world space and the vehicle's local space.
 * <p>
 * Local to world: pitch around the side axis (Z) at the model head, then yaw, then translation to the model location.
 * The yaw matches {@link me.zombie_striker.qav.api.QualityArmoryVehicles#rotateRelToCar}.
 */
public final class VehicleFrame {

    private final World world;
    private final double ox, oy, oz;
    private final double cos, sin;
    private final double pitchCos, pitchSin;
    private final double pivotY;

    private VehicleFrame(World world, double ox, double oy, double oz, double yaw, double pitch, double pivotY) {
        this.world = world;
        this.ox = ox;
        this.oy = oy;
        this.oz = oz;
        this.cos = Math.cos(yaw);
        this.sin = Math.sin(yaw);
        this.pitchCos = Math.cos(pitch);
        this.pitchSin = Math.sin(pitch);
        this.pivotY = pivotY;
    }

    /**
     * @return the current frame of the vehicle, or null if its entities are missing
     */
    public static @Nullable VehicleFrame of(@NotNull VehicleEntity ve) {
        Entity origin = ve.getModelEntities().isEmpty() ? ve.getDriverSeat() : ve.getModelEntity();
        if (origin == null || ve.getType() == null) return null;

        Location loc = origin.getLocation();
        if (loc.getWorld() == null) return null;

        double pitch = 0;
        if (origin instanceof ArmorStand) {
            pitch = ((ArmorStand) origin).getHeadPose().getX();
        }
        if (Double.isNaN(pitch) || Math.abs(pitch) < 1.0E-4) pitch = 0;

        return new VehicleFrame(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(),
                ve.getAngleRotation() - Main.YOFFSET, pitch, pivotHeight(ve.getType().getModelType()));
    }

    /**
     * @return the height of the armor stand head, where the model rotates when it pitches
     */
    private static double pivotHeight(ModelSize size) {
        if (size == null) return 0;
        switch (size) {
            case ADULT_ARMORSTAND_HEAD:
                return 1.44;
            case ADULT_ARMORSTAND_HAND:
            case BABY_ARMORSTAND_HEAD:
                return 0.72;
            default:
                return 0;
        }
    }

    public World getWorld() {
        return world;
    }

    public Vector getOrigin() {
        return new Vector(ox, oy, oz);
    }

    public boolean hasPitch() {
        return pitchSin != 0;
    }

    public double getPitchSin() {
        return pitchSin;
    }

    public double getPitchCos() {
        return pitchCos;
    }

    /**
     * Converts a world position to local space.
     */
    public Vector toLocal(double x, double y, double z) {
        Vector v = toLocalDirection(x - ox, y - oy, z - oz, false);
        if (hasPitch()) {
            double ly = v.getY() - pivotY;
            double lx = v.getX();
            v.setX(lx * pitchCos - ly * pitchSin);
            v.setY(pivotY + ly * pitchCos + lx * pitchSin);
        }
        return v;
    }

    public Vector toLocal(@NotNull Vector world) {
        return toLocal(world.getX(), world.getY(), world.getZ());
    }

    /**
     * Converts a world direction to local space.
     */
    public Vector toLocalDirection(@NotNull Vector direction) {
        return toLocalDirection(direction.getX(), direction.getY(), direction.getZ(), true);
    }

    private Vector toLocalDirection(double x, double y, double z, boolean applyPitch) {
        // Inverse yaw
        double lx = x * cos + z * sin;
        double lz = z * cos - x * sin;
        Vector v = new Vector(lx, y, lz);
        if (applyPitch && hasPitch()) {
            double py = v.getY();
            double px = v.getX();
            v.setX(px * pitchCos - py * pitchSin);
            v.setY(py * pitchCos + px * pitchSin);
        }
        return v;
    }

    /**
     * Converts a local position to world space.
     */
    public Vector toWorld(@NotNull Vector local) {
        double x = local.getX();
        double y = local.getY();
        double z = local.getZ();

        if (hasPitch()) {
            double ly = y - pivotY;
            double px = x * pitchCos + ly * pitchSin;
            y = pivotY + ly * pitchCos - x * pitchSin;
            x = px;
        }

        double wx = x * cos - z * sin;
        double wz = z * cos + x * sin;
        return new Vector(ox + wx, oy + y, oz + wz);
    }

    public Location toWorldLocation(@NotNull Vector local) {
        return toWorld(local).toLocation(world);
    }
}
