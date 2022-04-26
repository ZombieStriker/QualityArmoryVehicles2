package me.zombie_striker.qav;

import me.zombie_striker.qav.attachments.Attachment;
import me.zombie_striker.qav.attachments.Wheel;
import me.zombie_striker.qav.vehicles.AbstractVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

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

	public static ItemStack getItem(AbstractVehicle car) {
		if(car != null)
			return getItem(car.getDisplayname(), car.getLore(), car.getMaterial(), car.getItemData());
		return null;
	}

	public static ItemStack getItem(Attachment attachment) {
		if(attachment != null)
			return getItem(attachment.getName(), null, attachment.getMaterial(), attachment.getId());
		return null;
	}

	public static ItemStack getItem(AbstractVehicle car, Material material) {
		return getItem(car.getDisplayname(),car.getLore(), material, car.getItemData());
	}

	public static ItemStack getItem(String name, List<String> lore, Material material, int data) {
		ItemStack item = null;
		ItemMeta im = null;
		if (Main.useDamage) {
			item= new ItemStack(material, 1, (short) data);
			im = item.getItemMeta();
		} else {
			try {
				item= new ItemStack(material);
				im = item.getItemMeta();
				im.setCustomModelData(data);
			}catch (Error|Exception e45){
				item= new ItemStack(material, 1, (short) data);
				im = item.getItemMeta();
			}
		}

		if (name != null) {
			im.setDisplayName(ChatColor.GOLD + name);
			if (lore != null)
				im.setLore(lore);
		}
		try {
			im.setUnbreakable(true);
		} catch (Error | Exception e4) {
		}
		item.setItemMeta(im);
		return item;
	}

	public static ItemStack a(Material m, int data, String displayname, String... lore) {
		return a(m, data, false, displayname, lore);
	}

	public static ItemStack a(Material m, int data, boolean unbreakable, String displayname, String... lore) {
		ItemStack i = null;
		ItemMeta im = null;
		if (Main.useDamage) {
			i= new ItemStack(m, 1, (short) data);
			im = i.getItemMeta();
		} else {
			try {
				i= new ItemStack(m);
				im = i.getItemMeta();
				im.setCustomModelData(data);
			}catch (Error|Exception e45){
				i= new ItemStack(m, 1, (short) data);
				im = i.getItemMeta();
			}
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
