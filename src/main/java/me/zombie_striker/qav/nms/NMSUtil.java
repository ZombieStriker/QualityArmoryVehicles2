package me.zombie_striker.qav.nms;

import com.cryptomorin.xseries.reflection.XReflection;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.lang.reflect.Method;

public final class NMSUtil {
    private static Method teleport;
    private static Method getHandle;

    @SuppressWarnings("deprecation")
    public static void init() {
        if (!XReflection.supports(18)) {
            QualityArmoryVehicles.getPlugin().getLogger().info("[NMS] Legacy NMS support loaded.");
            return;
        }

        try {
            try {
                Class<?> entityClass = XReflection.getCraftClass("entity.CraftEntity");
                getHandle = entityClass.getMethod("getHandle");
            } catch (Exception | Error ignored) {
                Main.DEBUG("[NMS] Unable to find getHandle method. This may cause bugs. Please report this error.");
            }

            Class<?> entity = XReflection.getNMSClass("world.entity", "Entity");
            try {
                teleport = entity.getMethod("a", double.class, double.class, double.class, float.class, float.class);
            } catch (Exception | Error ignored) {
                try {
                    teleport = entity.getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
                } catch (Exception | Error ignored2) {
                    Main.DEBUG("[NMS] Unable to find teleport method. This may cause bugs. Please report this error.");
                }
            }

            QualityArmoryVehicles.getPlugin().getLogger().info("[NMS] Modern NMS support loaded.");
        } catch (Exception | Error e) {
            Main.DEBUG("[NMS] An exception occurred while loading data. This may cause bugs. Please report this error.");
        }
    }

    public static void teleport(Entity entity, Location to) {
        if (getHandle == null || teleport == null) {
            Main.DEBUG(getHandle == null ? "getHandle is null" : "teleport is null");
            fallbackTeleport(entity, to);
            return;
        }

        try {
            Main.DEBUG("Trying to teleporting with NMS.");
            teleport.invoke(getHandle.invoke(entity), to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
        } catch (Exception | Error e) {
            Main.DEBUG("Unable to teleport with NMS. Falling back to legacy teleport.");
            fallbackTeleport(entity, to);
        }
    }

    @SuppressWarnings("deprecation")
    private static void fallbackTeleport(Entity entity, Location to) {
        Main.DEBUG("Falling back to legacy teleport.");
        final Entity rider = entity.getPassenger();
        entity.eject();
        if (rider != null) rider.teleport(to);
        entity.teleport(to);

        if (rider != null) {
            Bukkit.getScheduler().runTaskLater(QualityArmoryVehicles.getPlugin(), () -> entity.setPassenger(rider), 2L);
        }
    }
}
