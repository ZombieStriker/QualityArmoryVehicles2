package me.zombie_striker.qav.hooks.implementation;

import me.zombie_striker.qav.hooks.ProtectionHook;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

public class WorldGuardHook implements ProtectionHook {
    private final WorldGuardWrapper worldGuard;
    private final IWrappedFlag<WrappedState> breakFlag;

    public WorldGuardHook() {
        worldGuard = WorldGuardWrapper.getInstance();
        breakFlag = worldGuard.getFlag("block-break", WrappedState.class).orElse(null);
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        IWrappedRegion top = null;
        int topPriority = Integer.MIN_VALUE;

        for (IWrappedRegion region : worldGuard.getRegions(location)) {
            if (region.getPriority() > topPriority) {
                top = region;
                topPriority = region.getPriority();
            }
        }

        if (top == null) return true;

        // todo: fix in base of player domain
        return breakFlag == null || !top.getFlag(breakFlag).isPresent() || !top.getFlag(breakFlag).get().equals(WrappedState.DENY);
    }

}
