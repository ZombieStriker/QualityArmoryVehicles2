package me.zombie_striker.qav.tracks.assign;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.tracks.data.Track;
import me.zombie_striker.qav.tracks.data.TrackStop;
import me.zombie_striker.qav.tracks.data.TrackTrainAssignment;
import me.zombie_striker.qav.util.HeadPoseUtil;
import me.zombie_striker.qav.vehicles.AbstractTrain;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class TrainAssignController {

    private static final class Session {
        final @NotNull String trackId;
        final @NotNull String vehicleTypeName;
        final int spawnDelaySeconds;
        final @NotNull VehicleEntity preview;
        int direction;
        long lastRotateMs;

        Session(@NotNull String trackId, @NotNull String vehicleTypeName, int spawnDelaySeconds,
                @NotNull VehicleEntity preview, int direction) {
            this.trackId = trackId;
            this.vehicleTypeName = vehicleTypeName;
            this.spawnDelaySeconds = spawnDelaySeconds;
            this.preview = preview;
            this.direction = direction;
        }
    }

    private final @NotNull Main plugin;
    private final @NotNull Map<UUID, Session> sessions = new HashMap<>();

    public TrainAssignController(@NotNull Main plugin) {
        this.plugin = plugin;
    }

    public void begin(@NotNull Player player, @NotNull Track track, @NotNull AbstractVehicle trainType, int spawnDelaySeconds) {
        if (!(trainType instanceof AbstractTrain)) {
            return;
        }

        List<TrackStop> sorted = new ArrayList<>(track.getStops());
        sorted.sort(Comparator.comparingInt(TrackStop::getOrder));
        if (sorted.isEmpty()) {
            player.sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_ASSIGN_NEED_STOPS);
            return;
        }

        World w = Bukkit.getWorld(track.getWorldName());
        if (w == null) {
            player.sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_TRACK_NOT_FOUND.replace("%track%", track.getId()));
            return;
        }

        discardSessionQuietly(player);

        TrackStop spawnStop = sorted.get(0);
        Location base = spawnStop.getRailBlockLocation(w).clone().add(0, -1, 0);

        VehicleEntity preview = QualityArmoryVehicles.spawnVehicle(trainType, base, player);
        if (preview == null) {
            player.sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_ASSIGN_SPAWN_FAILED);
            return;
        }

        preview.setSpeed(0);
        preview.setDisplayOnly(true);

        int direction = 0;
        applyDirection(preview, direction);

        sessions.put(player.getUniqueId(), new Session(track.getId(), trainType.getName(), spawnDelaySeconds, preview, direction));

        player.sendMessage(Main.prefix + MessagesConfig.colorize(MessagesConfig.MESSAGE_TRAM_ASSIGN_PREVIEW_HINT));
    }

    private static void applyDirection(@NotNull VehicleEntity ve, int direction) {
        int d = direction % 4;
        if (d < 0) {
            d += 4;
        }
        ve.setSpeed(0);
        ve.setAngle(AbstractTrain.getAngleFromDirection(d));
        HeadPoseUtil.setHeadPoseUsingReflection(ve);
    }

    public boolean hasSession(@NotNull Player player) {
        return sessions.containsKey(player.getUniqueId());
    }

    public boolean matchesPendingSession(@NotNull Player player, @NotNull String trackId,
                                         @NotNull String vehicleTypeName, int spawnDelaySeconds) {
        Session s = sessions.get(player.getUniqueId());
        if (s == null) return false;

        return s.trackId.equalsIgnoreCase(trackId)
                && s.vehicleTypeName.equalsIgnoreCase(vehicleTypeName)
                && s.spawnDelaySeconds == spawnDelaySeconds;
    }

    public boolean handlePreviewInteract(@NotNull Player player) {
        Session s = sessions.get(player.getUniqueId());
        if (s == null) return false;

        long now = System.currentTimeMillis();
        if (now - s.lastRotateMs < 200L) return true;

        s.lastRotateMs = now;
        s.direction = (s.direction + 1) % 4;
        applyDirection(s.preview, s.direction);
        player.sendMessage(Main.prefix + MessagesConfig.colorize(
                MessagesConfig.MESSAGE_TRAM_ASSIGN_ROTATED.replace("%dir%", directionLabel(s.direction))));
        return true;
    }

    private static @NotNull String directionLabel(int direction) {
        int d = direction % 4;
        if (d < 0) d += 4;

        switch (d) {
            case 0:
                return MessagesConfig.MESSAGE_TRAM_DIR_SOUTH;
            case 1:
                return MessagesConfig.MESSAGE_TRAM_DIR_EAST;
            case 2:
                return MessagesConfig.MESSAGE_TRAM_DIR_NORTH;
            case 3:
                return MessagesConfig.MESSAGE_TRAM_DIR_WEST;
            default:
                return String.valueOf(d);
        }
    }

    public void confirm(@NotNull Player player) {
        Session s = sessions.remove(player.getUniqueId());
        if (s == null) return;

        Track track = Main.tracksManager.getTrack(s.trackId);
        if (track == null) {
            cleanupPreview(s);
            player.sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_TRACK_NOT_FOUND_GENERIC);
            return;
        }

        int savedDirection = s.direction;
        cleanupPreview(s);

        List<TrackTrainAssignment> assignments = new ArrayList<>(track.getTrainAssignments());
        assignments.add(new TrackTrainAssignment(s.vehicleTypeName, s.spawnDelaySeconds, savedDirection));
        track.setTrainAssignments(assignments);

        try {
            Main.tracksManager.saveAll();
        } catch (IOException e) {
            player.sendMessage(Main.prefix + MessagesConfig.MESSAGE_TRAM_SAVE_FAILED);
            plugin.getLogger().log(Level.SEVERE, "Failed to save tram tracks", e);
            return;
        }

        player.sendMessage(Main.prefix + MessagesConfig.colorize(
                MessagesConfig.MESSAGE_TRAM_ASSIGN_CONFIRMED
                        .replace("%train%", s.vehicleTypeName)
                        .replace("%track%", track.getId())
                        .replace("%delay%", String.valueOf(s.spawnDelaySeconds))
                        .replace("%dir%", directionLabel(savedDirection))));
    }

    public void discardSessionQuietly(@NotNull Player player) {
        Session s = sessions.remove(player.getUniqueId());
        if (s != null) {
            cleanupPreview(s);
        }
    }

    public void shutdown() {
        for (Session s : new ArrayList<>(sessions.values())) cleanupPreview(s);

        sessions.clear();
    }

    private static void cleanupPreview(@NotNull Session s) {
        if (!s.preview.isInvalid()) {
            s.preview.setDisplayOnly(false);
            s.preview.deconstruct(null, "Tram train assign preview ended", false);
        }
    }
}
