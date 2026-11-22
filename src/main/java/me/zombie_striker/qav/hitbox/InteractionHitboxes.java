package me.zombie_striker.qav.hitbox;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.util.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 1.19.4+ implementation of {@link HitboxEntities}. Do not reference this class directly from version independent code.
 * <p>
 * An {@code Interaction} entity is an axis aligned box with a square base that can't rotate, so every hitbox box is
 * split into a grid of cells and each cell gets its own entity, moved with the vehicle every tick. The exact click
 * point is then computed with {@link HitboxRaycast} against the real rotated box.
 * <p>
 * The entities are hidden from players riding a vehicle: their eyes would be inside the boxes, so all of their clicks
 * (used to control the vehicle) would hit the hitbox instead.
 */
final class InteractionHitboxes {

    /** Max entities per vehicle, the cells get bigger when a vehicle would need more */
    private static final int MAX_CELLS = 48;
    /** Players riding a vehicle within this distance don't see the hitbox entities */
    private static final double HIDE_DISTANCE = 96;
    private static final int VISIBILITY_INTERVAL = 2;

    private static final Map<UUID, State> STATES = new ConcurrentHashMap<>();

    private InteractionHitboxes() {
    }

    private static final class Cell {
        /** Center of the cell in local space (at half height) */
        final Vector localCenter;
        final double size;
        final double height;
        Interaction entity;
        Location lastLocation;
        double lastHeight;

        Cell(Vector localCenter, double size, double height) {
            this.localCenter = localCenter;
            this.size = size;
            this.height = height;
        }
    }

    private static final class State {
        final VehicleHitbox hitbox;
        final double cellSize;
        final List<Cell> cells;
        final Set<UUID> hiddenFrom = ConcurrentHashMap.newKeySet();
        int ticks = 0;

        State(VehicleHitbox hitbox, double cellSize) {
            this.hitbox = hitbox;
            this.cellSize = cellSize;
            this.cells = buildCells(hitbox, cellSize);
        }
    }

    static void update(@NotNull VehicleEntity ve) {
        VehicleFrame frame = VehicleFrame.of(ve);
        if (frame == null) return;

        VehicleHitbox hitbox = ve.getType().getHitbox();
        State state = STATES.get(ve.getVehicleUUID());
        if (state == null || state.hitbox != hitbox || state.cellSize != Main.hitboxCellSize) {
            // New vehicle, or the vehicle type was reloaded
            if (state != null) removeEntities(state);
            state = new State(hitbox, Main.hitboxCellSize);
            STATES.put(ve.getVehicleUUID(), state);
        }

        World world = frame.getWorld();
        for (Cell cell : state.cells) {
            Vector center = frame.toWorld(cell.localCenter);
            double height = frame.hasPitch()
                    ? cell.height * Math.abs(frame.getPitchCos()) + cell.size * Math.abs(frame.getPitchSin())
                    : cell.height;
            Location location = new Location(world, center.getX(), center.getY() - height / 2, center.getZ());

            if (cell.entity == null || !cell.entity.isValid()) {
                if (cell.entity != null) HitboxEntities.unregister(cell.entity);
                cell.entity = null;

                // Folia: entities can only be spawned in the current region, try again next tick
                if (!SchedulerUtil.isOwned(location)) continue;

                cell.entity = spawn(location, cell.size, height, ve);
                cell.lastLocation = location;
                cell.lastHeight = height;
                hideFromAll(state, cell.entity);
                continue;
            }

            if (Math.abs(height - cell.lastHeight) > 0.02) {
                cell.entity.setInteractionHeight((float) height);
                cell.lastHeight = height;
            }

            Location last = cell.lastLocation;
            if (last == null || !world.equals(last.getWorld()) || last.distanceSquared(location) > 1.0E-4) {
                SchedulerUtil.teleport(cell.entity, location);
                cell.lastLocation = location;
            }
        }

        if (state.ticks++ % VISIBILITY_INTERVAL == 0) {
            updateVisibility(state, frame);
        }
    }

    private static Interaction spawn(Location location, double size, double height, VehicleEntity owner) {
        Interaction entity = location.getWorld().spawn(location, Interaction.class);
        entity.setInteractionWidth((float) size);
        entity.setInteractionHeight((float) height);
        entity.setResponsive(true);
        entity.setPersistent(false);
        entity.setInvulnerable(true);
        entity.setSilent(true);
        entity.addScoreboardTag(HitboxEntities.TAG);
        HitboxEntities.register(entity, owner);
        return entity;
    }

    /**
     * Hides the hitbox from the players riding a vehicle nearby, and shows it again to the others.
     */
    private static void updateVisibility(State state, VehicleFrame frame) {
        Plugin plugin = QualityArmoryVehicles.getPlugin();
        Vector origin = frame.getOrigin();
        Set<UUID> shouldHide = new HashSet<>();

        for (Player player : frame.getWorld().getPlayers()) {
            if (!SchedulerUtil.isOwned(player)) {
                // Folia: player in another region, keep the current state
                if (state.hiddenFrom.contains(player.getUniqueId())) shouldHide.add(player.getUniqueId());
                continue;
            }

            Entity riding = player.getVehicle();
            if (riding == null || !QualityArmoryVehicles.isQAVEntity(riding)) continue;
            if (player.getLocation().toVector().distanceSquared(origin) > HIDE_DISTANCE * HIDE_DISTANCE) continue;
            shouldHide.add(player.getUniqueId());
        }

        for (UUID uuid : shouldHide) {
            if (!state.hiddenFrom.add(uuid)) continue;
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            for (Cell cell : state.cells) {
                if (cell.entity != null) player.hideEntity(plugin, cell.entity);
            }
        }

        for (UUID uuid : new ArrayList<>(state.hiddenFrom)) {
            if (shouldHide.contains(uuid)) continue;
            state.hiddenFrom.remove(uuid);
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !SchedulerUtil.isOwned(player)) continue;
            for (Cell cell : state.cells) {
                if (cell.entity != null) player.showEntity(plugin, cell.entity);
            }
        }
    }

    private static void hideFromAll(State state, Entity entity) {
        Plugin plugin = QualityArmoryVehicles.getPlugin();
        for (UUID uuid : state.hiddenFrom) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && SchedulerUtil.isOwned(player)) player.hideEntity(plugin, entity);
        }
    }

    static void remove(@NotNull VehicleEntity ve) {
        State state = STATES.remove(ve.getVehicleUUID());
        if (state != null) removeEntities(state);
    }

    static void forget(@NotNull VehicleEntity ve) {
        State state = STATES.remove(ve.getVehicleUUID());
        if (state == null) return;
        for (Cell cell : state.cells) {
            if (cell.entity != null) HitboxEntities.unregister(cell.entity);
        }
    }

    static void forgetPlayer(@NotNull UUID player) {
        for (State state : STATES.values()) {
            state.hiddenFrom.remove(player);
        }
    }

    private static void removeEntities(State state) {
        for (Cell cell : state.cells) {
            Interaction entity = cell.entity;
            if (entity == null) continue;
            cell.entity = null;
            HitboxEntities.unregister(entity);
            SchedulerUtil.runAtEntity(entity, entity::remove);
        }
    }

    /**
     * Splits every box into square-ish cells of about the given size.
     */
    private static List<Cell> buildCells(VehicleHitbox hitbox, double cellSize) {
        double size = Math.max(0.5, cellSize);
        while (countCells(hitbox, size) > MAX_CELLS && size < 16) {
            size *= 1.25;
        }

        List<Cell> cells = new ArrayList<>();
        for (HitboxBox box : hitbox.getBoxes()) {
            int nx = Math.max(1, (int) Math.ceil(box.getLength() / size - 1.0E-6));
            int nz = Math.max(1, (int) Math.ceil(box.getWidth() / size - 1.0E-6));
            double cellLength = box.getLength() / nx;
            double cellWidth = box.getWidth() / nz;
            double side = Math.max(cellLength, cellWidth);
            double centerY = box.getMinY() + box.getHeight() / 2;

            for (int i = 0; i < nx; i++) {
                for (int j = 0; j < nz; j++) {
                    Vector center = new Vector(box.getMinX() + cellLength * (i + 0.5), centerY, box.getMinZ() + cellWidth * (j + 0.5));
                    cells.add(new Cell(center, side, box.getHeight()));
                }
            }
        }
        return cells;
    }

    private static int countCells(VehicleHitbox hitbox, double size) {
        int count = 0;
        for (HitboxBox box : hitbox.getBoxes()) {
            count += Math.max(1, (int) Math.ceil(box.getLength() / size - 1.0E-6))
                    * Math.max(1, (int) Math.ceil(box.getWidth() / size - 1.0E-6));
        }
        return count;
    }
}
