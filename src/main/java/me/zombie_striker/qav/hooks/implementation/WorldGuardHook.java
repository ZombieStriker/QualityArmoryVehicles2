package me.zombie_striker.qav.hooks.implementation;

import me.zombie_striker.qav.hooks.ProtectionHook;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;

import java.util.Optional;

public class WorldGuardHook implements ProtectionHook {
    private final WorldGuardWrapper worldGuard;
    private final IWrappedFlag<WrappedState> canMove;
    private final IWrappedFlag<WrappedState> canPlace;
    private final IWrappedFlag<WrappedState> canRemove;

    public static void register() {
        WorldGuardWrapper.getInstance().registerFlag("qav-use", WrappedState.class, WrappedState.ALLOW);
    }

    public WorldGuardHook() {
        worldGuard = WorldGuardWrapper.getInstance();
        canMove = worldGuard.getFlag("qav-use", WrappedState.class).orElse(null);
        canPlace = worldGuard.getFlag("vehicle-place", WrappedState.class).orElse(null);
        canRemove = worldGuard.getFlag("vehicle-remove", WrappedState.class).orElse(null);
    }

    @Override
    public boolean canMove(Player player, Location location) {
        return checkFlag(player, location, canMove);
    }

    @Override
    public boolean canPlace(Player player, Location location) {
        return checkFlag(player, location, canPlace);
    }

    @Override
    public boolean canRemove(Player player, Location location) {
        return checkFlag(player, location, canRemove);
    }

    private boolean checkFlag(Player player, Location location, IWrappedFlag<WrappedState> flag) {
        if (flag == null) return true;

        Optional<WrappedState> wrappedState = worldGuard.queryFlag(player, location, flag);
        return wrappedState.orElse(WrappedState.ALLOW).equals(WrappedState.ALLOW);
    }
}
