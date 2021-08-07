package me.zombie_striker.qav.api.events;

import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public final class AntiFlyKickEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private boolean blockKick = true;

    public AntiFlyKickEvent(Player p) {
        super(p);
    }

    public boolean blockKick() {
        return blockKick;
    }

    public void blockKick(boolean blockKick) {
        this.blockKick = blockKick;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
