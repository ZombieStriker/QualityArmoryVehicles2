package me.zombie_striker.qav.tracks.data;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class TrackStop {
    private final @NotNull String id;
    private @NotNull String name;
    private int order;
    private int blockX;
    private int blockY;
    private int blockZ;
    private int dwellSeconds;

    public TrackStop(@NotNull String id, @NotNull String name, int order, int blockX, int blockY, int blockZ, int dwellSeconds) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.dwellSeconds = dwellSeconds;
    }

    public @NotNull String getId() {
        return id;
    }

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getBlockX() {
        return blockX;
    }

    public int getBlockY() {
        return blockY;
    }

    public int getBlockZ() {
        return blockZ;
    }

    public void setBlock(int blockX, int blockY, int blockZ) {
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
    }

    public int getDwellSeconds() {
        return dwellSeconds;
    }

    public void setDwellSeconds(int dwellSeconds) {
        this.dwellSeconds = dwellSeconds;
    }

    public @NotNull Location getRailBlockLocation(@NotNull World world) {
        return new Location(world, blockX, blockY, blockZ);
    }

    public boolean isSameRailBlock(@NotNull Location loc) {
        return loc.getBlockX() == blockX
                && loc.getBlockZ() == blockZ
                && Math.abs(loc.getBlockY() - blockY) <= 1;
    }
}
