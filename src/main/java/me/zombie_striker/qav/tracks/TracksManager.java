package me.zombie_striker.qav.tracks;

import me.zombie_striker.qav.tracks.data.Track;
import me.zombie_striker.qav.tracks.data.TrackStop;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

public class TracksManager {

    private final @NotNull TracksStorage storage;
    private final @NotNull Map<String, Track> tracks = new HashMap<>();

    public TracksManager(@NotNull TracksStorage storage) {
        this.storage = storage;
    }

    public void loadAll() {
        tracks.clear();
        tracks.putAll(storage.loadAllTracks());
    }

    public void saveAll() throws IOException {
        storage.writeAllTracks(tracks.values());
    }

    public @NotNull Collection<Track> getTracks() {
        return Collections.unmodifiableCollection(tracks.values());
    }

    public @Nullable Track getTrack(@NotNull String id) {
        return tracks.get(id.toLowerCase(Locale.ROOT));
    }

    public @NotNull Track createTrack(@NotNull String id, @NotNull String worldName) {
        if (id.contains(".")) throw new IllegalArgumentException("Track id may not contain '.' characters: " + id);

        Track track = new Track(id, worldName);
        tracks.put(id.toLowerCase(Locale.ROOT), track);
        return track;
    }

    public boolean deleteTrack(@NotNull String id) {
        return tracks.remove(id.toLowerCase(Locale.ROOT)) != null;
    }

    public boolean isRunning(@NotNull String trackId) {
        Track t = tracks.get(trackId.toLowerCase(Locale.ROOT));
        return t != null && t.isRunning();
    }

    public void setRunning(@NotNull String trackId, boolean running) {
        Track t = tracks.get(trackId.toLowerCase(Locale.ROOT));
        if (t != null) t.setRunning(running);
    }

    public @NotNull TrackStop addStop(@NotNull Track track, @NotNull String name, @NotNull Location railBlock, int dwellSeconds) {
        if (railBlock.getWorld() != null) {
            track.setWorldName(railBlock.getWorld().getName());
        }
        int nextOrder = track.getStops().stream().mapToInt(TrackStop::getOrder).max().orElse(-1) + 1;
        String stopId = "stop_" + System.currentTimeMillis();
        TrackStop stop = new TrackStop(stopId, name, nextOrder,
                railBlock.getBlockX(), railBlock.getBlockY(), railBlock.getBlockZ(), dwellSeconds);
        track.addStop(stop);
        return stop;
    }

    public boolean removeStop(@NotNull Track track, @NotNull String stopId) {
        TrackStop existing = track.getStopById(stopId);
        if (existing == null) return false;
        track.removeStop(stopId);
        return true;
    }
}
