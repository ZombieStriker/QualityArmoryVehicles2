package me.zombie_striker.qav.hooks;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.QAVListener;
import me.zombie_striker.qg.api.QAWeaponDamageEntityEvent;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class QualityArmoryListener implements Listener {
    @EventHandler
    public void onDamage(QAWeaponDamageEntityEvent event) {
        // Only apply this fix when in creative mode
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;

        Main.DEBUG("QualityArmoryListener: " + event.getDamage() + " " + event.getPlayer().getName());
        QAVListener.handleDamage(event, event.getDamaged(), event.getDamage(), EntityDamageEvent.DamageCause.CUSTOM, event::setDamage);
    }
}
