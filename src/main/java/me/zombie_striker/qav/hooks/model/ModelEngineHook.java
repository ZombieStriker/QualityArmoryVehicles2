package me.zombie_striker.qav.hooks.model;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModelEngineHook {
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
        ActiveModel model = ModelEngineAPI.api.getModelManager().createActiveModel(vehicle.getType().getName());
        if (model == null) return;

        ModeledEntity modelEntity = ModelEngineAPI.api.getModelManager().createModeledEntity(vehicle.getModelEntity());
        if (modelEntity == null) return;

        vehicle.getModelEntity().setHelmet(new ItemStack(Material.AIR));

        modelEntity.addActiveModel(model);
        modelEntity.detectPlayers();
    }

    public static void playAnimation(VehicleEntity vehicle, String id) {
        if (!enabled) return;

        try {
            playAnimation0(vehicle,id);
        } catch (Exception | Error ignored) {}
    }

    private static void playAnimation0(VehicleEntity vehicle, String id) {
        ModeledEntity modelEntity = ModelEngineAPI.api.getModelManager().getModeledEntity(vehicle.getModelEntity().getUniqueId());
        if (modelEntity == null) return;

        modelEntity.getActiveModel(vehicle.getType().getName()).addState(id,10,10,1);
    }


    public static boolean isInitialized() {
        return enabled;
    }
}
