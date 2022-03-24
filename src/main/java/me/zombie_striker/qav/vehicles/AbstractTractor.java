package me.zombie_striker.qav.vehicles;

import com.cryptomorin.xseries.XMaterial;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.util.BlockCollisionUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AbstractTractor extends AbstractCar {

    public AbstractTractor(String name, int id) {
        super(name, id);
    }

    @Override
    public void tick(VehicleEntity vehicleEntity) {
        super.tick(vehicleEntity);

        if (vehicleEntity.getModelEntities().size() == 0) return;
        near(vehicleEntity.getCenter().clone().subtract(0,1,0).getBlock()).forEach(block -> {
            if (BlockCollisionUtil.getMaterial(block.getLocation()) == XMaterial.FARMLAND.parseMaterial()) {
                block.getLocation().add(0,1,0).getBlock().setType(XMaterial.WHEAT.parseMaterial());
            }
        });
    }

    private @NotNull List<Block> near(@NotNull Block center) {
        ArrayList<Block> blocks = new ArrayList<>();
        for(double x = center.getLocation().getX() - 2; x <= center.getLocation().getX(); x++){
            for(double z = center.getLocation().getZ() - 2; z <= center.getLocation().getZ(); z++){
                Location loc = new Location(center.getWorld(), x, center.getY(), z);
                blocks.add(loc.getBlock());
            }
        }
        return blocks;
    }

    @Override
    boolean canJump() {
        return false;
    }
}
