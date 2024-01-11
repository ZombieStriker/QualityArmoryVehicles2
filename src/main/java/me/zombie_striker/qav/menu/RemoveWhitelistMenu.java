package me.zombie_striker.qav.menu;

import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import me.zombie_striker.qav.ItemFact;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RemoveWhitelistMenu extends Menu {
    private final OverviewMenu overview;
    private final VehicleEntity ve;

    public RemoveWhitelistMenu(@NotNull Player player, OverviewMenu overview, VehicleEntity vehicle) {
        super(4, MessagesConfig.MENU_REMOVE_ALLOWED_TITLE.replace("%cartype%", vehicle.getType().getDisplayname()), player);
        this.overview = overview;
        this.ve = vehicle;
    }

    @Override
    public void setupItems() {
        this.setPageButtons();

        if (ve.getWhiteList() != null) {
            for (UUID uuid : ve.getWhiteList()) {
                if (!uuid.equals(ve.getOwner())) {
                    OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
                    String name = offline.getName();
                    if (Main.useHeads) {
                        this.addItem(new GuiItem(ItemFact.askull(name, ChatColor.YELLOW + name), getAction(offline)));
                    } else {
                        this.addItem(new GuiItem(ItemFact.a(Material.STONE, ChatColor.YELLOW + name), getAction(offline)));
                    }
                }
            }
        }

    }

    private GuiAction<InventoryClickEvent> getAction(OfflinePlayer player) {
        return event -> {
            Main.DEBUG("Clicked remove whitelist");
            if (ve != null) {
                if (MessagesConfig.MESSAGE_REMOVE_PLAYER_WHITELIST != null)
                    event.getWhoClicked().sendMessage(
                            Main.prefix + MessagesConfig.MESSAGE_REMOVE_PLAYER_WHITELIST.replace("%name%", player.getName() == null ? "Unknown" : player.getName()));
                if (ve.allowUserDriver(player.getUniqueId()))
                    ve.removeFromWhitelist(player.getUniqueId());
                overview.open();
                Main.DEBUG("Removed from whitelist");
            }
        };
    }

}
