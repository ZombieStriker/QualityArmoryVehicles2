package me.zombie_striker.qav.api.events;

import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public final class VehicleSpawnEvent extends PlayerEvent {
	private static final HandlerList handlers = new HandlerList();

	private boolean cancel = false;
	private final VehicleEntity ve;

	public VehicleSpawnEvent(Player player, VehicleEntity ve) {
		super(player);
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

	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}