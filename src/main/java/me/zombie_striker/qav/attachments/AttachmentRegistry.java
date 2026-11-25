package me.zombie_striker.qav.attachments;

import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.attachments.impl.TurretAttachment;
import me.zombie_striker.qav.attachments.impl.WheelAttachment;
import me.zombie_striker.qav.config.VehicleLoader;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The attachment types that can be used in the {@code attachments} section of the vehicle files.
 */
public final class AttachmentRegistry {
	private static final Map<String, AttachmentFactory> TYPES = new ConcurrentHashMap<>();

	static {
		TYPES.put("wheel", WheelAttachment::create);
		TYPES.put("turret", TurretAttachment::create);
	}

	private AttachmentRegistry() {
	}

	/**
	 * Registers an attachment type. Vehicles already loaded that use it are updated, so it can be registered at any time.
	 *
	 * @param type    the value of the {@code type} option, case-insensitive
	 * @param factory creates the attachment from its section
	 * @throws IllegalArgumentException if the type is already registered
	 */
	public static void register(@NotNull String type, @NotNull AttachmentFactory factory) {
		String key = type.toLowerCase(Locale.ROOT);
		if (TYPES.putIfAbsent(key, factory) != null)
			throw new IllegalArgumentException("Attachment type " + type + " is already registered");

		for (AbstractVehicle vehicle : Main.vehicleTypes) {
			List<Attachment> attachments = vehicle.getAttachments();
			for (int i = 0; i < attachments.size(); i++) {
				Attachment attachment = attachments.get(i);
				if (!(attachment instanceof PendingAttachment) || !((PendingAttachment) attachment).getType().equalsIgnoreCase(type))
					continue;

				try {
					Attachment loaded = VehicleLoader.loadAttachment(((PendingAttachment) attachment).getConfig());
					attachments.set(i, loaded);
					VehicleLoader.registerItem(loaded.getMaterial(), loaded.getId());
				} catch (IllegalArgumentException e) {
					QualityArmoryVehicles.getPlugin().getLogger().warning("Invalid attachment \"" + attachment.getName()
							+ "\" for vehicle \"" + vehicle.getName() + "\": " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Unregisters an attachment type, vehicles already loaded keep using it until they are reloaded.
	 */
	public static void unregister(@NotNull String type) {
		TYPES.remove(type.toLowerCase(Locale.ROOT));
	}

	public static @Nullable AttachmentFactory get(@Nullable String type) {
		return type == null ? null : TYPES.get(type.toLowerCase(Locale.ROOT));
	}

	/**
	 * @return the registered types, lower case
	 */
	public static @NotNull Map<String, AttachmentFactory> getTypes() {
		return Collections.unmodifiableMap(TYPES);
	}
}
