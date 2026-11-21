package me.zombie_striker.qav.tracks.assign;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class TrainAssignListener implements Listener {

    private final @NotNull TrainAssignController controller;

    public TrainAssignListener(@NotNull TrainAssignController controller) {
        this.controller = controller;
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;

        Player p = e.getPlayer();
        if (!controller.hasSession(p)) return;

        if (controller.handlePreviewInteract(p))
            e.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        controller.discardSessionQuietly(e.getPlayer());
    }
}
