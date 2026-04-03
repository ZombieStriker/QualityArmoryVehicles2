package me.zombie_striker.qav.tracks.editor;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.vehicles.AbstractTrain;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class TrackEditorListener implements Listener {

    private final @NotNull TrackEditorController editor;

    public TrackEditorListener(@NotNull TrackEditorController editor) {
        this.editor = editor;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClickRail(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        Block clicked = e.getClickedBlock();
        if (clicked == null) return;
        if (editor.getSession(e.getPlayer()) == null) return;

        AbstractTrain trainPrototype = Main.vehicleTypes.stream()
                .filter(v -> v instanceof AbstractTrain)
                .map(v -> (AbstractTrain) v)
                .findFirst()
                .orElse(null);
        if (trainPrototype == null) return;
        if (!trainPrototype.isRail(clicked.getLocation())) return;
        if (e.getPlayer().isConversing()) return;

        e.setCancelled(true);
        editor.handleRailClickForStop(e.getPlayer(), clicked);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        editor.stopEditing(e.getPlayer());
    }
}
