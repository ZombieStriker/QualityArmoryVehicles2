package me.zombie_striker.qav.fuel;

import com.cryptomorin.xseries.ReflectionUtils;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.MessagesConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RepairItemStack {
    private String name;
    private Material material;
    private List<String> lore;
    private int data;
    private boolean shouldBeInShop;
    private int cost;

    @SuppressWarnings("deprecation")
    public boolean isItem(@Nullable ItemStack item) {
        if (item == null) return false;

        if (item.getType() != material)
            return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        if (tryData(meta)) return true;

        if (name == null && !meta.hasDisplayName()) {
            meta.getDisplayName();
        }

        return name != null && meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', name));
    }

    private boolean tryData(ItemMeta meta) {
        try {
            if (!meta.hasCustomModelData())
                return false;

            if (data == 0)
                return false;

            return meta.getCustomModelData() == data;
        } catch (Exception|Error ignored) {}

        return false;
    }

    private RepairItemStack() {}

    public static RepairItemStack loadFromFile() throws IOException {
        File repairYML = Main.repairYML;

        FileConfiguration configuration = YamlConfiguration.loadConfiguration(repairYML);

        update(configuration,"name", "&6Repair Vehicle");
        update(configuration,"material", ReflectionUtils.supports(14) ? Material.RABBIT_HIDE.name() : Material.DIAMOND_AXE.name());
        update(configuration,"lore", Collections.singletonList("&7Use this item to repair your vehicle"));
        update(configuration,"data", 0);
        update(configuration,"shouldBeInShop", true);
        update(configuration,"cost", 50);

        configuration.save(repairYML);

        return new RepairItemStack()
                .setMaterial(Material.getMaterial(configuration.getString("material", "")))
                .setName(configuration.getString("name"))
                .setLore(configuration.getStringList("lore"))
                .setData(configuration.getInt("data"))
                .setShouldBeInShop(configuration.getBoolean("shouldBeInShop"))
                .setCost(configuration.getInt("cost"));
    }

    public void reload() {
        File repairYML = Main.repairYML;

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(repairYML);
        this.setName(configuration.getString("name"))
                .setMaterial(Material.getMaterial(configuration.getString("material", "")))
                .setLore(configuration.getStringList("lore"))
                .setData(configuration.getInt("data"))
                .setShouldBeInShop(configuration.getBoolean("shouldBeInShop"))
                .setCost(configuration.getInt("cost"));
    }

    private static void update(FileConfiguration config, String path, Object value) {
        if (!config.contains(path))
            config.set(path,value);
    }

    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', name);
    }

    public RepairItemStack setName(String name) {
        this.name = name;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public RepairItemStack setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public List<String> getLore() {
        return lore;
    }

    public RepairItemStack setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public int getData() {
        return data;
    }

    public RepairItemStack setData(int data) {
        this.data = data;
        return this;
    }

    public boolean shouldBeInShop() {
        return shouldBeInShop;
    }

    public RepairItemStack setShouldBeInShop(boolean shouldBeInShop) {
        this.shouldBeInShop = shouldBeInShop;
        return this;
    }

    public int getCost() {
        return cost;
    }

    public RepairItemStack setCost(int cost) {
        this.cost = cost;
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemStack asItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta == null) return null;

        if (data != 0)
            itemMeta.setCustomModelData(data);
        if (name != null)
            itemMeta.setDisplayName(this.getName());
        if (lore != null && !lore.isEmpty())
            itemMeta.setLore(lore.stream().map(MessagesConfig::colorize).collect(Collectors.toList()));

        item.setItemMeta(itemMeta);

        return item;
    }
}
