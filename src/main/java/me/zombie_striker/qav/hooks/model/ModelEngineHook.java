package me.zombie_striker.qav.hooks.model;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.state.DefaultStateHandler;
import com.ticxo.modelengine.api.animation.state.ModelState;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModelEngineHook {
    private static final Map<UUID,ModeledEntity> MODELS = new HashMap<>();
    private static boolean enabled = false;

    public static void init() {
        enabled = true;
    }

    public static void createModel(VehicleEntity vehicle) {
        if (!enabled) return;

        try {
            createModel0(vehicle);
        } catch (Exception | Error ignored) {}
    }

    @SuppressWarnings("deprecation")
    private static void createModel0(@NotNull VehicleEntity vehicle) {
        MODELS.remove(vehicle.getVehicleUUID());
        ActiveModel model = ModelEngineAPI.createActiveModel(vehicle.getType().getName());
        if (model == null) return;

        ModeledEntity modelEntity = ModelEngineAPI.createModeledEntity(vehicle.getModelEntity());
        if (modelEntity == null) return;

        Main.DEBUG("Created model for " + vehicle.getType().getName());

        vehicle.getModelEntity().setHelmet(new ItemStack(Material.AIR));

        modelEntity.addModel(model, false);
        modelEntity.getRangeManager().setRenderDistance(100);
        MODELS.put(vehicle.getVehicleUUID(),modelEntity);
    }

    public static void playAnimation(VehicleEntity vehicle, String id) {
        if (!enabled) return;

        try {
            playAnimation0(vehicle,id);
        } catch (Exception | Error e) {
            Main.DEBUG("Failed to play animation for " + vehicle.getType().getName() + ": " + e.getMessage());
        }
    }

    private static void playAnimation0(VehicleEntity vehicle, String id) {
        if (!MODELS.containsKey(vehicle.getVehicleUUID()))
            createModel(vehicle);

        MODELS.get(vehicle.getVehicleUUID())
                .getModel(vehicle.getType().getName())
                .getDefaultStateHandler()
                .setProperty(ModelState.WALK, new DefaultStateHandler.Property(id, 10, 1, 1));
        Main.DEBUG("Playing animation for " + vehicle.getType().getName());
    }


    public static boolean isInitialized() {
        return enabled;
    }
}
