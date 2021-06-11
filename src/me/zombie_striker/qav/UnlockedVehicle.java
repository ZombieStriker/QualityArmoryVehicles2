package me.zombie_striker.qav;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UnlockedVehicle implements ConfigurationSerializable {
    private final AbstractVehicle vehicleType;
    private double health;

    public UnlockedVehicle(AbstractVehicle ab, double health) {
        this.vehicleType = ab;
        this.health = health;
    }

    public UnlockedVehicle(Map<String, Object> data) {
        vehicleType = QualityArmoryVehicles.getVehicle((String) data.get("type"));

        if(vehicleType==null)
            return;

        health = (double) data.get("health");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnlockedVehicle that = (UnlockedVehicle) o;
        return Double.compare(that.health, health) == 0 && Objects.equals(vehicleType, that.vehicleType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleType, health);
    }

    public double getHealth() {
        return health;
    }

    public AbstractVehicle getVehicleType() {
        return vehicleType;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> data = new HashMap<>();
        data.put("type",vehicleType.getName());
        data.put("health",health);
        return data;
    }
}
