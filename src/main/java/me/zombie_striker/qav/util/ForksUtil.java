package me.zombie_striker.qav.util;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class ForksUtil {
    private static boolean paper = false;
    private static Method getCause;

    public static void init() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            paper = true;
        } catch (ClassNotFoundException ignored) {}

        if (isFork()) {
            try {
                getCause = PlayerKickEvent.class.getDeclaredMethod("getCause");
            } catch (NoSuchMethodException ignored) {
                getCause = null;
            }

            QualityArmoryVehicles.getPlugin().getLogger().info("Found paper. Loaded support.");
        }
    }

    public static boolean isFlyKick(PlayerKickEvent event) {
        if(paper && getCause != null) {
            try {
                return getCause.invoke(event).toString().equals("FLYING_PLAYER");
            } catch (Throwable ignored) {}
        }

        return event.getReason().equals("Flying is not enabled on this server");
    }

    @SuppressWarnings("deprecation")
    public static void sendComponent(CommandSender player, String message, @Nullable String hover, @Nullable String clickURL) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        if (hover != null)
            hover = ChatColor.translateAlternateColorCodes('&', hover);

        try {
            if (paper && PaperImpl.sendComponent(player, message, hover, clickURL)) return;
        } catch (Throwable ignored) {}

        BaseComponent component = new TextComponent(message);

        if (hover != null) {
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(hover)}));
        }
        if (clickURL != null) {
            component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, clickURL));
        }

        player.spigot().sendMessage(component);
    }

    public static boolean isFork() {
        return paper;
    }
}
