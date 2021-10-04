package me.zombie_striker.qav.util;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.Nullable;

public class ForksUtil {
    private static boolean paper = false;

    public static void init() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            paper = true;
        } catch (ClassNotFoundException ignored) {}

        if (isFork()) {
            QualityArmoryVehicles.getPlugin().getLogger().info("Found spigot fork. Loaded support.");
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean isFlyKick(PlayerKickEvent event) {
        if(paper){
            try {
                return event.getCause().name().equals("FLYING_PLAYER");
            } catch (NoSuchMethodError ignored) {}
        }

        return event.getReason().equals("Flying is not enabled on this server");
    }

    @SuppressWarnings("deprecation")
    public static void sendComponent(CommandSender player, String message, @Nullable String hover, @Nullable String clickURL) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        if (hover != null)
            hover = ChatColor.translateAlternateColorCodes('&', hover);

        if (paper) {
            try {
                Component component = Component.text(message);

                if (hover != null) {
                    component = component.hoverEvent(HoverEvent.showText(Component.text(hover)));
                }

                if (clickURL != null) {
                    component = component.clickEvent(ClickEvent.openUrl(clickURL));
                }

                player.sendMessage(component);
                return;
            } catch (Exception | Error ignored) {}
        }

        BaseComponent component = new TextComponent(message);

        if (hover != null) {
            component.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(hover)}));
        }
        if (clickURL != null) {
            component.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, clickURL));
        }

        player.spigot().sendMessage(component);
    }

    public static boolean isFork() {
        return paper;
    }
}
