package me.zombie_striker.qav.api.events;

import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when an animal is unhitched from a carriage. The player is null when the animal is released
 * automatically (e.g. the carriage has been destroyed or picked up); in that case cancelling has no effect.
 */
public final class VehicleUnhitchEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancel = false;
    private final VehicleEntity vehicle;
    private final LivingEntity animal;
    private final Player player;
    private final int hitchPoint;

    public VehicleUnhitchEvent(@NotNull VehicleEntity vehicle, @NotNull LivingEntity animal, @Nullable Player player, int hitchPoint) {
        this.vehicle = vehicle;
        this.animal = animal;
        this.player = player;
        this.hitchPoint = hitchPoint;
    }

    public @NotNull VehicleEntity getVehicle() {
        return vehicle;
    }

    public @NotNull LivingEntity getAnimal() {
        return animal;
    }

    public @Nullable Player getPlayer() {
        return player;
    }

    /**
     * @return the index of the hitch point (as configured in the vehicle's hitch.points list)
     */
    public int getHitchPoint() {
        return hitchPoint;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
