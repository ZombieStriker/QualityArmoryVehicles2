package me.zombie_striker.qav;

import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

@SuppressWarnings("deprecation")
public class ItemFact {

	public static Material skull;

	public static void init() {
		skull = Material.matchMaterial("PLAYER_HEAD");
		if (skull == null)
			skull = Material.matchMaterial("SKULL_ITEM");
	}

	public static ItemStack askull(String owner, String displayname, String... lore) {
		boolean oldsk = skull.name().equals("SKULL_ITEM");
		ItemStack i = a(skull, oldsk ? SkullType.PLAYER.ordinal() : 0, displayname, lore);
		if (owner != null || oldsk) {
			SkullMeta sm = (SkullMeta) (i.getItemMeta());
			try {
				sm.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
			} catch (Error | Exception e4) {
				sm.setOwner(owner);
			}
			i.setItemMeta(sm);
		}
		return i;
	}

	public static ItemStack a(Material m, String displayname, String... lore) {
		return a(m, 0, displayname, lore);
	}

	public static ItemStack getCarItem(AbstractVehicle car) {
		if(car != null)
		return getCarItem(car, car.getMaterial(), car.getItemData());
		return null;
	}

	public static ItemStack getCarItem(AbstractVehicle car, Material material) {
		return getCarItem(car, material, car.getItemData());
	}

	public static ItemStack getCarItem(AbstractVehicle car, Material material, int data) {
		ItemStack caritem = null;
		ItemMeta im = null;
		try {
			caritem= new ItemStack(material);
			im = caritem.getItemMeta();
			im.setCustomModelData(data);
		}catch (Error|Exception e45){
			caritem= new ItemStack(material, 1, (short) data);
			im = caritem.getItemMeta();
		}
		if (car != null) {
			im.setDisplayName(ChatColor.GOLD + car.getDisplayname());
			if (car.hasLore())
				im.setLore(car.getLore());
		}
		try {
			im.setUnbreakable(true);
		} catch (Error | Exception e4) {
		}
		caritem.setItemMeta(im);
		return caritem;
	}

	public static ItemStack a(Material m, int data, String displayname, String... lore) {
		return a(m, data, false, displayname, lore);
	}

	public static ItemStack a(Material m, int data, boolean unbreakable, String displayname, String... lore) {
		ItemStack i = null;
		ItemMeta im = null;
		try {
			i= new ItemStack(m);
			im = i.getItemMeta();
			im.setCustomModelData(data);
		}catch (Error|Exception e45){
			i= new ItemStack(m, 1, (short) data);
		im = i.getItemMeta();
		}

		im.setDisplayName(displayname);
		if (lore != null)
			im.setLore(Arrays.asList(lore));
		try {
			im.setUnbreakable(unbreakable);
		} catch (Error | Exception e4) {
			try {
				im.setUnbreakable(unbreakable);
			} catch (Exception | Error e3) {

			}
		}
		i.setItemMeta(im);
		return i;
	}
}
