package me.zombie_striker.qav.hooks.model;

import java.util.Arrays;

public class Animation {
    private final AnimationType type;
    private final String id;
    private final String[] args;

    public Animation(AnimationType type, String id, String... args) {
        this.type = type;
        this.id = id;
        this.args = args;
    }

    public AnimationType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "Animation{" +
                "type=" + type +
                ", id='" + id + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }

    public enum AnimationType {
        SPAWN,
        DESPAWN,
        ENTER,
        BREAK;

        public static AnimationType getType(final String s) {
            for (final AnimationType type : values()) {
                if (type.name().equalsIgnoreCase(s)) {
                    return type;
                }
            }
            return null;
        }
    }
}
