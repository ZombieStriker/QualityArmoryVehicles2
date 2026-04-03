package me.zombie_striker.qav.tracks;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.tracks.data.Track;
import me.zombie_striker.qav.tracks.data.TrackStop;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class TracksStorage {

    private final @NotNull File file;
    private @NotNull FileConfiguration config;

    public TracksStorage() {
        File dataFolder = QualityArmoryVehicles.getPlugin().getDataFolder();
        this.file = new File(dataFolder, "tram-tracks.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public synchronized void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public synchronized void save() throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        File tempFile = new File(file.getParentFile(), file.getName() + ".tmp");
        config.save(tempFile);

        // Rename may fail on some platforms when the destination already exists; use Files.move instead.
        try {
            Files.move(
                    tempFile.toPath(),
                    file.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public synchronized @NotNull Map<String, Track> loadAllTracks() {
        reload();
        Map<String, Track> tracks = new HashMap<>();

        ConfigurationSection root = config.getConfigurationSection("tracks");
        if (root == null) return tracks;

        for (String trackId : root.getKeys(false)) {
            if (trackId.contains(".")) {
                QualityArmoryVehicles.getPlugin().getLogger().warning("[Tracks] Skipping track id '" + trackId + "' because it contains an invalid character ('.').");
                continue;
            }
            ConfigurationSection t = root.getConfigurationSection(trackId);
            if (t == null) continue;

            String worldName = t.getString("world", "");
            if (worldName.isEmpty()) continue;

            Track track = new Track(trackId, worldName);
            track.setLooping(t.getBoolean("looping", true));

            List<TrackStop> stops = new ArrayList<>();
            ConfigurationSection stopsSection = t.getConfigurationSection("stops");
            if (stopsSection != null) {
                List<String> stopIds = new ArrayList<>(stopsSection.getKeys(false));
                Collections.sort(stopIds);
                for (String stopId : stopIds) {
                    ConfigurationSection s = stopsSection.getConfigurationSection(stopId);
                    if (s == null) continue;
                    String name = s.getString("name", stopId);
                    int dwell = s.getInt("dwellSeconds", 5);
                    if (dwell < 0) {
                        dwell = 0;
                    } else if (dwell > 3600) {
                        dwell = 3600;
                    }
                    if (!s.isSet("x") || !s.isSet("y") || !s.isSet("z")) continue;
                    int x = s.getInt("x");
                    int y = s.getInt("y");
                    int z = s.getInt("z");
                    int order = s.getInt("order", stops.size());
                    stops.add(new TrackStop(stopId, name, order, x, y, z, dwell));
                }
            }
            track.setStops(stops);
            track.setTrains(t.getStringList("trains"));

            tracks.put(trackId.toLowerCase(Locale.ROOT), track);
        }
        return tracks;
    }

    public synchronized void writeAllTracks(@NotNull Collection<Track> tracks) throws IOException {
        config.set("tracks", null);

        for (Track track : tracks) {
            String base = "tracks." + track.getId();
            config.set(base + ".world", track.getWorldName());
            config.set(base + ".looping", track.isLooping());

            config.set(base + ".stops", null);
            for (TrackStop stop : track.getStops()) {
                String sbase = base + ".stops." + stop.getId();
                config.set(sbase + ".name", stop.getName());
                config.set(sbase + ".order", stop.getOrder());
                config.set(sbase + ".x", stop.getBlockX());
                config.set(sbase + ".y", stop.getBlockY());
                config.set(sbase + ".z", stop.getBlockZ());
                config.set(sbase + ".dwellSeconds", stop.getDwellSeconds());
            }

            config.set(base + ".trains", track.getTrains());
        }

        save();
    }
}
