package me.zombie_striker.qav.menu;

import com.cryptomorin.xseries.XMaterial;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.InteractionModifier;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.zombie_striker.qav.ItemFact;
import me.zombie_striker.qav.MessagesConfig;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public abstract class Menu extends PaginatedGui {
    protected final Player player;

    public Menu(int rows, @NotNull String title, @NotNull Player player) {
        super(rows, (rows - 1) * 9, ChatColor.translateAlternateColorCodes('&', title), EnumSet.noneOf(InteractionModifier.class));
        this.player = player;

        this.setDefaultClickAction((e) -> e.setCancelled(true));
    }

    public abstract void setupItems();

    public void open() {
        this.open(player);
    }

    @Override
    public void open(@NotNull HumanEntity player, int openPage) {
        this.setupItems();

        super.open(player, openPage);
    }

    public void setPageButtons() {
        this.setItem(this.getRows(), 4, new GuiItem(ItemFact.a(XMaterial.STONE_BUTTON.parseMaterial(), MessagesConfig.PREV_PAGE), (e) -> this.previous()));
        this.setItem(this.getRows(), 6, new GuiItem(ItemFact.a(XMaterial.STONE_BUTTON.parseMaterial(), MessagesConfig.PREV_PAGE), (e) -> this.next()));
    }

    public Player getPlayer() {
        return player;
    }
}
