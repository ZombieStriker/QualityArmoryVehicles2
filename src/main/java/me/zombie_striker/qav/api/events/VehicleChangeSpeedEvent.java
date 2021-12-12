package me.zombie_striker.qav.api.events;

import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class VehicleChangeSpeedEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancel = false;
	private final VehicleEntity ve;
	private final double forwardBefore;
	private double forwardNow;

	public VehicleChangeSpeedEvent(VehicleEntity ve, double fowardFrom, double forwardNow) {
		this.ve = ve;
		this.forwardBefore = fowardFrom;
		this.forwardNow = forwardNow;
	}

	public VehicleEntity getVehicle() {
		return ve;
	}

	public void setNewSpeed(double speed) {
		this.forwardNow = speed;
	}

	public double getOldSpeed() {
		return forwardBefore;
	}
	public double getNewSpeed() {
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
