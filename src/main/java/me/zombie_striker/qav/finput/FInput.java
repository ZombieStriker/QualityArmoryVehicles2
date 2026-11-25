package me.zombie_striker.qav.finput;


import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface FInput {

	void onInput(VehicleEntity ve);

	/**
	 * Called when the input is used from a specific point of the vehicle, e.g. by a turret.
	 * Inputs that do not support it fall back to {@link #onInput(VehicleEntity)}.
	 *
	 * @param shooter   the player using the input
	 * @param origin    where the input is used from
	 * @param direction where the input is aimed to
	 */
	default void onInput(@NotNull VehicleEntity ve, @NotNull Player shooter, @NotNull Location origin, @NotNull Vector direction) {
		onInput(ve);
	}

	String getName();

	enum ClickType {
		RIGHT("RMB"),
		LEFT("LMB"),
		F("F");

		private final String id;

		ClickType(final String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}
	}
}
