package me.zombie_striker.qav.nms;

import com.cryptomorin.xseries.ReflectionUtils;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class NMSUtil {
    private static final Map<String, String> MAPPINGS = new HashMap<>();
    private static Method teleport;
    private static Method getHandle;

    public static void init() {
        try {
            InputStream stream = Main.class.getClassLoader().getResourceAsStream("mappings.yml");
            if (stream == null) {
                QualityArmoryVehicles.getPlugin().getLogger().warning("Unable to load mappings data. NMS method won't be used and this may cause bugs. Please report this error.");
                return;
            }

            Reader reader = new InputStreamReader(stream);
            YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);

            for (String key : config.getKeys(false)) {
                MAPPINGS.put(key, config.getString(key + "." + ReflectionUtils.NMS_VERSION));
            }

            reader.close();
            stream.close();
        } catch (Exception e) {
            QualityArmoryVehicles.getPlugin().getLogger().warning("Unable to load mappings data. NMS method won't be used and this may cause bugs. Please report this error.");
        }

        Class<?> entityClass = ReflectionUtils.getCraftClass("entity.CraftEntity");
        if (entityClass != null) {
            try {
                getHandle = entityClass.getMethod("getHandle");
            } catch (Exception | Error ignored) {}
        }

        Class<?> entity = ReflectionUtils.getNMSClass("world.entity","Entity");
        if (entity != null) {
            try {
                teleport = entity.getMethod(MAPPINGS.get("absMoveTo"), double.class,double.class,double.class,float.class,float.class);
            } catch (Exception | Error ignored) {
                try {
                    teleport = entity.getMethod("setLocation", double.class,double.class,double.class,float.class,float.class);
                } catch (Exception | Error ignored2) {}
            }
        }

        QualityArmoryVehicles.getPlugin().getLogger().info("Loaded NMS mappings & support. NMS version: " + ReflectionUtils.NMS_VERSION);
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
