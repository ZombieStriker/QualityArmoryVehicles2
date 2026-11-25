package me.zombie_striker.qav.input;

import org.jetbrains.annotations.Nullable;

public enum VehicleInput {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    SPACE,
    SHIFT,
    LMB,
    RMB,
    F;

    public static @Nullable VehicleInput getByName(@Nullable String name) {
        if (name == null) return null;
        for (VehicleInput input : values()) {
            if (input.name().equalsIgnoreCase(name))
                return input;
        }
        return null;
    }
}
