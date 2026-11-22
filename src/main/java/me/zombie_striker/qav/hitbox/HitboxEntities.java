package me.zombie_striker.qav.hitbox;

import com.cryptomorin.xseries.reflection.XReflection;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clickable hitbox entities that follow the vehicles.
 * <p>
 * On 1.19.4+ every vehicle gets invisible {@code Interaction} entities that cover its hitbox. The client then sends
 * a real entity click, so players can click the vehicle with an empty hand, at the normal entity reach, and walls
 * block the click like for any other entity. On older versions this class does nothing and the clicks are found by
 * {@link HitboxRaycast} instead.
 * <p>
 * Everything that uses the {@code Interaction} class is in {@link InteractionHitboxes}, which is only loaded when supported.
 */
public final class HitboxEntities {

    /** Scoreboard tag of the hitbox entities, used to find leftover ones */
    public static final String TAG = "qav_hitbox";

    private static final Map<UUID, VehicleEntity> OWNERS = new ConcurrentHashMap<>();
    private static boolean supported = false;

    private HitboxEntities() {
    }

    public static void init() {
        try {
            supported = XReflection.supports(1, 19, 4) && Class.forName("org.bukkit.entity.Interaction") != null;
        } catch (Throwable ignored) {
            supported = false;
        }
        Main.DEBUG("Interaction hitboxes supported: " + supported);
    }

    /**
     * @return true if the server supports the hitbox entities (1.19.4+)
     */
    public static boolean isSupported() {
        return supported;
    }

    /**
     * @return true if the hitbox entities are supported and enabled in the config
     */
    public static boolean isEnabled() {
        return supported && Main.hitboxInteractionEntities;
    }

    /**
     * Spawns, moves and updates the hitbox entities of a vehicle. Called every tick by the thread owning the vehicle.
     */
    public static void update(@NotNull VehicleEntity ve) {
        if (!supported) return;
        try {
            // Unloaded vehicle: the hitbox entities are not persistent and get removed with the chunk
            if (Main.hitboxInteractionEntities && !ve.isInvalid() && ve.getDriverSeat().isValid()) {
                InteractionHitboxes.update(ve);
            } else {
                InteractionHitboxes.remove(ve);
            }
        } catch (Throwable t) {
            Main.DEBUG("Failed to update the hitbox of " + ve.getVehicleUUID() + ": " + t);
        }
    }

    /**
     * Removes the hitbox entities of a vehicle.
     */
    public static void remove(@NotNull VehicleEntity ve) {
        if (!supported) return;
        try {
            InteractionHitboxes.remove(ve);
        } catch (Throwable t) {
            Main.DEBUG("Failed to remove the hitbox of " + ve.getVehicleUUID() + ": " + t);
        }
    }

    /**
     * Forgets a vehicle without touching its entities (used on Folia shutdown, where entities can't be removed;
     * they are not persistent so they are not saved).
     */
    public static void forget(@NotNull VehicleEntity ve) {
        if (!supported) return;
        InteractionHitboxes.forget(ve);
    }

    /**
     * Forgets a player that left, so the hidden entities are hidden again if they ride a vehicle after joining back.
     */
    public static void onQuit(@NotNull UUID player) {
        if (!supported) return;
        InteractionHitboxes.forgetPlayer(player);
    }

    /**
     * @return the vehicle owning the hitbox entity, or null if the entity is not a hitbox entity
     */
    public static @Nullable VehicleEntity getOwner(@Nullable Entity entity) {
        if (entity == null || OWNERS.isEmpty()) return null;
        return OWNERS.get(entity.getUniqueId());
    }

    /**
     * @return true if the entity is a vehicle hitbox entity, including leftover ones that don't belong to a vehicle anymore
     */
    public static boolean isHitboxEntity(@Nullable Entity entity) {
        if (entity == null) return false;
        if (OWNERS.containsKey(entity.getUniqueId())) return true;
        try {
            return entity.getScoreboardTags().contains(TAG);
        } catch (Throwable ignored) {
            return false;
        }
    }

    static void register(@NotNull Entity entity, @NotNull VehicleEntity owner) {
        OWNERS.put(entity.getUniqueId(), owner);
    }

    static void unregister(@NotNull Entity entity) {
        OWNERS.remove(entity.getUniqueId());
    }
}
