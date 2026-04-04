package me.zombie_striker.qav.menu.tram;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.guis.GuiItem;
import me.zombie_striker.qav.ItemFact;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.menu.Menu;
import me.zombie_striker.qav.tracks.data.TrackStop;
import me.zombie_striker.qav.tracks.data.Track;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TrackStopsMenu extends Menu {

    private final @NotNull String trackId;
    private final @Nullable Menu back;

    public TrackStopsMenu(@NotNull Player player, @NotNull String trackId, @Nullable Menu back) {
        super(4, MessagesConfig.MENU_TRAM_STOPS_TITLE, player);
        this.trackId = trackId;
        this.back = back;
    }

    @Override
    public void setupItems() {
        Track track = Main.tracksManager.getTrack(trackId);
        if (track == null) {
            player.closeInventory();
            player.sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_TRACK_NOT_FOUND.replace("%track%", trackId));
            return;
        }

        this.clearPageItems();
        this.setPageButtons();

        List<TrackStop> stops = new ArrayList<>(track.getStops());
        stops.sort(Comparator.comparingInt(TrackStop::getOrder));

        for (TrackStop stop : stops) {
            this.addItem(new GuiItem(ItemFact.a(XMaterial.OAK_SIGN.parseMaterial(),
                    MessagesConfig.colorize("&6" + stop.getName()),
                    MessagesConfig.MENU_TRAM_STOP_ID.replace("%id%", stop.getId()),
                    MessagesConfig.MENU_TRAM_STOP_ORDER.replace("%order%", String.valueOf(stop.getOrder())),
                    MessagesConfig.MENU_TRAM_STOP_BLOCK
                            .replace("%x%", String.valueOf(stop.getBlockX()))
                            .replace("%y%", String.valueOf(stop.getBlockY()))
                            .replace("%z%", String.valueOf(stop.getBlockZ())),
                    MessagesConfig.MENU_TRAM_STOP_DWELL.replace("%seconds%", String.valueOf(stop.getDwellSeconds())),
                    "",
                    MessagesConfig.MENU_TRAM_REMOVE_HINT), (e) -> {
                if (e.isRightClick()) {
                    Main.tracksManager.removeStop(track, stop.getId());
                    try {
                        Main.tracksManager.saveAll();
                    } catch (Exception ex) {
                        Main.getPlugin(Main.class).getLogger().log(java.util.logging.Level.SEVERE, "Failed to save tram tracks", ex);
                        e.getWhoClicked().sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_SAVE_FAILED);
                    }

                    this.setupItems();
                    this.update();
                }
            }));
        }

        this.setItem(this.getRows(), 2, new GuiItem(ItemFact.a(XMaterial.EMERALD.parseMaterial(), MessagesConfig.MENU_TRAM_ADD_STOP,
                MessagesConfig.MENU_TRAM_ADD_STOP_LORE), (e) -> {
            e.getWhoClicked().closeInventory();
            Main.trackEditorController.startStopSelection((Player) e.getWhoClicked(), trackId);
        }));

        if (back != null) {
            this.setItem(this.getRows(), 8, new GuiItem(ItemFact.a(XMaterial.BARRIER.parseMaterial(), MessagesConfig.MENU_TRAM_BACK), (e) -> back.open()));
        }
    }
}

