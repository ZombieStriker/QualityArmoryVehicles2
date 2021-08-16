package me.zombie_striker.qav.util;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.event.player.PlayerKickEvent;

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

    public static boolean isFork() {
        return paper;
    }
}
