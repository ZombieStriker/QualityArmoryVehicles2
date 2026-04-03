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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TracksMenu extends Menu {

    public TracksMenu(@NotNull Player player) {
        super(4, MessagesConfig.MENU_TRAM_TRACKS_TITLE, player);
    }

    @Override
    public void setupItems() {
        this.clearPageItems();
        this.setPageButtons();

        List<Track> tracks = new ArrayList<>(Main.tracksManager.getTracks());
        tracks.sort(Comparator.comparing(Track::getId, String.CASE_INSENSITIVE_ORDER));

        for (Track track : tracks) {
            String name = MessagesConfig.colorize("&6" + track.getId());
            String[] lore = new String[]{
                    MessagesConfig.MENU_TRAM_TRACK_ID.replace("%id%", track.getId()),
                    MessagesConfig.MENU_TRAM_TRACK_WORLD.replace("%world%", track.getWorldName()),
                    MessagesConfig.MENU_TRAM_TRACK_STOPS.replace("%count%", String.valueOf(track.getStops().size())),
                    MessagesConfig.MENU_TRAM_TRACK_TRAINS.replace("%count%", String.valueOf(track.getTrains().size())),
                    "",
                    MessagesConfig.MENU_TRAM_EDIT_HINT
            };

            this.addItem(new GuiItem(ItemFact.a(XMaterial.RAIL.parseMaterial(), name, lore), (e) -> {
                new TrackEditMenu((Player) e.getWhoClicked(), track.getId(), this).open();
            }));
        }

        this.setItem(this.getRows(), 2, new GuiItem(ItemFact.a(XMaterial.ANVIL.parseMaterial(), MessagesConfig.MENU_TRAM_CREATE_TRACK,
                MessagesConfig.MENU_TRAM_CREATE_TRACK_LORE), (e) -> {
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().sendMessage(Main.prefix + MessagesConfig.MENU_TRAM_CREATE_TRACK_CHAT);
        }));
    }
}

