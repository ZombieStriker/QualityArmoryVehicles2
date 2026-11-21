package me.zombie_striker.qav.util;

import com.tcoded.folialib.impl.PlatformScheduler;
import me.zombie_striker.qav.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

/**
 * Small helpers on top of FoliaLib to run code on the thread that owns an entity or location.
 * <p>
 * On Spigot/Paper every helper behaves like the plain Bukkit scheduler (main thread).
 * On Folia, entities and blocks can only be accessed by the region thread owning them,
 * so work is executed immediately when the current thread already owns the target and
 * scheduled on the owning region otherwise.
 */
public final class SchedulerUtil {

    private SchedulerUtil() {
    }

    public static boolean isFolia() {
        return Main.foliaLib != null && Main.foliaLib.isFolia();
    }

    public static @NotNull PlatformScheduler scheduler() {
        return Main.foliaLib.getScheduler();
    }

    /**
     * @return true if the current thread can safely access the given entity
     */
    public static boolean isOwned(@NotNull Entity entity) {
        if (!isFolia()) return Bukkit.isPrimaryThread();
        return scheduler().isOwnedByCurrentRegion(entity);
    }

    /**
     * @return true if the current thread can safely access the given location
     */
    public static boolean isOwned(@NotNull Location location) {
        if (!isFolia()) return Bukkit.isPrimaryThread();
        return scheduler().isOwnedByCurrentRegion(location);
    }

    /**
     * Runs the task now if the current thread owns the entity, otherwise on the entity's thread as soon as possible.
     */
    public static void runAtEntity(@NotNull Entity entity, @NotNull Runnable runnable) {
        if (isOwned(entity)) {
            runnable.run();
            return;
        }

        scheduler().runAtEntity(entity, task -> runnable.run());
    }

    /**
     * Runs the task now if the current thread owns the location, otherwise on the location's region as soon as possible.
     */
    public static void runAtLocation(@NotNull Location location, @NotNull Runnable runnable) {
        if (isOwned(location)) {
            runnable.run();
            return;
        }

        scheduler().runAtLocation(location, task -> runnable.run());
    }

    /**
     * Runs the task on the next tick of the thread owning the entity.
     */
    public static void runAtEntityNextTick(@NotNull Entity entity, @NotNull Runnable runnable) {
        scheduler().runAtEntityLater(entity, runnable, 1L);
    }

    /**
     * Runs the step every tick until it returns false. On Folia every execution happens on the region
     * owning the (mutable) location at the time it is scheduled, so the location can move across regions.
     * The step should return {@code true} as soon as {@link #isOwned(Location)} is false for the location.
     */
    public static void runAtMovingLocationTimer(@NotNull Location location, @NotNull BooleanSupplier step) {
        if (!isFolia()) {
            scheduler().runTimer(task -> {
                if (!step.getAsBoolean()) task.cancel();
            }, 1L, 1L);
            return;
        }

        scheduler().runAtLocationLater(location.clone(), () -> {
            if (step.getAsBoolean())
                runAtMovingLocationTimer(location, step);
        }, 1L);
    }

    /**
     * Teleports an entity. On Folia {@link Entity#teleport(Location)} is not supported, so the teleport is done asynchronously.
     */
    public static void teleport(@NotNull Entity entity, @NotNull Location location) {
        if (isFolia()) {
            scheduler().teleportAsync(entity, location);
        } else {
            entity.teleport(location);
        }
    }
}
