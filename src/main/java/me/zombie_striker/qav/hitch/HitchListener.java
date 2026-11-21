package me.zombie_striker.qav.hitch;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class HitchListener implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        HitchManager.handleDeath(event.getEntity());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (HitchManager.cleanupOrphan(event.getRightClicked())) return;
        if (!HitchManager.isHitched(event.getRightClicked())) return;

        // Hitched animals cannot be ridden, leashed or opened while pulling a carriage
        event.setCancelled(true);
        if (event.getHand() == EquipmentSlot.HAND)
            event.getPlayer().sendMessage(Main.prefix + MessagesConfig.MESSAGE_HITCH_CANNOT_INTERACT);
    }
}
