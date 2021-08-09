package me.zombie_striker.qav.vehicles;

import com.comphenix.protocol.events.PacketEvent;
import com.cryptomorin.xseries.XPotion;
import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.events.VehicleTurnEvent;
import me.zombie_striker.qav.util.HeadPoseUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractSubmarine extends AbstractVehicle {
    public AbstractSubmarine(String name, int id) {
        super(name, id);
    }

    @Override
    public void handleTurnLeft(VehicleEntity ve, PacketEvent event) {
        VehicleTurnEvent e = new VehicleTurnEvent(ve,ve.getAngleRotation(), ve.getAngleRotation() + ve.getType().getRotationDelta());
        Bukkit.getPluginManager().callEvent(e);
        if(e.isCanceled())
            return;
        ve.setAngle(ve.getAngleRotation() + ve.getType().getRotationDelta());
        HeadPoseUtil.setHeadPoseUsingReflection(ve);
    }

    @Override
    public void handleTurnRight(VehicleEntity ve, PacketEvent event) {
        VehicleTurnEvent e = new VehicleTurnEvent(ve,ve.getAngleRotation(), ve.getAngleRotation() - ve.getType().getRotationDelta());
        Bukkit.getPluginManager().callEvent(e);
        if(e.isCanceled())
            return;
        ve.setAngle(ve.getAngleRotation() - ve.getType().getRotationDelta());
        HeadPoseUtil.setHeadPoseUsingReflection(ve);
    }


    @Override
    public void handleSpeedIncrease(VehicleEntity ve, PacketEvent event) {
        if (!this.handleFuel(ve,event)) {
            return;
        }
        ve.setSpeed(Math.min(ve.getSpeed() + 0.1, ve.getType().getMaxSpeed()));
    }

    @Override
    public void handleSpeedDecrease(VehicleEntity ve, PacketEvent event) {
        if (!this.handleFuel(ve,event)) {
            return;
        }
        ve.setSpeed(Math.max(ve.getSpeed() - 0.1, -ve.getType().getMaxSpeed()));

    }

    @Override
    public void handleSpace(VehicleEntity ve, PacketEvent event) {

    }

    @Override
    public void tick(VehicleEntity vehicleEntity) {
        PotionEffect potion = XPotion.NIGHT_VISION.parsePotion(10, 5);
        if (XPotion.NIGHT_VISION.parsePotionEffectType() != null && potion != null) {
            List<LivingEntity> entities = new ArrayList<>();
            if (vehicleEntity.getDriverSeat().getPassenger() != null && vehicleEntity.getDriverSeat().getPassenger() instanceof LivingEntity)
                entities.add((LivingEntity) vehicleEntity.getDriverSeat().getPassenger());
            entities.addAll(vehicleEntity.getPassagerSeats()
                    .stream()
                    .filter((e) -> e instanceof LivingEntity)
                    .map((e) -> ((LivingEntity) e.getPassenger()))
                    .collect(Collectors.toList()));

            for (LivingEntity entity : entities) {
                if (entity == null) continue;
                PotionEffect potionEffect = entity.getPotionEffect(XPotion.NIGHT_VISION.parsePotionEffectType());
                if (potionEffect == null) {
                    entity.addPotionEffect(potion);
                }
            }
        }

        basicDirections(vehicleEntity,false,true);
    }
}
