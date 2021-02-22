package me.zombie_striker.qav.api.events;

import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class VehicleDamageEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancel = false;
	private VehicleEntity ve;
	private double damage;

	public VehicleDamageEvent(VehicleEntity ve, double damage) {
		this.ve = ve;
		this.damage = damage;
	}

	public VehicleEntity getVehicle() {
		return ve;
	}

	public void setDamage(double d) {
		this.damage = d;
	}

	public double getDamage() {
		return damage;
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