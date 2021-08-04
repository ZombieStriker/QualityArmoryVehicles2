package me.zombie_striker.qav.easygui;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class EasyGUI {

	private static final String BACK_NAME = ChatColor.RED + "[Previous Page]";
	private static final String FORWARD_NAME = ChatColor.GREEN + "[Next Page]";
	private static final String BOTTOM_NAME = "";
	private static HashMap<String, EasyGUI> lists = new HashMap<>();
	private static ItemStack BOTTOM;
	private static ItemStack BACK;
	private static ItemStack FORWARD;
	private static boolean beenEnabled = false;

	static {
		try {
			BOTTOM = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
			BACK = new ItemStack(Material.RED_STAINED_GLASS_PANE);
			FORWARD = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		} catch (Error | Exception e4) {
			BOTTOM = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 0);
			BACK = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 12);
			FORWARD = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 6);
		}
		ItemMeta temp;

		temp = BOTTOM.getItemMeta();
		temp.setDisplayName(BOTTOM_NAME);
		BOTTOM.setItemMeta(temp);

		temp = BACK.getItemMeta();
		temp.setDisplayName(BACK_NAME);
		BACK.setItemMeta(temp);

		temp = FORWARD.getItemMeta();
		temp.setDisplayName(FORWARD_NAME);
		FORWARD.setItemMeta(temp);
	}

	private ItemStack[] items;
	private String title;
	private String displayname;
	private EasyGUICallable[] buttons;
	private Inventory[] loadedInventories;

	private EasyGUI(ItemStack[] items, String title, boolean maximizeInventory) {
		this(items, title, title, maximizeInventory);
	}

	private EasyGUI(ItemStack[] items, String title, String displayname, boolean maximizeInventory) {
		this.items = items;
		buttons = new EasyGUICallable[items.length];

		this.title = title;
		this.displayname = displayname;

		loadedInventories = new Inventory[(((items.length / (9 * 5)) + 1))];
		for (int page = 0; page < (items.length / (45)) + 1; page++) {
			int id = page * 45;
			int size = maximizeInventory ? 54 : (Math.min(45, (((items.length - id - 1) / 9) * 9)/*+9*/) + 9);
			Inventory pageI = Bukkit.createInventory(null, size, displayname);
			for (int i = 0; i < Math.min(45, items.length - id); i++) {
				if (i < pageI.getSize())
					pageI.setItem(i, items[id + i]);
			}
			if (items.length > 45) {
				for (int i = 0; i < 7; i++) {
					pageI.setItem(45 + i, BOTTOM);
				}
				if (page == 0) {
					pageI.setItem(52, BOTTOM);
				} else {
					pageI.setItem(52, BACK);
				}
				if (page == (items.length / (45)) - 1) {
					pageI.setItem(53, BOTTOM);
				} else {
					pageI.setItem(53, FORWARD);
				}
			}
			loadedInventories[page] = pageI;
		}
	}

	public static void INIT(JavaPlugin plugin) {
		if (!beenEnabled)
			Bukkit.getPluginManager().registerEvents(new InventoryListener(), plugin);
		beenEnabled = true;
	}

	public static EasyGUI generateGUIIfNoneExist(ItemStack[] items, String title, boolean removeEmptySlots) {
		return generateGUIIfNoneExist(items, title, title, !removeEmptySlots);
	}

	public static EasyGUI generateGUIIfNoneExist(ItemStack[] items, String title, String displayname, boolean removeEmptySlots) {
		EasyGUI gui = new EasyGUI(items, title, displayname, !removeEmptySlots);

		if (lists.containsKey(title) && lists.get(title).equals(gui))
			return lists.get(title);
		lists.put(title,gui);
		return gui;
	}

	public static EasyGUI generateGUIIfNoneExist(ItemStack[] items, String title, String displayname, boolean removeEmptySlots, boolean k) {
		if (!k)
			if (lists.containsKey(title))
				return lists.get(title);
		EasyGUI gui = new EasyGUI(items, title, displayname, !removeEmptySlots);
		lists.put(title,gui);
		return gui;
	}

	public static EasyGUI generateGUIIfNoneExist(int maxAmountAllowedItems, String title, boolean removeEmptySlots) {
		return generateGUIIfNoneExist(maxAmountAllowedItems, title, title, !removeEmptySlots);
	}

	public static EasyGUI generateGUIIfNoneExist(int maxAmountAllowedItems, String title, String displayname, boolean removeEmptySlots) {
		return generateGUIIfNoneExist(maxAmountAllowedItems, title, displayname, removeEmptySlots, false);
	}

	public static EasyGUI generateGUIIfNoneExist(int maxAmountAllowedItems, String title, String displayname, boolean removeEmptySlots, boolean forceCreateNew) {
		if (!forceCreateNew)
			if (lists.containsKey(title))
				return lists.get(title);
		if (maxAmountAllowedItems % 9 != 0)
			maxAmountAllowedItems = (((maxAmountAllowedItems / 9) + 1) * 9);
		if (maxAmountAllowedItems == 0)
			maxAmountAllowedItems = 9;

		EasyGUI gui = new EasyGUI(new ItemStack[maxAmountAllowedItems], title, displayname, !removeEmptySlots);
		lists.put(title, gui);
		return gui;
	}

	public static EasyGUI getEasyGUIByName(String title) {
		return lists.get(title);
	}

	public static EasyGUI getEasyGUIByInventory(Inventory inventory) {
		for (EasyGUI gui : lists.values()) {
			for (Inventory i : gui.loadedInventories)
				if (i != null && i.equals(inventory))
					return gui;
		}
		return null;
	}

	public void updateButton(int slot, ItemStack newItem) {
		updateButton(slot, newItem, buttons[slot]);
	}

	public void updateButton(int slot, final ItemStack newItem, EasyGUICallable newCallable) {
		final int page = slot / 45;
		final int i = slot - (page * 45);

		if (page >= loadedInventories.length) {
			Inventory[] tempItem = new Inventory[page];
			EasyGUICallable[] buttons = new EasyGUICallable[items.length];
			for (int l = 0; l < loadedInventories.length; l++) {
				tempItem[l] = loadedInventories[l];
			}
			loadedInventories = tempItem;
			this.buttons = buttons;
		}

		if (page >= 1) {
			new BukkitRunnable() {
				@Override
				public void run() {
					loadedInventories[page].setItem(i, newItem);
				}
			}.runTaskLater(QualityArmoryVehicles.getPlugin(), page * 4);
		} else {
			loadedInventories[page].setItem(i, newItem);
		}
		if (items.length <= slot) {
			ItemStack[] ti = items;
			EasyGUICallable[] tb = buttons;
			items = new ItemStack[(((slot / 9) + 1) * 9)];
			buttons = new EasyGUICallable[(((slot / 9) + 1) * 9)];
			for (int k = 0; k < ti.length; k++) {
				items[k] = ti[k];
				buttons[k] = tb[k];
			}
		}
		items[slot] = newItem;
		buttons[slot] = newCallable;
	}

	public void updateAllCallables(EasyGUICallable... callables) {
		buttons = callables;
	}

	public int getPageIDFromInventory(Inventory inventory) {
		for (int i = 0; i < loadedInventories.length; i++)
			if (loadedInventories[i].equals(inventory))
				return i;
		return -1;
	}

	public void registerButton(EasyGUICallable code, int itemSlot) {
		buttons[itemSlot] = code;
	}

	public Inventory getPageByID(int id) {
		return loadedInventories[id];
	}

	static class InventoryListener implements Listener {

		@EventHandler
		public void onClick(InventoryClickEvent e) {
			EasyGUI gui = null;
			if ((gui = EasyGUI.getEasyGUIByInventory(e.getClickedInventory())) != null) {
				e.setCancelled(true);
				if (e.getClick().isShiftClick()) {
					e.setCancelled(true);
				}
				if (e.getCurrentItem() != null) {
					if (e.getSlot() >= 45) {
						e.setCancelled(true);
						if (e.getCurrentItem().isSimilar(BOTTOM))
							return;
						if (e.getSlot() == 52) {
							//back
							int page = gui.getPageIDFromInventory(e.getClickedInventory());
							if (page > 0) {
								e.getWhoClicked().closeInventory();
								e.getWhoClicked().openInventory(gui.getPageByID(page - 1));
							}
						} else if (e.getSlot() == 53) {
							//back
							int page = gui.getPageIDFromInventory(e.getClickedInventory());
							if (page < gui.loadedInventories.length - 1) {
								e.getWhoClicked().closeInventory();
								e.getWhoClicked().openInventory(gui.getPageByID(page + 1));
							}
						}
						return;
					}
					ClickData data = new ClickData(e, gui);
					if (gui.buttons.length > e.getSlot())
						if (gui.buttons[e.getSlot()] != null)
							gui.buttons[e.getSlot()].call(data);
				}
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		EasyGUI easyGUI = (EasyGUI) o;
		return Arrays.equals(items, easyGUI.items) && Objects.equals(title, easyGUI.title) && Objects.equals(displayname, easyGUI.displayname) && Arrays.equals(loadedInventories, easyGUI.loadedInventories);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(title, displayname);
		result = 31 * result + Arrays.hashCode(items);
		result = 31 * result + Arrays.hashCode(loadedInventories);
		return result;
	}
}
