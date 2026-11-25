package me.zombie_striker.qav.attachments;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Creates an attachment from its section in a vehicle file.
 *
 * @see AttachmentRegistry#register(String, AttachmentFactory)
 */
@FunctionalInterface
public interface AttachmentFactory {

	/**
	 * The common options (name, id, material, position and seat) are already read by the plugin, the seat is applied
	 * after the attachment is created when the section contains it.
	 *
	 * @param name     the name of the model item
	 * @param id       the custom model data of the model item
	 * @param material the material of the model item, null if the attachment has no model
	 * @param position the position of the attachment relative to the model
	 * @param config   the section of the attachment, to read custom options
	 * @return the attachment
	 * @throws IllegalArgumentException if an option is not valid, the message is shown in the console
	 */
	@NotNull Attachment create(@NotNull String name, int id, @Nullable Material material, @NotNull Vector position,
							   @NotNull ConfigurationSection config) throws IllegalArgumentException;
}
