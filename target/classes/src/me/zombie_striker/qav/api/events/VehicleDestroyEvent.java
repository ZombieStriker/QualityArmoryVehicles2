package me.zombie_striker.qav.api.events;

import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class VehicleDestroyEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancel = false;
	private VehicleEntity ve;

	public VehicleDestroyEvent(VehicleEntity ve) {
		this.ve = ve;
	}
	public VehicleEntity getVehicle() {
		return ve;
	}

	public boolean isCanceled() {
		return cancel;
	}

	public void setCanceled(boolean canceled) {
		this.cancel = canceled;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}