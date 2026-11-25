package me.zombie_striker.qav.attachments;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.input.VehicleInput;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An extra model part spawned on its own armor stand that follows the vehicle (e.g. wheels or a tank turret).
 * Positions are relative to the vehicle model: x is forward, y is up and z is sideways.
 * <p>
 * Custom attachments can be added to the vehicle files by registering a type with
 * {@link AttachmentRegistry#register(String, AttachmentFactory)}, or to a vehicle type with
 * {@link me.zombie_striker.qav.vehicles.AbstractVehicle#addAttachment(Attachment)}.
 * The state of each vehicle is stored in {@link VehicleEntity#getAttachmentState(int)}.
 */
public abstract class Attachment {
    /**
     * Seat index of the driver, passenger seats use their index in the passagers list
     */
    public static final int DRIVER_SEAT = -1;
    /**
     * Seat index used by attachments that are not controlled by any seat
     */
    public static final int NO_SEAT = -2;

    private final String name;
    private final int id;
    private final Material material;
    private final Vector vector;
    private int seat = NO_SEAT;

    public Attachment(String name, int id, @Nullable Material material, @Nullable Vector vector) {
        this.name = name;
        this.id = id;
        this.material = material;
        this.vector = vector == null ? new Vector() : vector;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    /**
     * @return the material of the model, null if the armor stand has no model
     */
    public @Nullable Material getMaterial() {
        return material;
    }

    /**
     * @return the position of the attachment armor stand relative to the model
     */
    public Vector getVector() {
        return vector;
    }

    /**
     * @return the seat controlling this attachment, {@link #DRIVER_SEAT} or {@link #NO_SEAT}
     */
    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    /**
     * @return the model, null if the armor stand has no model
     */
    public @Nullable ItemStack build() {
        if (material == null) return null;
        return QualityArmoryVehicles.getAttachmentItemStack(this);
    }

    /**
     * @return false if no armor stand should be spawned for this attachment
     */
    public boolean isSpawnable() {
        return true;
    }

    /**
     * Called every tick before the armor stand is moved, to update the state of the attachment.
     */
    public void update(@NotNull VehicleEntity ve, int index) {
    }

    /**
     * @param ve            the vehicle
     * @param index         the index of this attachment in the vehicle type
     * @param modelLocation where the model armor stand is (or is being moved to)
     * @return where the armor stand of this attachment should be
     */
    public @NotNull Location getLocation(@NotNull VehicleEntity ve, int index, @NotNull Location modelLocation) {
        return modelLocation.clone().add(QualityArmoryVehicles.rotateRelToCar(ve, ve.getModelEntity(), vector, false));
    }

    /**
     * Called every tick after the armor stand has been moved.
     */
    public abstract void animate(@NotNull VehicleEntity ve, int index, @NotNull ArmorStand entity);

    /**
     * Called when the player sitting in the seat controlling this attachment presses a key.
     *
     * @return true if the input was used by the attachment
     */
    public boolean onInput(@NotNull VehicleEntity ve, int index, @NotNull Player player, @NotNull VehicleInput input) {
        return false;
    }

    /**
     * Armor stands rotate their head around the neck, but the item on the head is drawn higher, so a rotated
     * model moves around the neck instead of rotating on itself unless the armor stand is moved to compensate.
     *
     * @return the offset from the neck to the center of the head item, in blocks
     */
    public static @NotNull Vector getHeadItemCenter(@NotNull VehicleEntity ve) {
        return new Vector(0, ve.getModelEntity().isSmall() ? 0.125 : 0.25, 0);
    }

    /**
     * Rotates a position relative to the model by the given angle, like the vehicle rotates its seats.
     */
    public static @NotNull Vector rotate(@NotNull Vector offset, double angle) {
        double cos = Math.cos(angle - Main.YOFFSET);
        double sin = Math.sin(angle - Main.YOFFSET);
        return new Vector((offset.getX() * cos) - (offset.getZ() * sin), offset.getY(),
                (offset.getZ() * cos) + (offset.getX() * sin));
    }
}
