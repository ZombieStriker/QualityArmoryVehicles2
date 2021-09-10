package me.zombie_striker.qav.finput;


import me.zombie_striker.qav.VehicleEntity;

import java.util.Map;

public interface FInput {

	void onInput(VehicleEntity ve);
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
