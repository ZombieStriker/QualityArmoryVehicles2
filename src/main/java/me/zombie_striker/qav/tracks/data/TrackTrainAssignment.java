package me.zombie_striker.qav.tracks.data;

import org.jetbrains.annotations.NotNull;

public class TrackTrainAssignment {

    public static final int MAX_SPAWN_DELAY_SECONDS = 86400;

    private final @NotNull String vehicleTypeName;
    private final int spawnDelaySeconds;
    private final int spawnDirection;

    public TrackTrainAssignment(@NotNull String vehicleTypeName, int spawnDelaySeconds) {
        this(vehicleTypeName, spawnDelaySeconds, 0);
    }

    public TrackTrainAssignment(@NotNull String vehicleTypeName, int spawnDelaySeconds, int spawnDirection) {
        this.vehicleTypeName = vehicleTypeName;
        this.spawnDelaySeconds = Math.max(0, Math.min(MAX_SPAWN_DELAY_SECONDS, spawnDelaySeconds));
        int d = spawnDirection % 4;
        if (d < 0) d += 4;

        this.spawnDirection = d;
    }

    public @NotNull String getVehicleTypeName() {
        return vehicleTypeName;
    }

    public int getSpawnDelaySeconds() {
        return spawnDelaySeconds;
    }

    public int getSpawnDirection() {
        return spawnDirection;
    }
}
