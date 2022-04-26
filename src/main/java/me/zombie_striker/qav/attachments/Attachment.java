package me.zombie_striker.qav.attachments;

import me.zombie_striker.qav.VehicleEntity;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public abstract class Attachment {
    private final String name;
    private final List<String> lore;
    private final int id;
    private final Material material;
    private final Vector vector;

    public Attachment(String name, int id, Material material, Vector vector) {
        this(name,null,id,material,vector);
    }

    public Attachment(String name, List<String> lore, int id, Material material, Vector vector) {
        this.name = name;
        this.lore = lore;
        this.id = id;
        this.material = material;
        this.vector = vector;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public Vector getVector() {
        return vector;
    }

    public ItemStack build() {
        return QualityArmoryVehicles.getAttachmentItemStack(this);
    }

    public abstract void animate(VehicleEntity ve, ArmorStand entity);
}
