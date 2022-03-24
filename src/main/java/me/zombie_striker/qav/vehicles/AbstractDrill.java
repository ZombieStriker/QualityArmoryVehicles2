package me.zombie_striker.qav.vehicles;

import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AbstractDrill extends AbstractCar {
    private final ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);

    public AbstractDrill(String name, int id) {
        super(name, id);
    }

    @Override
    public void tick(VehicleEntity vehicleEntity) {
        super.tick(vehicleEntity);

        if (vehicleEntity.getModelEntities().size() == 0) return;

        Location location = vehicleEntity.getCenter().clone().add(0, 2, 0).add(vehicleEntity.getDirection());
        near(location.getBlock()).forEach(block -> {
            block.getDrops(pickaxe).forEach(item -> QualityArmoryVehicles.giveOrDrop(vehicleEntity.getTrunk(), location, item));
            block.setType(Material.AIR);
        });
    }

    private @NotNull List<Block> near(@NotNull Block center) {
        ArrayList<Block> blocks = new ArrayList<>();
        for(double x = center.getLocation().getX() - 2; x <= center.getLocation().getX(); x++){
            for(double y = center.getLocation().getY() - 2; y <= center.getLocation().getY(); y++){
                Location loc = new Location(center.getWorld(), x, y, center.getZ());
                blocks.add(loc.getBlock());
            }
        }
        return blocks;
    }
}
