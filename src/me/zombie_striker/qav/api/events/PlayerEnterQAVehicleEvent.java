package me.zombie_striker.qav.api.events;

import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class PlayerEnterQAVehicleEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancel = false;
	private final VehicleEntity ve;
	private final Player player;

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

	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}