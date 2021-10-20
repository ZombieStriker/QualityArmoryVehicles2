package me.zombie_striker.qav.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class PaperImpl {

    public static void sendComponent(CommandSender player, String message, @Nullable String hover, @Nullable String clickURL) {
        try {
            Component component = Component.text(message);

            if (hover != null) {
                component = component.hoverEvent(HoverEvent.showText(Component.text(hover)));
            }

            if (clickURL != null) {
                component = component.clickEvent(ClickEvent.openUrl(clickURL));
            }

            player.sendMessage(component);
        } catch (Throwable ignored) {}
    }

}
