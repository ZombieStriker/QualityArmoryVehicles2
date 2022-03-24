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

        GuiAction<InventoryClickEvent> action = event -> {
            Main.DEBUG("Opend add whitelist");
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR
                    || !event.getCurrentItem().hasItemMeta())
                return;

            OfflinePlayer clicked = getClickedSkull(event.getCurrentItem());
            if (clicked != null) {
                event.getWhoClicked().sendMessage(MessagesConfig.MESSAGE_ADD_PLAYER_WHITELIST.replace("%name%", Objects.requireNonNull(clicked.getName())));
                if (!ve.allowUserDriver(clicked.getUniqueId()))
                    ve.addToWhitelist(clicked.getUniqueId());
                overview.open();
                Main.DEBUG("Added to whitelist");
            }
        };

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (ve.getWhiteList() == null || !ve.getWhiteList().contains(online.getUniqueId())) {
                if(Main.useHeads) {
                    this.addItem(new GuiItem(ItemFact.askull(online.getName(), ChatColor.YELLOW + online.getName()), action));
                }else{
                    this.addItem(new GuiItem(ItemFact.a(Material.STONE, ChatColor.YELLOW + online.getName()), action));
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
