package me.zombie_striker.qav.hitbox;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The shape of a vehicle type: one or more boxes in the vehicle's local space (see {@link HitboxBox}).
 * <p>
 * Boxes come from the {@code hitbox} option of the vehicle file. When it is missing they are generated from
 * the seats, the vehicle length ({@code center}, {@code front_vector_offset}, {@code back_vector_offset})
 * and the legacy {@code widthOffset}/{@code heightOffset} options.
 */
public final class VehicleHitbox {

    /** Space added around the seats when the hitbox is generated */
    private static final double SEAT_PADDING = 0.7;
    /** Space above a seat for the rider when the hitbox is generated */
    private static final double RIDER_HEIGHT = 1.6;

    private final List<HitboxBox> boxes;
    private final boolean generated;
    private final double radius;

    public VehicleHitbox(@NotNull List<HitboxBox> boxes, boolean generated) {
        this.boxes = Collections.unmodifiableList(new ArrayList<>(boxes));
        this.generated = generated;

        double r = 0;
        for (HitboxBox box : boxes) {
            for (Vector corner : box.corners()) {
                r = Math.max(r, corner.length());
            }
        }
        this.radius = r;
    }

    /**
     * Generates the hitbox of a vehicle that does not configure one.
     */
    public static VehicleHitbox generate(@NotNull AbstractVehicle type) {
        List<Vector> seats = new ArrayList<>();
        seats.add(type.getDriverSeatLocal());
        seats.addAll(type.getPassagerSeatsLocal());

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;
        for (Vector seat : seats) {
            minX = Math.min(minX, seat.getX() - SEAT_PADDING);
            maxX = Math.max(maxX, seat.getX() + SEAT_PADDING);
            minZ = Math.min(minZ, seat.getZ() - SEAT_PADDING);
            maxZ = Math.max(maxZ, seat.getZ() + SEAT_PADDING);
            minY = Math.min(minY, seat.getY());
            maxY = Math.max(maxY, seat.getY() + RIDER_HEIGHT);
        }

        Vector center = type.getCenter() == null ? new Vector() : type.getCenter();
        double front = type.getFrontOffset();
        double back = type.getBackOffset();
        if (front > 0 || back > 0) {
            // The length of the vehicle is known: use it, and make sure the vehicle is at least ~2 blocks wide
            minX = Math.min(minX, center.getX() - back);
            maxX = Math.max(maxX, center.getX() + front);
            double halfWidth = Math.min(type.getWidthRadius(), 1.0);
            minZ = Math.min(minZ, center.getZ() - halfWidth);
            maxZ = Math.max(maxZ, center.getZ() + halfWidth);
        } else {
            // Never smaller than the old cylinder hitbox
            double r = type.getWidthRadius();
            minX = Math.min(minX, -r);
            maxX = Math.max(maxX, r);
            minZ = Math.min(minZ, -r);
            maxZ = Math.max(maxZ, r);
        }

        minY = Math.min(minY, 0);
        maxY = Math.max(maxY, type.getHeight());

        return new VehicleHitbox(Collections.singletonList(new HitboxBox(minX, minY, minZ, maxX, maxY, maxZ)), true);
    }

    public @NotNull List<HitboxBox> getBoxes() {
        return boxes;
    }

    /**
     * @return true if the hitbox was generated because the vehicle does not configure one
     */
    public boolean isGenerated() {
        return generated;
    }

    /**
     * @return the distance from the origin to the farthest corner, used to skip far vehicles quickly
     */
    public double getRadius() {
        return radius;
    }

    public boolean contains(@NotNull Vector local) {
        for (HitboxBox box : boxes) {
            if (box.contains(local)) return true;
        }
        return false;
    }

    /**
     * @return the closest distance along the local ray where it enters a box, or -1 if it misses all of them
     */
    public double intersect(@NotNull Vector origin, @NotNull Vector direction, double maxDistance) {
        double best = -1;
        for (HitboxBox box : boxes) {
            double t = box.intersect(origin, direction, maxDistance);
            if (t >= 0 && (best < 0 || t < best)) best = t;
        }
        return best;
    }

    /**
     * Finds the seat the player clicked on.
     *
     * @param type  the vehicle type
     * @param local the clicked point in the vehicle's local space
     * @return {@link SeatSelection#DRIVER}, the passenger seat index, or {@link SeatSelection#NONE} if the point is not close to a seat
     */
    public static int seatAt(@NotNull AbstractVehicle type, @Nullable Vector local) {
        if (local == null) return SeatSelection.NONE;

        double maxDistance = Main.hitboxSeatSelectRadius;
        if (maxDistance <= 0) return SeatSelection.NONE;

        int best = SeatSelection.NONE;
        double bestDistance = maxDistance * maxDistance;

        Vector driver = type.getDriverSeatLocal();
        double d = horizontalDistanceSquared(driver, local);
        if (d <= bestDistance) {
            best = SeatSelection.DRIVER;
            bestDistance = d;
        }

        List<Vector> passengers = type.getPassagerSeatsLocal();
        for (int i = 0; i < passengers.size(); i++) {
            d = horizontalDistanceSquared(passengers.get(i), local);
            if (d < bestDistance) {
                best = i;
                bestDistance = d;
            }
        }

        return best;
    }

    private static double horizontalDistanceSquared(Vector a, Vector b) {
        double x = a.getX() - b.getX();
        double z = a.getZ() - b.getZ();
        return x * x + z * z;
    }

    @Override
    public String toString() {
        return "{boxes=" + boxes + ", generated=" + generated + "}";
    }
}
