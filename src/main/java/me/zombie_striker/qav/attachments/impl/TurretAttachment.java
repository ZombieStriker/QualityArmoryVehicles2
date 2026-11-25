package me.zombie_striker.qav.attachments.impl;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.attachments.Attachment;
import me.zombie_striker.qav.attachments.AttachmentState;
import me.zombie_striker.qav.finput.FInput;
import me.zombie_striker.qav.finput.FInputManager;
import me.zombie_striker.qav.input.VehicleInput;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * A model part rotated with A/D by the player sitting in the controlling seat, that can fire an {@link FInput}
 * from its barrel (e.g. the cannon of a tank).
 */
public class TurretAttachment extends Attachment {
    private final Map<VehicleInput, String> fire = new EnumMap<>(VehicleInput.class);
    private Vector pivot = new Vector();
    private Vector muzzle = new Vector(1, 0, 0);
    private double turnSpeed = 0.05;
    private double maxYaw = 0;
    private int cooldown = 20;

    public TurretAttachment(String name, int id, Material material, Vector vector) {
        super(name, id, material, vector);
        setSeat(DRIVER_SEAT);
    }

    public static @NotNull TurretAttachment create(@NotNull String name, int id, @Nullable Material material,
                                                   @NotNull Vector position, @NotNull ConfigurationSection config) {
        TurretAttachment turret = new TurretAttachment(name, id, material, position);
        turret.setPivot(config.getVector("pivot", new Vector()));
        turret.setMuzzle(config.getVector("muzzle", new Vector(1, 0, 0)));
        turret.setTurnSpeed(config.getDouble("turnSpeed", 0.05));
        turret.setMaxYaw(Math.toRadians(config.getDouble("maxYaw", 0)));
        turret.setCooldown(config.getInt("cooldown", 20));

        ConfigurationSection fire = config.getConfigurationSection("fire");
        if (fire != null) {
            for (String key : fire.getKeys(false)) {
                VehicleInput input = VehicleInput.getByName(key);
                if (input != VehicleInput.LMB && input != VehicleInput.RMB && input != VehicleInput.F)
                    throw new IllegalArgumentException("unknown fire key " + key + " (use LMB, RMB or F)");

                String handler = fire.getString(key, "none");
                if (!handler.equalsIgnoreCase("none"))
                    turret.getFire().put(input, handler);
            }
        }
        return turret;
    }

    /**
     * Where the turret stand has to be so that the model rotates around the pivot and not around the stand.
     */
    @Override
    public @NotNull Location getLocation(@NotNull VehicleEntity ve, int index, @NotNull Location modelLocation) {
        double hull = ve.getAngleRotation();
        double turret = hull + ve.getAttachmentState(index).getYaw();

        return super.getLocation(ve, index, modelLocation)
                .add(rotate(pivot, hull))
                .subtract(rotate(pivot, turret));
    }

    @Override
    public void animate(@NotNull VehicleEntity ve, int index, @NotNull ArmorStand entity) {
        EulerAngle model = ve.getModelEntity().getHeadPose();
        EulerAngle pose = new EulerAngle(model.getX(), model.getY() + ve.getAttachmentState(index).getYaw(), model.getZ());
        if (!pose.equals(entity.getHeadPose()))
            entity.setHeadPose(pose);
    }

    @Override
    public boolean onInput(@NotNull VehicleEntity ve, int index, @NotNull Player player, @NotNull VehicleInput input) {
        AttachmentState state = ve.getAttachmentState(index);

        switch (input) {
            // Same directions used by the vehicles to turn
            case LEFT:
                state.setYaw(clamp(state.getYaw() - turnSpeed));
                return true;
            case RIGHT:
                state.setYaw(clamp(state.getYaw() + turnSpeed));
                return true;
            default:
                break;
        }

        String inputName = fire.get(input);
        if (inputName == null) return false;

        FInput handler = FInputManager.getHandler(inputName);
        if (handler == null) {
            Main.DEBUG("Unknown input " + inputName + " for turret " + getName());
            return false;
        }

        long now = System.currentTimeMillis();
        if (now - state.getLastFire() < cooldown * 50L) return true;
        state.setLastFire(now);

        Entity stand = ve.getAttachments().get(index);
        if (stand == null) return true;

        handler.onInput(ve, player, getMuzzleLocation(ve, index, stand.getLocation()), getDirection(ve, index));
        return true;
    }

    /**
     * @return where the barrel ends
     */
    public @NotNull Location getMuzzleLocation(@NotNull VehicleEntity ve, int index, @NotNull Location standLocation) {
        double turret = ve.getAngleRotation() + ve.getAttachmentState(index).getYaw();
        return standLocation.clone().add(rotate(pivot, turret)).add(rotate(muzzle, turret));
    }

    /**
     * @return where the barrel points to
     */
    public @NotNull Vector getDirection(@NotNull VehicleEntity ve, int index) {
        return rotate(new Vector(1, 0, 0), ve.getAngleRotation() + ve.getAttachmentState(index).getYaw()).normalize();
    }

    private double clamp(double yaw) {
        if (maxYaw <= 0) {
            // Unlimited: keep it in [-PI, PI]
            if (yaw > Math.PI) return yaw - Math.PI * 2;
            if (yaw < -Math.PI) return yaw + Math.PI * 2;
            return yaw;
        }
        return Math.max(-maxYaw, Math.min(maxYaw, yaw));
    }

    public Map<VehicleInput, String> getFire() {
        return fire;
    }

    /**
     * @param pivot the axis the turret rotates around, relative to the turret armor stand
     */
    public void setPivot(@NotNull Vector pivot) {
        this.pivot = pivot;
    }

    /**
     * @param muzzle the end of the barrel relative to the pivot, when the turret looks forward
     */
    public void setMuzzle(@NotNull Vector muzzle) {
        this.muzzle = muzzle;
    }

    /**
     * @param turnSpeed radians per tick
     */
    public void setTurnSpeed(double turnSpeed) {
        this.turnSpeed = turnSpeed;
    }

    /**
     * @param maxYaw the max rotation in radians from the front of the vehicle on each side, 0 for unlimited
     */
    public void setMaxYaw(double maxYaw) {
        this.maxYaw = maxYaw;
    }

    /**
     * @param cooldown ticks between two shots
     */
    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
}
