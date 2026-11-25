package me.zombie_striker.qav.attachments;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The state of an attachment of a single vehicle. Only the yaw is saved when the server stops.
 */
public class AttachmentState {
    private double yaw;
    private double spin;
    private long lastFire;
    private long lastInput;
    private final Map<String, Object> data = new HashMap<>();

    /**
     * @return the rotation in radians relative to the vehicle (turret aim, wheel steering)
     */
    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    /**
     * @return the rotation in radians around the side axis (rolling wheels)
     */
    public double getSpin() {
        return spin;
    }

    public void setSpin(double spin) {
        this.spin = spin;
    }

    public long getLastFire() {
        return lastFire;
    }

    public void setLastFire(long lastFire) {
        this.lastFire = lastFire;
    }

    /**
     * @return when the controlling player last pressed a key for this attachment, in milliseconds
     */
    public long getLastInput() {
        return lastInput;
    }

    public void setLastInput(long lastInput) {
        this.lastInput = lastInput;
    }

    /**
     * @return free storage for custom attachments
     */
    public @NotNull Map<String, Object> getData() {
        return data;
    }
}
