package me.zombie_striker.qav.hitbox;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.util.SchedulerUtil;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Precise ray tests against the rotated vehicle hitboxes. Works on every server version.
 */
public final class HitboxRaycast {

    private static final boolean MODERN_BLOCK_TRACE = detectModernBlockTrace();

    private HitboxRaycast() {
    }

    private static boolean detectModernBlockTrace() {
        try {
            Class<?> mode = Class.forName("org.bukkit.FluidCollisionMode");
            World.class.getMethod("rayTraceBlocks", Location.class, Vector.class, double.class, mode, boolean.class);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Finds the closest vehicle the player is looking at, ignoring vehicles behind blocks.
     *
     * @param player the player
     * @param reach  the max distance from the player's eyes
     * @return the hit, or null if the player is not looking at a vehicle
     */
    public static @Nullable HitResult rayTrace(@NotNull Player player, double reach) {
        Location eye = player.getEyeLocation();
        return rayTrace(eye, eye.getDirection(), reach, player.getVehicle());
    }

    /**
     * Finds the closest vehicle hit by a ray, ignoring vehicles behind blocks.
     *
     * @param ignoreRiding an entity to ignore the vehicle of (e.g. the vehicle the player is riding), nullable
     */
    public static @Nullable HitResult rayTrace(@NotNull Location start, @NotNull Vector direction, double reach, @Nullable Entity ignoreRiding) {
        if (start.getWorld() == null || direction.lengthSquared() == 0) return null;
        Vector dir = direction.clone().normalize();

        HitResult best = null;
        for (VehicleEntity ve : Main.vehicles) {
            if (ve == null || ve.getDriverSeat() == null || ve.getType() == null) continue;
            if (ignoreRiding != null && (ignoreRiding.equals(ve.getDriverSeat()) || ve.getPassagerSeats().contains(ignoreRiding)))
                continue;
            // Folia: only the vehicles of the current region can be accessed
            if (SchedulerUtil.isFolia() && !SchedulerUtil.isOwned(ve.getDriverSeat())) continue;

            HitResult hit = intersect(ve, start, dir, best == null ? reach : best.getDistance());
            if (hit != null && (best == null || hit.getDistance() < best.getDistance())) {
                best = hit;
            }
        }

        if (best != null && isBlocked(start, dir, best.getDistance())) {
            return null;
        }
        return best;
    }

    /**
     * Tests a ray against a single vehicle. Blocks are not checked.
     *
     * @return the hit, or null if the ray misses the vehicle
     */
    public static @Nullable HitResult intersect(@NotNull VehicleEntity ve, @NotNull Location start, @NotNull Vector direction, double maxDistance) {
        VehicleFrame frame = VehicleFrame.of(ve);
        if (frame == null || !frame.getWorld().equals(start.getWorld())) return null;

        VehicleHitbox hitbox = ve.getType().getHitbox();

        // Quick distance check before doing the full test
        double dx = start.getX() - frame.getOrigin().getX();
        double dy = start.getY() - frame.getOrigin().getY();
        double dz = start.getZ() - frame.getOrigin().getZ();
        double limit = maxDistance + hitbox.getRadius() + 2;
        if (dx * dx + dy * dy + dz * dz > limit * limit) return null;

        Vector dir = direction.clone().normalize();
        Vector localStart = frame.toLocal(start.getX(), start.getY(), start.getZ());
        Vector localDir = frame.toLocalDirection(dir);

        double t = hitbox.intersect(localStart, localDir, maxDistance);
        if (t < 0) return null;

        Vector localPoint = localStart.clone().add(localDir.clone().multiply(t));
        Location worldPoint = start.clone().add(dir.clone().multiply(t));
        return new HitResult(ve, t, localPoint, worldPoint);
    }

    /**
     * Tests the ray of a player that clicked one of the vehicle entities. Used to find where the vehicle was clicked.
     *
     * @return the hit, or null if the player's ray misses the hitbox
     */
    public static @Nullable HitResult intersect(@NotNull VehicleEntity ve, @NotNull Player player) {
        Location eye = player.getEyeLocation();
        return intersect(ve, eye, eye.getDirection(), Math.max(Main.hitboxReach, 6) + 2);
    }

    /**
     * @return true if the location is inside the vehicle hitbox
     */
    public static boolean contains(@NotNull VehicleEntity ve, @NotNull Location location) {
        if (ve.getDriverSeat() == null || ve.getType() == null) return false;
        VehicleFrame frame = VehicleFrame.of(ve);
        if (frame == null || !frame.getWorld().equals(location.getWorld())) return false;
        return ve.getType().getHitbox().contains(frame.toLocal(location.getX(), location.getY(), location.getZ()));
    }

    /**
     * @return true if a block stops the ray before the given distance
     */
    public static boolean isBlocked(@NotNull Location start, @NotNull Vector direction, double distance) {
        if (distance <= 0) return false;

        if (MODERN_BLOCK_TRACE) {
            try {
                return ModernBlockTrace.isBlocked(start, direction, distance);
            } catch (Throwable ignored) {
                // Fall back to the block iterator below
            }
        }

        return isBlockedLegacy(start, direction, distance);
    }

    /**
     * Pre 1.13.2: walk the blocks along the ray and test the solid ones as full cubes.
     */
    private static boolean isBlockedLegacy(Location start, Vector direction, double distance) {
        World world = start.getWorld();
        if (world == null) return false;

        Vector origin = start.toVector();
        Vector dir = direction.clone().normalize();
        try {
            BlockIterator iterator = new BlockIterator(world, origin, dir, 0, (int) Math.ceil(distance) + 1);
            while (iterator.hasNext()) {
                Block block = iterator.next();
                if (!block.getType().isSolid()) continue;

                HitboxBox cube = new HitboxBox(block.getX(), block.getY(), block.getZ(),
                        block.getX() + 1, block.getY() + 1, block.getZ() + 1);
                double t = cube.intersect(origin, dir, distance);
                if (t >= 0 && t < distance) return true;
            }
        } catch (IllegalStateException ignored) {
            // Thrown by the iterator in some edge cases (e.g. start outside the world height)
        }
        return false;
    }

    /**
     * 1.13.2+: uses the server ray trace, which knows the exact shape of every block.
     * In its own class so the newer API classes are never loaded on older servers.
     */
    private static final class ModernBlockTrace {
        static boolean isBlocked(Location start, Vector direction, double distance) {
            RayTraceResult result = start.getWorld().rayTraceBlocks(start, direction, distance, FluidCollisionMode.NEVER, true);
            if (result == null || result.getHitPosition() == null) return false;
            return result.getHitPosition().distance(start.toVector()) < distance;
        }
    }
}
