package me.zombie_striker.qav.tracks.data;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Track {
    private final @NotNull String id;
    private @NotNull String worldName;
    private boolean looping = true;

    private final @NotNull List<TrackStop> stops = new ArrayList<>();
    private final @NotNull List<String> trains = new ArrayList<>();

    public Track(@NotNull String id, @NotNull String worldName) {
        this.id = id;
        this.worldName = worldName;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull String getWorldName() {
        return worldName;
    }

    public void setWorldName(@NotNull String worldName) {
        this.worldName = worldName;
    }

    public @Nullable World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public @NotNull List<TrackStop> getStops() {
        return Collections.unmodifiableList(stops);
    }

    public void setStops(@NotNull List<TrackStop> newStops) {
        this.stops.clear();
        this.stops.addAll(newStops);
        this.stops.sort(Comparator.comparingInt(TrackStop::getOrder));
    }

    public @NotNull List<String> getTrains() {
        return Collections.unmodifiableList(trains);
    }

    public void setTrains(@NotNull List<String> newTrains) {
        this.trains.clear();
        this.trains.addAll(newTrains);
    }

    public void addStop(@NotNull TrackStop stop) {
        stops.add(stop);
        stops.sort(Comparator.comparingInt(TrackStop::getOrder));
    }

    public void removeStop(@NotNull String stopId) {
        stops.removeIf(s -> s.getId().equalsIgnoreCase(stopId));
    }

    public @Nullable TrackStop getStopById(@NotNull String stopId) {
        for (TrackStop s : stops) {
            if (s.getId().equalsIgnoreCase(stopId)) return s;
        }
        return null;
    }
}
