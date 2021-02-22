package me.zombie_striker.qav.api.events;

import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PlayerExitQAVehicleEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private VehicleEntity ve;
	private Player player;

	public PlayerExitQAVehicleEvent(VehicleEntity ve, Player p) {
		this.ve = ve;
		this.player = p;
	}
	public Player getPlayer() {
		return player;
	}
	public VehicleEntity getVehicle() {
		return ve;
	}
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}