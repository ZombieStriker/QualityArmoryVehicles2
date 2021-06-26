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
    private boolean inGarage;

    public UnlockedVehicle(AbstractVehicle ab, double health, boolean inGarage) {
        this.vehicleType = ab;
        this.health = health;
        this.inGarage = inGarage;
    }

    public UnlockedVehicle(Map<String, Object> data) {
        vehicleType = QualityArmoryVehicles.getVehicle((String) data.get("type"));
        inGarage = (boolean) data.getOrDefault("inGarage", true);

        if(vehicleType==null)
            return;

        health = (double) data.get("health");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnlockedVehicle that = (UnlockedVehicle) o;
        return Double.compare(that.health, health) == 0 && inGarage == that.inGarage && Objects.equals(vehicleType, that.vehicleType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleType, health, inGarage);
    }

    public double getHealth() {
        return health;
    }

    public void setInGarage(boolean inGarage) {
        this.inGarage = inGarage;
    }

    /**
     * @return true if the vehicle is in the garage
     */
    public boolean isInGarage() {
        return inGarage;
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
        data.put("inGarage",inGarage);
        return data;
    }
}
