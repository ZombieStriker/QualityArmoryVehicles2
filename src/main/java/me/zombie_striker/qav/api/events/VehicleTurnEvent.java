package me.zombie_striker.qav.api.events;

import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class VehicleTurnEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancel = false;
	private final VehicleEntity ve;
	private final double forwardBefore;
	private double forwardNow;

	public VehicleTurnEvent(VehicleEntity ve, double fowardFrom, double forwardNow) {
		this.ve = ve;
		this.forwardBefore = fowardFrom;
		this.forwardNow = forwardNow;
	}

	public VehicleEntity getVehicle() {
		return ve;
	}
	/**
	 * Sets the rotation in radians
	 * @param rotation New rotation
	 */
	public void setNewRotationAngle(double rotation) {
		this.forwardNow = rotation;
	}

	public double getOldRotationAngle() {
		return forwardBefore;
	}
	public double getNewRotationAngle() {
		return forwardNow;
	}

	public boolean isCanceled() {
		return cancel;
	}

	public void setCanceled(boolean canceled) {
		this.cancel = canceled;
	}

	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
