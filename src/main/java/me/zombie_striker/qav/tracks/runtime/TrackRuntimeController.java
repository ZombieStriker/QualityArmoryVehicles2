package me.zombie_striker.qav.tracks.runtime;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.tracks.data.Track;
import me.zombie_striker.qav.tracks.data.TrackStop;
import me.zombie_striker.qav.vehicles.AbstractTrain;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.*;

public class TrackRuntimeController {

    private final @NotNull Main plugin;
    private final @NotNull Map<String, List<RuntimeTrain>> runtime = new HashMap<>();
    private @Nullable BukkitRunnable task;

    public TrackRuntimeController(@NotNull Main plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        task.runTaskTimer(plugin, 1, 1);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        runtime.clear();
    }

    private void tick() {
        for (Track track : Main.tracksManager.getTracks()) {
            if (!Main.tracksManager.isRunning(track.getId())) continue;

            String key = track.getId().toLowerCase(Locale.ROOT);

            World w = Bukkit.getWorld(track.getWorldName());
            if (w == null) {
                destroyRuntimeTrains(runtime.remove(key), "World not loaded");
                continue;
            }

            List<TrackStop> sorted = sortedStops(track);
            if (sorted.isEmpty() || track.getTrains().isEmpty()) {
                destroyRuntimeTrains(runtime.remove(key), "Track missing stops/trains");
                continue;
            }

            List<RuntimeTrain> trains = runtime.get(key);
            if (needsRebuild(track, trains, sorted.size())) {
                destroyRuntimeTrains(trains, "Track runtime rebuild");
                trains = buildRuntimeTrains(track, sorted);
                runtime.put(key, trains);
            }

            int n = sorted.size();
            for (RuntimeTrain rt : trains) {
                if (rt.targetStopOrdinal >= n) rt.targetStopOrdinal = rt.targetStopOrdinal % n;

                if (rt.vehicle == null || rt.vehicle.isInvalid()) {
                    rt.vehicle = spawnTrainForTrack(track, sorted, rt.vehicleTypeName, rt.spawnStopOrdinal);
                    if (rt.vehicle == null) continue;
                }

                VehicleEntity ve = rt.vehicle;
                if (ve.getDriverSeat() == null) {
                    // Vehicle is no longer valid for runtime control; it will be respawned next tick.
                    rt.vehicle = null;
                    continue;
                }

                TrackStop targetStop = sorted.get(rt.targetStopOrdinal);
                Location driverLoc = ve.getDriverSeat().getLocation();

                if (rt.dwellTicks > 0) {
                    ve.setSpeed(0);
                    if (!rt.inDwell) {
                        rt.inDwell = true;
                        playStopSound(driverLoc, true);
                    }
                    rt.dwellTicks--;
                    continue;
                }

                if (rt.inDwell) {
                    rt.inDwell = false;
                    playStopSound(driverLoc, false);
                    advanceToNextStop(track, rt, n);
                    continue;
                }

                if (rt.finished) {
                    ve.setSpeed(0);
                    continue;
                }

                double desired = Math.min(0.35, ve.getType().getMaxSpeed());
                ve.setSpeed(desired);

                if (rt.arrivalCooldownTicks > 0) {
                    rt.arrivalCooldownTicks--;
                } else if (targetStop.isSameRailBlock(driverLoc)) {
                    int dwell = Math.max(0, targetStop.getDwellSeconds());
                    showStopTitles(track, rt, sorted, ve);
                    if (dwell == 0) {
                        advanceToNextStop(track, rt, n);
                    } else {
                        rt.dwellTicks = dwell * 20;
                        ve.setSpeed(0);
                    }
                }
            }
        }

        cleanupStoppedTracks();
    }

    private void cleanupStoppedTracks() {
        Iterator<Map.Entry<String, List<RuntimeTrain>>> it = runtime.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<RuntimeTrain>> entry = it.next();
            String trackIdLower = entry.getKey();
            if (Main.tracksManager.isRunning(trackIdLower)) continue;

            destroyRuntimeTrains(entry.getValue(), "Track stopped");
            it.remove();
        }
    }

    private void destroyRuntimeTrains(@Nullable List<RuntimeTrain> trains, @NotNull String reason) {
        if (trains == null || trains.isEmpty()) return;
        for (RuntimeTrain rt : trains) {
            VehicleEntity ve = rt.vehicle;
            if (ve == null || ve.isInvalid()) continue;
            try {
                ve.deconstruct(null, "Tram runtime: " + reason);
            } catch (Throwable t) {
                plugin.getLogger().log(Level.SEVERE, "Failed to deconstruct tram runtime vehicle (" + reason + ")", t);
            }
        }
    }

    private void advanceToNextStop(@NotNull Track track, @NotNull RuntimeTrain rt, int n) {
        if (track.isLooping()) {
            rt.targetStopOrdinal = (rt.targetStopOrdinal + 1) % n;
        } else {
            rt.targetStopOrdinal++;
            if (rt.targetStopOrdinal >= n) {
                rt.targetStopOrdinal = n - 1;
                rt.finished = true;
            }
        }
        rt.arrivalCooldownTicks = 4;
    }

    private boolean needsRebuild(@NotNull Track track, @Nullable List<RuntimeTrain> trains, int stopCount) {
        if (trains == null) return true;

        List<String> assignedTrainTypes = track.getTrains();
        if (trains.size() != assignedTrainTypes.size()) return true;
        if (trains.isEmpty()) return true;

        for (int i = 0; i < trains.size(); i++) {
            RuntimeTrain rt = trains.get(i);
            if (rt.builtForStopCount != stopCount) return true;
            if (!rt.vehicleTypeName.equalsIgnoreCase(assignedTrainTypes.get(i))) return true;
        }

        return false;
    }

    private @NotNull List<TrackStop> sortedStops(@NotNull Track track) {
        List<TrackStop> sorted = new ArrayList<>(track.getStops());
        sorted.sort(Comparator.comparingInt(TrackStop::getOrder));
        return sorted;
    }

    private @NotNull List<RuntimeTrain> buildRuntimeTrains(@NotNull Track track, @NotNull List<TrackStop> sorted) {
        List<RuntimeTrain> out = new ArrayList<>();
        int n = sorted.size();
        List<String> assigned = new ArrayList<>(track.getTrains());
        for (int i = 0; i < assigned.size(); i++) {
            int spawnIdx = i % n;
            int targetIdx = n == 1 ? 0 : (spawnIdx + 1) % n;
            out.add(new RuntimeTrain(assigned.get(i), spawnIdx, targetIdx, n));
        }
        return out;
    }

    private @Nullable VehicleEntity spawnTrainForTrack(@NotNull Track track, @NotNull List<TrackStop> sorted,
                                                       @NotNull String vehicleTypeName, int spawnStopIndex) {
        AbstractVehicle v = QualityArmoryVehicles.getVehicle(vehicleTypeName);
        if (!(v instanceof AbstractTrain)) return null;

        World w = Bukkit.getWorld(track.getWorldName());
        if (w == null) return null;

        TrackStop spawn = sorted.get(spawnStopIndex % sorted.size());
        Location base = spawn.getRailBlockLocation(w).clone().add(0, -1, 0);
        return QualityArmoryVehicles.spawnVehicleSystem(v, base);
    }

    private void playStopSound(@NotNull Location loc, boolean arrival) {
        try {
            Sound s = arrival ? Sound.BLOCK_NOTE_BLOCK_BELL : Sound.BLOCK_NOTE_BLOCK_CHIME;
            loc.getWorld().playSound(loc, s, 0.9f, arrival ? 0.8f : 1.1f);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void showStopTitles(@NotNull Track track,
                                @NotNull RuntimeTrain rt,
                                @NotNull List<TrackStop> sorted,
                                @NotNull VehicleEntity ve) {
        int n = sorted.size();
        if (n == 0) return;

        TrackStop currentStop = sorted.get(Math.max(0, Math.min(rt.targetStopOrdinal, n - 1)));

        TrackStop nextStop = null;
        if (track.isLooping()) {
            int nextIdx = (rt.targetStopOrdinal + 1) % n;
            nextStop = sorted.get(nextIdx);
        } else {
            int nextIdx = rt.targetStopOrdinal + 1;
            if (nextIdx < n) {
                nextStop = sorted.get(nextIdx);
            }
        }

        String title = MessagesConfig.colorize(
                MessagesConfig.MESSAGE_TRAM_TITLE_CURRENT_STOP.replace("%current%", currentStop.getName()));
        String subtitleRaw;
        if (nextStop != null) {
            subtitleRaw = MessagesConfig.MESSAGE_TRAM_TITLE_NEXT_STOP.replace("%next%", nextStop.getName());
        } else {
            subtitleRaw = MessagesConfig.MESSAGE_TRAM_TITLE_LAST_STOP;
        }
        String subtitle = MessagesConfig.colorize(subtitleRaw);

        sendTitleToVehicle(ve, title, subtitle);
    }

    private void sendTitleToVehicle(@NotNull VehicleEntity ve,
                                    @NotNull String title,
                                    @NotNull String subtitle) {
        Entity driverSeat = ve.getDriverSeat();
        if (driverSeat != null) {
            if (driverSeat instanceof Player) {
                ((Player) driverSeat).sendTitle(title, subtitle, 10, 60, 10);
            } else if (driverSeat.getPassenger() instanceof Player) {
                Player p = (Player) driverSeat.getPassenger();
                p.sendTitle(title, subtitle, 10, 60, 10);
            }
        }

        for (Entity seat : ve.getPassagerSeats()) {
            if (seat == null) continue;
            if (seat instanceof Player) {
                ((Player) seat).sendTitle(title, subtitle, 10, 60, 10);
            } else if (seat.getPassenger() instanceof Player) {
                Player p = (Player) seat.getPassenger();
                p.sendTitle(title, subtitle, 10, 60, 10);
            }
        }
    }

    private static class RuntimeTrain {
        private final @NotNull String vehicleTypeName;
        private final int spawnStopOrdinal;
        private int targetStopOrdinal;
        private final int builtForStopCount;
        private int dwellTicks = 0;
        private boolean inDwell = false;
        private boolean finished = false;
        private int arrivalCooldownTicks = 0;
        private @Nullable VehicleEntity vehicle;

        private RuntimeTrain(@NotNull String vehicleTypeName, int spawnStopOrdinal,
                             int targetStopOrdinal, int builtForStopCount) {
            this.vehicleTypeName = vehicleTypeName;
            this.spawnStopOrdinal = spawnStopOrdinal;
            this.targetStopOrdinal = targetStopOrdinal;
            this.builtForStopCount = builtForStopCount;
        }
    }
}
