package me.zombie_striker.qav.hitbox;

import com.cryptomorin.xseries.particles.XParticle;
import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Draws the vehicle hitboxes and seats with particles for the players that enabled it with {@code /qav hitbox}.
 * Used to tune the {@code hitbox} option of the vehicle files.
 */
public final class HitboxVisualizer {

    private static final Set<UUID> VIEWERS = ConcurrentHashMap.newKeySet();
    private static final int INTERVAL = 5;
    private static final double VIEW_DISTANCE = 48;
    private static final double STEP = 0.25;
    private static final Vector SEAT_MARKER_OFFSET = new Vector(0, 0.5, 0);

    private HitboxVisualizer() {
    }

    /**
     * @return true if the hitboxes are now shown to the player
     */
    public static boolean toggle(@NotNull Player player) {
        if (VIEWERS.remove(player.getUniqueId())) return false;
        VIEWERS.add(player.getUniqueId());
        return true;
    }

    public static void remove(@NotNull UUID player) {
        VIEWERS.remove(player);
    }

    /**
     * Called every tick by the thread owning the vehicle.
     */
    public static void tick(@NotNull VehicleEntity ve, long tick) {
        if (VIEWERS.isEmpty() || tick % INTERVAL != 0) return;

        VehicleFrame frame = VehicleFrame.of(ve);
        if (frame == null) return;

        Vector origin = frame.getOrigin();
        for (UUID uuid : VIEWERS) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.getWorld().equals(frame.getWorld())) continue;
            if (player.getLocation().toVector().distanceSquared(origin) > VIEW_DISTANCE * VIEW_DISTANCE) continue;

            try {
                draw(player, ve, frame);
            } catch (Throwable ignored) {
                // Particle not supported on this version
            }
        }
    }

    private static void draw(Player player, VehicleEntity ve, VehicleFrame frame) {
        Particle edge = XParticle.END_ROD.get();
        Particle passenger = XParticle.HAPPY_VILLAGER.get();
        Particle driver = XParticle.FLAME.get();

        if (edge != null) {
            for (HitboxBox box : ve.getType().getHitbox().getBoxes()) {
                Vector[] c = box.corners();
                int[][] edges = {{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
                for (int[] e : edges) {
                    line(player, frame, edge, c[e[0]], c[e[1]]);
                }
            }
        }

        if (driver != null) {
            point(player, frame, driver, ve.getType().getDriverSeatLocal().add(SEAT_MARKER_OFFSET));
        }
        if (passenger != null) {
            List<Vector> seats = ve.getType().getPassagerSeatsLocal();
            for (Vector seat : seats) {
                point(player, frame, passenger, seat.add(SEAT_MARKER_OFFSET));
            }
        }
    }

    private static void line(Player player, VehicleFrame frame, Particle particle, Vector from, Vector to) {
        Vector delta = to.clone().subtract(from);
        int steps = Math.max(1, (int) Math.ceil(delta.length() / STEP));
        for (int i = 0; i <= steps; i++) {
            point(player, frame, particle, from.clone().add(delta.clone().multiply((double) i / steps)));
        }
    }

    private static void point(Player player, VehicleFrame frame, Particle particle, Vector local) {
        Vector world = frame.toWorld(local);
        player.spawnParticle(particle, world.getX(), world.getY(), world.getZ(), 1, 0, 0, 0, 0);
    }
}
