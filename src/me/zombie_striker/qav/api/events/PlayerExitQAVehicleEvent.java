package me.zombie_striker.qav.api.events;

import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class PlayerExitQAVehicleEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private final VehicleEntity ve;
	private final Player player;

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
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}