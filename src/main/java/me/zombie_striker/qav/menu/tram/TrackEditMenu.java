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

public class TrackEditMenu extends Menu {

    private final @NotNull String trackId;
    private final @Nullable Menu back;

    public TrackEditMenu(@NotNull Player player, @NotNull String trackId, @Nullable Menu back) {
        super(3, MessagesConfig.MENU_TRAM_EDIT_TITLE, player);
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

        this.setItem(11, new GuiItem(ItemFact.a(XMaterial.OAK_SIGN.parseMaterial(),
                MessagesConfig.MENU_TRAM_LABEL_STOPS,
                MessagesConfig.MENU_TRAM_LORE_STOPS_DESCRIPTION,
                MessagesConfig.MENU_TRAM_LORE_CURRENT.replace("%value%", String.valueOf(track.getStops().size()))), (e) ->
                new TrackStopsMenu((Player) e.getWhoClicked(), trackId, this).open()));

        this.setItem(13, new GuiItem(ItemFact.a(XMaterial.MINECART.parseMaterial(),
                MessagesConfig.MENU_TRAM_LABEL_TRAINS,
                MessagesConfig.MENU_TRAM_LORE_TRAINS_DESCRIPTION,
                MessagesConfig.MENU_TRAM_LORE_CURRENT.replace("%value%", String.valueOf(track.getTrains().size()))), (e) ->
                new TrackTrainsMenu((Player) e.getWhoClicked(), trackId, this).open()));

        String loopState = track.isLooping() ? MessagesConfig.MENU_TRAM_LOOP_ENABLED : MessagesConfig.MENU_TRAM_LOOP_DISABLED;
        this.setItem(15, new GuiItem(ItemFact.a(XMaterial.REPEATER.parseMaterial(),
                MessagesConfig.MENU_TRAM_LABEL_LOOPING,
                MessagesConfig.MENU_TRAM_LORE_LOOPING_DESCRIPTION,
                MessagesConfig.MENU_TRAM_LORE_CURRENT.replace("%value%", loopState)), (e) -> {
            track.setLooping(!track.isLooping());
            e.getWhoClicked().sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_LOOP_STATUS.replace("%state%",
                    track.isLooping() ? MessagesConfig.MENU_TRAM_LOOP_ENABLED : MessagesConfig.MENU_TRAM_LOOP_DISABLED));
            try {
                Main.tracksManager.saveAll();
            } catch (Exception ex) {
                Main.getPlugin(Main.class).getLogger().log(java.util.logging.Level.SEVERE, "Failed to save tram tracks", ex);
                e.getWhoClicked().sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_SAVE_FAILED);
            }

            this.setupItems();
            this.update();
        }));

        if (back != null) {
            this.setItem(22, new GuiItem(
                    ItemFact.a(XMaterial.BARRIER.parseMaterial(), MessagesConfig.MENU_TRAM_BACK),
                    (e) -> back.open()));
        }
    }
}
