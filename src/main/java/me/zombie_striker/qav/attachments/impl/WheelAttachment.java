package me.zombie_striker.qav.attachments.impl;

import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.attachments.Attachment;
import me.zombie_striker.qav.attachments.AttachmentState;
import me.zombie_striker.qav.input.VehicleInput;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A wheel rolling with the speed of the vehicle, front wheels can also steer when the driver turns.
 */
public class WheelAttachment extends Attachment {
    private static final long STEER_HOLD = 150;

    private double radius = 0.5;
    private @Nullable Vector center;
    private double steerAngle = 0;

    public WheelAttachment(String name, int id, Material material, Vector vector) {
        super(name, id, material, vector);
    }

    public static @NotNull WheelAttachment create(@NotNull String name, int id, @Nullable Material material,
                                                  @NotNull Vector position, @NotNull ConfigurationSection config) {
        WheelAttachment wheel = new WheelAttachment(name, id, material, position);
        wheel.setRadius(config.getDouble("radius", 0.5));
        wheel.setCenter(config.getVector("center", null));
        wheel.setSteerAngle(Math.toRadians(config.getDouble("steerAngle", 0)));
        return wheel;
    }

    @Override
    public void update(@NotNull VehicleEntity ve, int index) {
        AttachmentState state = ve.getAttachmentState(index);

        // Rolling without slipping: the distance moved in a tick over the radius
        double spin = state.getSpin() + ve.getSpeed() / radius;
        state.setSpin(spin % (Math.PI * 2));

        // Steering goes back to the center when the driver releases the key
        if (steerAngle > 0 && System.currentTimeMillis() - state.getLastInput() > STEER_HOLD) {
            double yaw = state.getYaw() * 0.6;
            state.setYaw(Math.abs(yaw) < 0.01 ? 0 : yaw);
        }
    }

    /**
     * Moves the armor stand so that the wheel rotates around its center and not around the neck of the armor stand.
     */
    @Override
    public @NotNull Location getLocation(@NotNull VehicleEntity ve, int index, @NotNull Location modelLocation) {
        AttachmentState state = ve.getAttachmentState(index);
        Vector center = this.center == null ? getHeadItemCenter(ve) : this.center;
        double heading = ve.getAngleRotation() + state.getYaw();

        // A positive head pose X moves the top of the head forward, like a wheel rolling forward
        double spin = state.getSpin();
        Vector rolled = new Vector(
                center.getX() * Math.cos(spin) + center.getY() * Math.sin(spin),
                -center.getX() * Math.sin(spin) + center.getY() * Math.cos(spin),
                center.getZ());

        return super.getLocation(ve, index, modelLocation)
                .add(rotate(center, ve.getAngleRotation()))
                .subtract(rotate(rolled, heading));
    }

    @Override
    public void animate(@NotNull VehicleEntity ve, int index, @NotNull ArmorStand e) {
        AttachmentState state = ve.getAttachmentState(index);
        EulerAngle model = ve.getModelEntity().getHeadPose();
        EulerAngle pose = new EulerAngle(model.getX() + state.getSpin(), model.getY() + state.getYaw(), model.getZ());
        if (!pose.equals(e.getHeadPose()))
            e.setHeadPose(pose);
    }

    @Override
    public boolean onInput(@NotNull VehicleEntity ve, int index, @NotNull Player player, @NotNull VehicleInput input) {
        if (steerAngle <= 0 || (input != VehicleInput.LEFT && input != VehicleInput.RIGHT)) return false;

        // Same directions used by the vehicles to turn
        AttachmentState state = ve.getAttachmentState(index);
        state.setYaw(input == VehicleInput.RIGHT ? steerAngle : -steerAngle);
        state.setLastInput(System.currentTimeMillis());
        // Do not stop the vehicle from turning
        return false;
    }

    /**
     * @param radius the radius of the wheel in blocks, used to roll it at the speed of the vehicle
     */
    public void setRadius(double radius) {
        if (radius <= 0) throw new IllegalArgumentException("the radius must be positive");
        this.radius = radius;
    }

    /**
     * @param center the center of the wheel relative to the neck of the armor stand, null to use the center of the head item
     */
    public void setCenter(@Nullable Vector center) {
        this.center = center;
    }

    /**
     * @param steerAngle radians the wheel turns when the driver turns, 0 to not steer. Steering wheels are controlled by the driver seat.
     */
    public void setSteerAngle(double steerAngle) {
        this.steerAngle = steerAngle;
        if (steerAngle > 0 && getSeat() == NO_SEAT)
            setSeat(DRIVER_SEAT);
    }
}
