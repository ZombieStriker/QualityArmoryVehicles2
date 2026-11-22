package me.zombie_striker.qav.hitbox;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A box in the vehicle's local space.
 * <p>
 * Local axes: +X points to the front of the vehicle, +Y up and +Z to the side.
 * The origin is the location of the vehicle model entity.
 */
public final class HitboxBox {

    private final double minX, minY, minZ;
    private final double maxX, maxY, maxZ;

    public HitboxBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    /**
     * @param length size along the front axis (X)
     * @param width  size along the side axis (Z)
     * @param height size along the vertical axis (Y)
     * @param x      X of the center of the box
     * @param y      Y of the bottom of the box
     * @param z      Z of the center of the box
     */
    public static HitboxBox of(double length, double width, double height, double x, double y, double z) {
        return new HitboxBox(x - length / 2, y, z - width / 2, x + length / 2, y + height, z + width / 2);
    }

    /**
     * Reads a box from a config section or map with the keys length, width, height and the optional x, y, z.
     *
     * @return the box, or null if the size is missing or invalid
     */
    public static @Nullable HitboxBox fromConfig(@Nullable Object raw) {
        double length, width, height, x, y, z;
        if (raw instanceof ConfigurationSection) {
            ConfigurationSection section = (ConfigurationSection) raw;
            length = section.getDouble("length", -1);
            width = section.getDouble("width", -1);
            height = section.getDouble("height", -1);
            x = section.getDouble("x", 0);
            y = section.getDouble("y", 0);
            z = section.getDouble("z", 0);
        } else if (raw instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) raw;
            length = number(map.get("length"), -1);
            width = number(map.get("width"), -1);
            height = number(map.get("height"), -1);
            x = number(map.get("x"), 0);
            y = number(map.get("y"), 0);
            z = number(map.get("z"), 0);
        } else {
            return null;
        }

        if (length <= 0 || width <= 0 || height <= 0) return null;
        return of(length, width, height, x, y, z);
    }

    private static double number(Object o, double def) {
        if (o instanceof Number) return ((Number) o).doubleValue();
        if (o instanceof String) {
            try {
                return Double.parseDouble((String) o);
            } catch (NumberFormatException ignored) {
            }
        }
        return def;
    }

    public boolean contains(@NotNull Vector local) {
        return local.getX() >= minX && local.getX() <= maxX
                && local.getY() >= minY && local.getY() <= maxY
                && local.getZ() >= minZ && local.getZ() <= maxZ;
    }

    /**
     * Slab intersection test.
     *
     * @return the distance along the ray where it enters the box (0 if the origin is inside), or -1 if it misses
     */
    public double intersect(@NotNull Vector origin, @NotNull Vector direction, double maxDistance) {
        double tMin = 0;
        double tMax = maxDistance;

        double[] o = {origin.getX(), origin.getY(), origin.getZ()};
        double[] d = {direction.getX(), direction.getY(), direction.getZ()};
        double[] min = {minX, minY, minZ};
        double[] max = {maxX, maxY, maxZ};

        for (int i = 0; i < 3; i++) {
            if (Math.abs(d[i]) < 1.0E-9) {
                if (o[i] < min[i] || o[i] > max[i]) return -1;
                continue;
            }

            double t1 = (min[i] - o[i]) / d[i];
            double t2 = (max[i] - o[i]) / d[i];
            if (t1 > t2) {
                double tmp = t1;
                t1 = t2;
                t2 = tmp;
            }

            if (t1 > tMin) tMin = t1;
            if (t2 < tMax) tMax = t2;
            if (tMin > tMax) return -1;
        }

        return tMin;
    }

    public HitboxBox union(@NotNull HitboxBox other) {
        return new HitboxBox(Math.min(minX, other.minX), Math.min(minY, other.minY), Math.min(minZ, other.minZ),
                Math.max(maxX, other.maxX), Math.max(maxY, other.maxY), Math.max(maxZ, other.maxZ));
    }

    /**
     * @return the 8 corners of the box
     */
    public Vector[] corners() {
        return new Vector[]{
                new Vector(minX, minY, minZ), new Vector(maxX, minY, minZ),
                new Vector(maxX, minY, maxZ), new Vector(minX, minY, maxZ),
                new Vector(minX, maxY, minZ), new Vector(maxX, maxY, minZ),
                new Vector(maxX, maxY, maxZ), new Vector(minX, maxY, maxZ)
        };
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public double getLength() {
        return maxX - minX;
    }

    public double getWidth() {
        return maxZ - minZ;
    }

    public double getHeight() {
        return maxY - minY;
    }

    @Override
    public String toString() {
        return String.format("{length=%.2f, width=%.2f, height=%.2f, x=%.2f, y=%.2f, z=%.2f}",
                getLength(), getWidth(), getHeight(), (minX + maxX) / 2, minY, (minZ + maxZ) / 2);
    }
}
