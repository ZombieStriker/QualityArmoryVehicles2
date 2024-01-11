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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class AddWhitelistMenu extends Menu {
    private final OverviewMenu overview;
    private final VehicleEntity ve;

    public AddWhitelistMenu(@NotNull Player player, OverviewMenu overview, VehicleEntity vehicle) {
        super(4, MessagesConfig.MENU_ADD_ALLOWED_TITLE.replace("%cartype%", vehicle.getType().getDisplayname()), player);
        this.overview = overview;
        this.ve = vehicle;
    }

    @Override
    public void setupItems() {
        this.setPageButtons();

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (ve.getWhiteList() == null || !ve.getWhiteList().contains(online.getUniqueId())) {
                if (Main.useHeads) {
                    this.addItem(new GuiItem(ItemFact.askull(online.getName(), ChatColor.YELLOW + online.getName()), getAction(online)));
                } else {
                    this.addItem(new GuiItem(ItemFact.a(Material.STONE, ChatColor.YELLOW + online.getName()), getAction(online)));
                }
            }
        }
    }

    private GuiAction<InventoryClickEvent> getAction(Player player) {
        return event -> {
            Main.DEBUG("Opend add whitelist");
            event.getWhoClicked().sendMessage(Main.prefix + MessagesConfig.MESSAGE_ADD_PLAYER_WHITELIST.replace("%name%", player.getName()));
            if (!ve.allowUserDriver(player.getUniqueId()))
                ve.addToWhitelist(player.getUniqueId());
            overview.open();
            Main.DEBUG("Added to whitelist");
        };
    }
}
