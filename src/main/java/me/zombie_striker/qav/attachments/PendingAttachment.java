package me.zombie_striker.qav.attachments;

import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.NotNull;

/**
 * Placeholder for an attachment whose type is not registered yet (e.g. the plugin adding it loads after QAV).
 * It is replaced when the type is registered, keeping the index of the attachments.
 */
public class PendingAttachment extends Attachment {
	private final String type;
	private final ConfigurationSection config;

	public PendingAttachment(@NotNull String type, @NotNull ConfigurationSection config) {
		super(config.getName(), 0, null, null);
		this.type = type;
		this.config = config;
	}

	public @NotNull String getType() {
		return type;
	}

	public @NotNull ConfigurationSection getConfig() {
		return config;
	}

	@Override
	public boolean isSpawnable() {
		return false;
	}

	@Override
	public void animate(@NotNull VehicleEntity ve, int index, @NotNull ArmorStand entity) {
	}
}
