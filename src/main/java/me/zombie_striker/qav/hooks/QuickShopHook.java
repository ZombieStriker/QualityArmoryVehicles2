package me.zombie_striker.qav.hooks;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.maxgamer.quickshop.api.event.ProtectionCheckStatus;
import org.maxgamer.quickshop.api.event.ShopProtectionCheckEvent;

public class QuickShopHook implements Listener {
    private volatile Location protectionCheckingLocation = null;

    @EventHandler
    public void onQuickShopProtectionChecking(ShopProtectionCheckEvent event){
        if(event.getStatus().equals(ProtectionCheckStatus.BEGIN)) {
            protectionCheckingLocation = event.getLocation();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event){
        if(protectionCheckingLocation != null && event.getBlock().getLocation().equals(protectionCheckingLocation)) {
            protectionCheckingLocation = null;

            if (QualityArmoryVehicles.isVehicleByItem(event.getPlayer().getInventory().getItemInMainHand()))
                event.setCancelled(false);
        }
    }

}