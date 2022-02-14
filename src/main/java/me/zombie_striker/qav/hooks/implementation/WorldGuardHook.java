package me.zombie_striker.qav.hooks.implementation;


import me.zombie_striker.qav.hooks.ProtectionHook;
import me.zombie_striker.qg.hooks.protection.worldguard.WorldGuardWrapper;
import me.zombie_striker.qg.hooks.protection.worldguard.flag.IWrappedFlag;
import me.zombie_striker.qg.hooks.protection.worldguard.flag.WrappedState;
import me.zombie_striker.qg.hooks.protection.worldguard.region.IWrappedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WorldGuardHook implements ProtectionHook {
    private final WorldGuardWrapper worldGuard;
    private final IWrappedFlag<WrappedState> blockBreak;

    public WorldGuardHook() {
        worldGuard = WorldGuardWrapper.getInstance();
        blockBreak = worldGuard.getFlag("BLOCK-BREAK", WrappedState.class).orElse(createFlag("BLOCK-BREAK"));
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        for (IWrappedRegion k : worldGuard.getRegions(location)) {
            WrappedState wrappedState = k.getFlag(blockBreak).orElse(WrappedState.ALLOW);
            if (wrappedState.equals(WrappedState.DENY)) return false;
        }

        return true;
    }

    @Contract(value = "_ -> new", pure = true)
    private @NotNull IWrappedFlag<WrappedState> createFlag(String name) {
        return new IWrappedFlag<WrappedState>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Optional<WrappedState> getDefaultValue() {
                return Optional.of(WrappedState.ALLOW);
            }
        };
    }
}
