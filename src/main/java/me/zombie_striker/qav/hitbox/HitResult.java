package me.zombie_striker.qav.hitbox;

import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * A point hit on a vehicle hitbox.
 */
public final class HitResult {

    private final VehicleEntity vehicle;
    private final double distance;
    private final Vector localPoint;
    private final Location worldPoint;

    public HitResult(@NotNull VehicleEntity vehicle, double distance, @NotNull Vector localPoint, @NotNull Location worldPoint) {
        this.vehicle = vehicle;
        this.distance = distance;
        this.localPoint = localPoint;
        this.worldPoint = worldPoint;
    }

    public @NotNull VehicleEntity getVehicle() {
        return vehicle;
    }

    /**
     * @return the distance from the start of the ray
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @return the hit point in the vehicle's local space (+X front, +Y up, +Z side)
     */
    public @NotNull Vector getLocalPoint() {
        return localPoint.clone();
    }

    public @NotNull Location getWorldPoint() {
        return worldPoint.clone();
    }

    /**
     * @return the seat at the hit point, see {@link VehicleHitbox#seatAt}
     */
    public int getSeat() {
        return VehicleHitbox.seatAt(vehicle.getType(), localPoint);
    }
}
