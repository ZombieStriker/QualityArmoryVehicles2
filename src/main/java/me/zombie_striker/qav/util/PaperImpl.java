package me.zombie_striker.qav.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class PaperImpl {
    private static Method sendMessage;

    static {
        try {
            sendMessage = CommandSender.class.getMethod("sendMessage", Component.class);
        } catch (Throwable ignored) {
            sendMessage = null;
        }
    }

    public static boolean sendComponent(CommandSender player, String message, @Nullable String hover, @Nullable String clickURL) {
        if (sendMessage == null) return false;

        try {
            Component component = Component.text(message);

            if (hover != null) {
                component = component.hoverEvent(HoverEvent.showText(Component.text(hover)));
            }

            if (clickURL != null) {
                component = component.clickEvent(ClickEvent.openUrl(clickURL));
            }

            sendMessage.invoke(player, component);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

}
