package me.zombie_striker.qav.menu.tram;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.guis.GuiItem;
import me.zombie_striker.qav.ItemFact;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.menu.Menu;
import me.zombie_striker.qav.tracks.data.Track;
import me.zombie_striker.qav.tracks.data.TrackTrainAssignment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TrackTrainsMenu extends Menu {

    private static @NotNull String tramFacingLabel(int direction) {
        int d = direction % 4;
        if (d < 0) d += 4;

        switch (d) {
            case 0:
                return MessagesConfig.MESSAGE_TRAM_DIR_SOUTH;
            case 1:
                return MessagesConfig.MESSAGE_TRAM_DIR_EAST;
            case 2:
                return MessagesConfig.MESSAGE_TRAM_DIR_NORTH;
            case 3:
                return MessagesConfig.MESSAGE_TRAM_DIR_WEST;
            default:
                return String.valueOf(d);
        }
    }

    private final @NotNull String trackId;
    private final @Nullable Menu back;

    public TrackTrainsMenu(@NotNull Player player, @NotNull String trackId, @Nullable Menu back) {
        super(4, MessagesConfig.MENU_TRAM_TRAINS_TITLE, player);
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

        List<TrackTrainAssignment> trains = new ArrayList<>(track.getTrainAssignments());
        for (int i = 0; i < trains.size(); i++) {
            TrackTrainAssignment slot = trains.get(i);
            String train = slot.getVehicleTypeName();
            int index = i;
            String stationName = Main.trackRuntimeController.getCurrentStationNameForTrain(track, index);
            String stationLine = MessagesConfig.colorize(MessagesConfig.MENU_TRAM_TRAIN_STATION.replace("%station%",
                    stationName != null ? stationName : MessagesConfig.MENU_TRAM_TRAIN_STATION_UNKNOWN));
            String delayLine = MessagesConfig.colorize(MessagesConfig.MENU_TRAM_TRAIN_SPAWN_DELAY
                    .replace("%delay%", String.valueOf(slot.getSpawnDelaySeconds())));
            String facingLine = MessagesConfig.colorize(MessagesConfig.MENU_TRAM_TRAIN_FACING
                    .replace("%dir%", tramFacingLabel(slot.getSpawnDirection())));
            this.addItem(new GuiItem(ItemFact.a(XMaterial.MINECART.parseMaterial(),
                    MessagesConfig.colorize("&6" + train),
                    stationLine,
                    delayLine,
                    facingLine,
                    "",
                    MessagesConfig.MENU_TRAM_REMOVE_HINT), (e) -> {
                if (e.isRightClick()) {
                    List<TrackTrainAssignment> newList = new ArrayList<>(track.getTrainAssignments());
                    if (index < newList.size()) {
                        newList.remove(index);
                        track.setTrainAssignments(newList);
                        try {
                            Main.tracksManager.saveAll();
                        } catch (Exception ex) {
                            Main.getPlugin(Main.class).getLogger().log(java.util.logging.Level.SEVERE, "Failed to save tram tracks", ex);
                            e.getWhoClicked().sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_SAVE_FAILED);
                        }

                        this.setupItems();
                        this.update();
                    }
                }
            }));
        }

        this.setItem(this.getRows(), 2, new GuiItem(ItemFact.a(XMaterial.EMERALD.parseMaterial(), MessagesConfig.MENU_TRAM_ADD_TRAIN,
                MessagesConfig.MENU_TRAM_ADD_TRAIN_LORE), (e) -> {
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().sendMessage(Main.prefix + MessagesConfig.MENU_TRAM_ADD_TRAIN_CHAT.replace("%track%", trackId));
        }));

        if (back != null) {
            this.setItem(this.getRows(), 8, new GuiItem(ItemFact.a(XMaterial.BARRIER.parseMaterial(), MessagesConfig.MENU_TRAM_BACK), (e) -> back.open()));
        }
    }
}
