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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
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

        GuiAction<InventoryClickEvent> action = event -> {
            Main.DEBUG("Clicked remove whitelist");
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR
                    || !event.getCurrentItem().hasItemMeta())
                return;
            OfflinePlayer clicked = getClickedSkull(event.getCurrentItem());
            if (clicked != null && ve != null) {
                if (MessagesConfig.MESSAGE_REMOVE_PLAYER_WHITELIST != null)
                    event.getWhoClicked().sendMessage(
                            MessagesConfig.MESSAGE_REMOVE_PLAYER_WHITELIST.replace("%name%", Objects.requireNonNull(clicked.getName())));
                if (ve.allowUserDriver(clicked.getUniqueId()))
                    ve.removeFromWhitelist(clicked.getUniqueId());
                overview.open();
                Main.DEBUG("Removed from whitelist");
            }
        };

        if (ve.getWhiteList() != null) {
            for (UUID uuid : ve.getWhiteList()) {
                if (!uuid.equals(ve.getOwner())) {
                    String name = Bukkit.getOfflinePlayer(uuid).getName();
                    if(Main.useHeads) {
                        this.addItem(new GuiItem(ItemFact.askull(name, ChatColor.YELLOW + name), action));
                    }else{
                        this.addItem(new GuiItem(ItemFact.a(Material.STONE, ChatColor.YELLOW + name), action));
                    }
                }
            }
        }

    }

    @SuppressWarnings("deprecation")
    private OfflinePlayer getClickedSkull(ItemStack clicked) {
        try {
            if (clicked != null && clicked.getType() != Material.PLAYER_HEAD) {
                Bukkit.getOfflinePlayer(ChatColor.stripColor(clicked.getItemMeta().getDisplayName()));
            }
        }catch (Error|Exception e4){
            if (clicked.getType().name().equals("SKULL_ITEM")) {
                Bukkit.getOfflinePlayer(ChatColor.stripColor(clicked.getItemMeta().getDisplayName()));
            }
        }
        try {
            return clicked == null ? null : ((SkullMeta) clicked.getItemMeta()).getOwningPlayer();
        } catch (Error | Exception ignored) {
        }
        try {
            return Bukkit.getOfflinePlayer(Objects.requireNonNull(((SkullMeta) clicked.getItemMeta()).getOwner()));
        }catch(Error|Exception e4){
            return Bukkit.getOfflinePlayer(ChatColor.stripColor(clicked.getItemMeta().getDisplayName()));
        }
    }
}
