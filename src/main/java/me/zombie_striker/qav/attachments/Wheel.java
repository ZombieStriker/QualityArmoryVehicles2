package me.zombie_striker.qav.attachments;

import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;

import java.util.List;

public class Wheel extends Attachment {

    public Wheel(String name, int id, Material material, Vector vector) {
        super(name, id, material, vector);
    }

    public Wheel(String name, List<String> lore, int id, Material material, Vector vector) {
        super(name, lore, id, material, vector);
    }

    @Override
    public void animate(VehicleEntity ve, ArmorStand e) {
        e.setHeadPose(e.getHeadPose().add(0,0,0.5));
    }
}
