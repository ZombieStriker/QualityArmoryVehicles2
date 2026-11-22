package me.zombie_striker.qav.hitbox;

/**
 * Seat ids used when choosing where a player enters a vehicle.
 */
public final class SeatSelection {

    /** No seat was clicked: use the default order (driver first, then the first free passenger seat) */
    public static final int NONE = -2;
    /** The driver seat */
    public static final int DRIVER = -1;

    private SeatSelection() {
    }
}
