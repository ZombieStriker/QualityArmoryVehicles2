package me.zombie_striker.qav.hooks.implementation;


import me.zombie_striker.qav.hooks.ProtectionHook;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
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
