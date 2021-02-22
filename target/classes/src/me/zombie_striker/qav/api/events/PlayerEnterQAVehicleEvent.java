package me.zombie_striker.qav.api.events;

import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PlayerEnterQAVehicleEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancel = false;
	private VehicleEntity ve;
	private Player player;

	public PlayerEnterQAVehicleEvent(VehicleEntity ve, Player p) {
		this.ve = ve;
		this.player = p;
	}
	public Player getPlayer() {
		return player;
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