package me.zombie_striker.qav.fuel;

import com.cryptomorin.xseries.ReflectionUtils;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.VehicleEntity;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class FuelItemStack {

	private static final HashMap<FuelItemStack, Integer> fuels = new HashMap<>();

	private final String name;
	private final Material material;
	private final int data;
	private final List<String> lore;
	private final boolean shouldBeInShop;
	private final int cost;

	private static final String OLD_BAD = "%name=%";
	private static final String newSplit = "%";

	public static Set<FuelItemStack> getFuels(){
		return fuels.keySet();
	}

	public FuelItemStack(String name, Material m, int data, List<String> lore) {
		this(name,m,data,lore,false,0);
	}
	public FuelItemStack(String name, Material m, int data, List<String> lore, boolean shouldBeInShop, int cost) {
		this.name = name != null ? ChatColor.translateAlternateColorCodes('&', name) : null;
		this.material = m;
		this.data = data;
		this.lore = lore;
		this.shouldBeInShop = shouldBeInShop;
		this.cost = cost;
	}

	public boolean isFuel(ItemStack check) {
		if (check.getType() != material)
			return false;
		try {
			if ((data != 0 && !check.getItemMeta().hasCustomModelData()) || (check.getItemMeta().hasCustomModelData() && check.getItemMeta().getCustomModelData() != data))
				return false;
		}catch(Error|Exception ignored){}
		return !hasCustomName() || (check.hasItemMeta()
				&& ((check.getItemMeta().hasDisplayName() && check.getItemMeta().getDisplayName().equals(name))));
	}
	public int getCost(){
		return cost;
	}

	public boolean isAllowedInShop(){
		return shouldBeInShop;
	}
	public Material getMaterial(){
		return material;
	}
	public int getData(){
		return data;
	}
	public List<String> getLore(){
		return lore;
	}
	public String getDisplayname(){
		return name;
	}

	public boolean hasCustomName() {
		return name != null;
	}

	public boolean hasLore() {
		return lore != null;
	}

	public static int getFuelForItem(ItemStack is) {
		for (Entry<FuelItemStack, Integer> f : fuels.entrySet())
			if (f.getKey().isFuel(is))
				return f.getValue();
		return 0;
	}
	public static FuelItemStack getFuelItemInstance(ItemStack is) {
		for (Entry<FuelItemStack, Integer> f : fuels.entrySet())
			if (f.getKey().isFuel(is))
				return f.getKey();
		return null;
	}
	public ItemStack getItemStack() {
		ItemStack temp = new ItemStack(material);
		ItemMeta meta = temp.getItemMeta();
		meta.setLore(lore.stream().map(e -> ChatColor.translateAlternateColorCodes('&', e)).collect(Collectors.toList()));
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getDisplayname()));
		try{
		meta.setCustomModelData(data);
	}catch(Error|Exception ignored){}
		temp.setItemMeta(meta);
		return temp;
	}

	public static void registerNewFuel(FuelItemStack fis, Integer fuel) {
		fuels.put(fis, fuel);
	}

	public static void loadFuels(File yml) {
		fuels.clear();
		FileConfiguration config = YamlConfiguration.loadConfiguration(yml);
		String splitChar = newSplit;

		if (Main.ENABLE_FILE_CREATION) {
			registerNewFuelToConfig(null, Material.COAL, (short) 0, null, 500, yml);
			registerNewFuelToConfig(null, Material.COAL_BLOCK, (short) 0, null, 9 * 500, yml);
			registerNewFuelToConfig(null, Material.BLAZE_POWDER, (short) 0, null, 500, yml);
			registerNewFuelToConfig(null, Material.BLAZE_ROD, (short) 0, null, 1000, yml);
			registerNewFuelToConfig(null, Material.LAVA_BUCKET, (short) 0, null, 20 * 500, yml);
			registerNewFuelToConfig("&6Fuel Canister", ReflectionUtils.supports(14) ? Material.RABBIT_HIDE : Material.DIAMOND_AXE, (short) 38, Collections.singletonList("&7Fuel for: 500 seconds"), 20 * 500, yml, true,50);
		}

		for (String key : config.getKeys(false)) {
			if (key.contains(OLD_BAD))
				splitChar = OLD_BAD;
			String[] split = key.split(splitChar);

			Material mat = Material.getMaterial(split[0]);
			String name = (split.length > 1 && split[1].length() > 0 && !split[1].equals("null")) ? split[1] : null;
			short data = (short) (config.contains(key + ".data") ? config.getInt(key + ".data") : 0);
			List<String> lore = config.contains(key + ".lore") ? config.getStringList(key + ".lore") : null;

			int fuel = (short) (config.contains(key + ".fuelevel") ? config.getInt(key + ".fuelevel") : 100);
			boolean shouldBeShop = (config.contains(key + ".shouldBeInShop") && config.getBoolean(key + ".shouldBeInShop"));
			int cost = (config.contains(key + ".cost") ? config.getInt(key + ".cost") : 0);

			FuelItemStack f = new FuelItemStack(name, mat, data, lore,shouldBeShop,cost);
			registerNewFuel(f, fuel);
		}
	}

	public static void registerNewFuelToConfig(String name, Material m, short data, List<String> lore, int fuellevel,
											   File yml) {
	registerNewFuelToConfig(name,m,data,lore,fuellevel,yml,false,0);
	}
	public static void registerNewFuelToConfig(String name, Material m, short data, List<String> lore, int fuellevel,
			File yml, boolean shouldBeInShop,int cost) {
		FuelItemStack f = new FuelItemStack(name, m, data, lore,shouldBeInShop, cost);
		registerNewFuel(f, fuellevel);

		String splitChar = newSplit;

		FileConfiguration config = YamlConfiguration.loadConfiguration(yml);
		config.set(m.name() + splitChar + name + ".data", data);
		if (lore != null)
			config.set(m.name() + splitChar + name + ".lore", lore);
		config.set(m.name() + splitChar + name + ".fuelevel", fuellevel);
		config.set(m.name() + splitChar + name + ".shouldBeInShop", shouldBeInShop);
		config.set(m.name() + splitChar + name + ".cost", cost);
		try {
			config.save(yml);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void updateFuel(VehicleEntity ve) {
		if (ve.getFuel() <= 0) {
			for (int slot = 0; slot < ve.getFuels().getSize(); slot++) {
				ItemStack isOrg = ve.getFuels().getItem(slot);
				if (isOrg != null) {
					ItemStack is = isOrg.clone();
					int rate = FuelItemStack.getFuelForItem(is);
					if (rate > 0) {
						if (is.getAmount() > 1) {
							is.setAmount(is.getAmount() - 1);
							ve.getFuels().setItem(slot, is);
						} else if (is.getAmount() <= 1) {
							ve.getFuels().setItem(slot, null);
						}
						ve.setFuel(ve.getFuel() + rate);
						break;
					}
				}
			}
		}
	}

	public static void addNewItem(File yml, ItemStack is, int fuellevel) {
		addNewItem(yml,is,fuellevel,false,0);
	}
	public static void addNewItem(File yml, ItemStack is, int fuellevel, boolean shouldBeInShop, int cost) {
		Material m = is.getType();
		@SuppressWarnings("deprecation")
		short data = is.getDurability();

		String splitChar = newSplit;

		String name = (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) ? is.getItemMeta().getDisplayName()
				: null;
		List<String> lore = (is.hasItemMeta() && is.getItemMeta().hasLore()) ? is.getItemMeta().getLore() : null;

		FuelItemStack f = new FuelItemStack(name, m, data, lore, shouldBeInShop, cost);
		registerNewFuel(f, fuellevel);

		FileConfiguration config = YamlConfiguration.loadConfiguration(yml);
		config.set(m.name() + splitChar + name + ".data", data);
		if (lore != null)
			config.set(m.name() + splitChar + name + ".lore", lore);
		config.set(m.name() + splitChar + name + ".fuelevel", fuellevel);
		try {
			config.save(yml);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
