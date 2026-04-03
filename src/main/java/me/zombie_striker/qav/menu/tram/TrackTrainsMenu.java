package me.zombie_striker.qav.menu.tram;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.guis.GuiItem;
import me.zombie_striker.qav.ItemFact;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.menu.Menu;
import me.zombie_striker.qav.tracks.data.Track;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TrackTrainsMenu extends Menu {

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

        List<String> trains = new ArrayList<>(track.getTrains());
        for (int i = 0; i < trains.size(); i++) {
            String train = trains.get(i);
            int index = i;
            this.addItem(new GuiItem(ItemFact.a(XMaterial.MINECART.parseMaterial(),
                    "&6" + train,
                    "",
                    MessagesConfig.MENU_TRAM_REMOVE_HINT), (e) -> {
                if (e.isRightClick()) {
                    List<String> newList = new ArrayList<>(track.getTrains());
                    if (index < newList.size()) {
                        newList.remove(index);
                        track.setTrains(newList);
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

