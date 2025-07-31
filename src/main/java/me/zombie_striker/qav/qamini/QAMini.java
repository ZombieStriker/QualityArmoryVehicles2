package me.zombie_striker.qav.qamini;

import com.cryptomorin.xseries.reflection.XReflection;
import me.zombie_striker.qav.MessagesConfig;
import me.zombie_striker.qav.customitemmanager.AbstractItem;
import me.zombie_striker.qav.customitemmanager.CustomItemManager;
import me.zombie_striker.qav.customitemmanager.MaterialStorage;
import me.zombie_striker.qav.Main;
import me.zombie_striker.qav.api.QualityArmoryVehicles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@SuppressWarnings("deprecation")
public class QAMini implements Listener {

	private static final String CALCTEXT = ChatColor.BLACK + "qa:";
	public static List<MaterialStorage> registeredItems = new ArrayList<>();
	public static List<String> namesToBypass = new ArrayList<>();
	public static List<UUID> resourcepackReq = new ArrayList<>();
	public static HashMap<UUID, Long> sentResourcepack = new HashMap<>();
	public static boolean sendOnJoin = true;
	public static boolean sendTitleOnJoin = true;
	public static boolean shouldSend = true;
	public static boolean overrideURL = false;
	public static boolean kickIfDeny = true;
	public static boolean verboseLogging = false;
	public static String S_ITEM_VARIENTS_NEW = "Variant";

	public static boolean isVersionHigherThan(int mainVersion, int secondVersion) {
		return XReflection.supports(secondVersion);
	}

	@SuppressWarnings("deprecation")
	public static boolean isSolid(Block b, Location l) {
		Material material = b.getType();

		if (material.name().endsWith("CARPET")) {
			return false;
		}
		if (material.name().contains("LEAVE")) {
			return true;
		}
		if (material.name().contains("SLAB") || material.name().contains("STEP")) {
			return (!(l.getY() - l.getBlockY() > 0.5) || b.getData() != 0)
					&& (!(l.getY() - l.getBlockY() <= 0.5) || b.getData() != 1);
		}
		if (material.name().contains("BED_") || material.name().contains("_BED")
				|| material.name().contains("DAYLIGHT_DETECTOR")) {
			return !(l.getY() - l.getBlockY() > 0.5);
		}
		if (material.name().contains("GLASS")) {
			return true;
		}

		if (material.isOccluding()) {
			return true;
		}
		if (material.name().contains("STAIR")) {
			if (b.getData() < 4 && (l.getY() - l.getBlockY() < 0.5))
				return true;
			if (b.getData() >= 4 && (l.getY() - l.getBlockY() > 0.5))
				return true;
			switch (b.getData()) {
				case 0:
				case 4:
					return l.getX() - (0.5 + l.getBlockX()) > 0;
				case 1:
				case 5:
					return l.getX() - (0.5 + l.getBlockX()) < 0;
				case 2:
				case 6:
					return l.getZ() - (0.5 + l.getBlockZ()) > 0;
				case 3:
				case 7:
					return l.getZ() - (0.5 + l.getBlockZ()) < 0;
			}
		}

		return false;
	}

	public static void DEBUG(String debug) {
		Main.DEBUG("[QAVehicles]" + debug);
	}

	@SuppressWarnings("deprecation")
	public static int findSafeSpot(ItemStack newItem, boolean findHighest, boolean allowPockets) {
		return findSafeSpot(newItem.getType(), newItem.getDurability(), findHighest, allowPockets);
	}

	public static int findSafeSpot(Material newItemtype, int startingData, boolean findHighest, boolean allowPockets) {
		int safeDurib = startingData;
		if (allowPockets) {
			List<Integer> idsToWorryAbout = new ArrayList<>();
			for (MaterialStorage j : registeredItems)
				if (j.getMat() == newItemtype && ((j.getData() > safeDurib) == findHighest))
					idsToWorryAbout.add(j.getData());
			if (findHighest) {
				for (int id = safeDurib + 1; id < safeDurib + 1000; id++) {
					if (!idsToWorryAbout.contains(id))
						return id;
				}
			} else {
				for (int id = safeDurib - 1; id > 0; id--) {
					if (!idsToWorryAbout.contains(id))
						return id;
				}
			}
			return 0;
		}

		for (MaterialStorage j : registeredItems)
			if (j.getMat() == newItemtype && (j.getData() > safeDurib) == findHighest)
				safeDurib = j.getData();
		return safeDurib;
	}

	public static int getCalculatedExtraDurib(ItemStack is) {
		if (!is.hasItemMeta() || !is.getItemMeta().hasLore() || Objects.requireNonNull(is.getItemMeta().getLore()).isEmpty())
			return -1;
		List<String> lore = is.getItemMeta().getLore();
		for (String s : lore) {
			if (s.startsWith(CALCTEXT))
				return Integer.parseInt(s.split(CALCTEXT)[1]);
		}
		return -1;
	}

	public static ItemStack addCalulatedExtraDurib(ItemStack is, int number) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = im.getLore();
		if (lore == null) {
			lore = new ArrayList<>();
		} else {
			if (getCalculatedExtraDurib(is) != -1)
				is = removeCalculatedExtra(is);
		}
		lore.add(CALCTEXT + number);
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack decrementCalculatedExtra(ItemStack is) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = is.getItemMeta().getLore();
		for (int i = 0; i < (lore != null ? lore.size() : 0); i++) {
			if (lore.get(i).startsWith(CALCTEXT)) {
				lore.set(i, CALCTEXT + "" + (Integer.parseInt(lore.get(i).split(CALCTEXT)[1]) - 1));
			}
		}
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack removeCalculatedExtra(ItemStack is) {
		if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
			ItemMeta im = is.getItemMeta();
			List<String> lore = is.getItemMeta().getLore();
			for (int i = 0; i < (lore != null ? lore.size() : 0); i++) {
				if (lore.get(i).startsWith(CALCTEXT)) {
					lore.remove(i);
				}
			}
			im.setLore(lore);
			is.setItemMeta(im);
		}
		return is;
	}


	@SuppressWarnings("deprecation")
	public static boolean isCustomItemNextId(ItemStack is) {
		if (is == null)
			return false;
		try {
			if (CustomItemManager.getItemType("vehicles") instanceof AbstractItem)
				return false;
		} catch (Error | Exception ignored) {
		}
		List<MaterialStorage> ms = new ArrayList<>();
		ms.addAll(registeredItems);
		for (MaterialStorage mat : ms) {
			if (mat.getMat() == is.getType())
				if (mat.getData() == (is.getDurability() + 1))
					if (!mat.hasVariant())
						return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static void sendResourcepack(final Player player, final boolean warning) {
		if (namesToBypass.contains(player.getName()) || resourcepackReq.contains(player.getUniqueId()))
			return;
		Main.foliaLib.getScheduler().runNextTick(task -> {
			if (namesToBypass.contains(player.getName())) {
				resourcepackReq.add(player.getUniqueId());
				return;
			}
			if (warning)
				try {
					player.sendTitle(
							MessagesConfig.RESOURCEPACK_TITLE,
							MessagesConfig.RESOURCEPACK_SUBTITLE);
				} catch (Error e2) {
					player.sendMessage(MessagesConfig.RESOURCEPACK_TITLE);
					player.sendMessage(MessagesConfig.RESOURCEPACK_SUBTITLE);
				}
			if (MessagesConfig.RESOURCEPACK_CRASH.length() > 2) {
				player.sendMessage(Main.prefix + MessagesConfig.RESOURCEPACK_CRASH);
			}
			Main.foliaLib.getScheduler().runLater(() -> {
				try {
					player.setResourcePack(CustomItemManager.getResourcepack(player));
					if (!isVersionHigherThan(1, 9)) {
						resourcepackReq.add(player.getUniqueId());
						sentResourcepack.put(player.getUniqueId(), System.currentTimeMillis());
					}
					resourcepackReq.add(player.getUniqueId());
					// If the player is on 1.8, manually add them to the resource list.

				} catch (Exception e) {

				}
			}, 20 * (warning ? 1 : 5));
		});
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.isCancelled())
			return;
		e.getPlayer().getItemInHand();
		if (QualityArmoryVehicles.isVehicleByItem(e.getPlayer().getItemInHand())) {
			e.setCancelled(true);
			return;
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreakMonitor(final BlockBreakEvent e) {
		if (e.isCancelled())
			return;
		int k;
		if (e.getPlayer().getItemInHand() != null && !e.getPlayer().getItemInHand().getType().equals(Material.AIR)) {
			if (CustomItemManager.isUsingCustomData())
				return;
			if ((k = getCalculatedExtraDurib(e.getPlayer().getItemInHand())) != -1) {
				ItemStack hand = e.getPlayer().getItemInHand();
				e.getBlock().breakNaturally(hand);
				e.setCancelled(true);
				final ItemStack t;
				if (k > 0) {
					t = decrementCalculatedExtra(hand);
				} else {
					t = removeCalculatedExtra(hand);
				}
				Main.foliaLib.getScheduler().runNextTick(task -> {
					e.getPlayer().setItemInHand(t);
				});
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		if (e.isCancelled())
			return;
		if (QualityArmoryVehicles.isVehicleByItem(e.getItem().getItemStack())) {
			if (shouldSend && !namesToBypass.contains(e.getPlayer().getName())
					&& !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				sendResourcepack(e.getPlayer(), true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onClickMONITOR(final PlayerInteractEvent e) {
		if (e.getPlayer().getItemInHand() != null
				&& !QualityArmoryVehicles.isVehicleByItem(e.getPlayer().getItemInHand())) {
			DEBUG("Item is not any valid item - mainhand");
			if (!CustomItemManager.isUsingCustomData()) {
				if (isCustomItemNextId(e.getPlayer().getItemInHand())) {
					DEBUG("A player is using a non-gun item, but may reach the textures of one!");
					// If the item is not a gun, but the item below it is
					int safeDurib = findSafeSpot(e.getPlayer().getItemInHand(), true, overrideURL)
							+ (overrideURL ? 0 : 3);

					// if (e.getItem().getDurability() == 1) {
					DEBUG("Safe Durib= " + (safeDurib) + "! ORG " + e.getPlayer().getItemInHand().getDurability());
					ItemStack is = e.getPlayer().getItemInHand();
					is.setDurability((short) (safeDurib));
					is = addCalulatedExtraDurib(is, safeDurib - e.getPlayer().getItemInHand().getDurability());
					e.getPlayer().setItemInHand(is);
					// }
				}
			}
			if (e.getPlayer().getInventory().getItemInOffHand() != null
					&& !QualityArmoryVehicles.isVehicleByItem(e.getPlayer().getInventory().getItemInOffHand())) {
				DEBUG("Item is not any valid item - offhand");
				if (isCustomItemNextId(e.getPlayer().getInventory().getItemInOffHand())) {
					DEBUG("A player is using a non-gun item, but may reach the textures of one!");
					// If the item is not a gun, but the item below it is
					int safeDurib = findSafeSpot(e.getPlayer().getInventory().getItemInOffHand(), true,
							overrideURL) + (overrideURL ? 0 : 3);

					// if (e.getItem().getDurability() == 1) {
					DEBUG("Safe Durib= " + (safeDurib) + "! ORG "
							+ e.getPlayer().getInventory().getItemInOffHand().getDurability());
					ItemStack is = e.getPlayer().getInventory().getItemInOffHand();
					is.setDurability((short) (safeDurib));
					is = addCalulatedExtraDurib(is,
							safeDurib - e.getPlayer().getInventory().getItemInOffHand().getDurability());
					e.getPlayer().getInventory().setItemInOffHand(is);
					// }
				}
			}
		}

	}

	@SuppressWarnings({"deprecation"})
	@EventHandler
	public void onClick(final PlayerInteractEvent e) {
		DEBUG("InteractEvent Called");
		// Quick bugfix for specifically this item.

		try {
			if (QualityArmoryVehicles.isVehicleByItem(e.getPlayer().getItemInHand())) {
				if (!e.getPlayer().getItemInHand().getItemMeta().isUnbreakable()) {
					DEBUG("A player is using a breakable item that reached being a gun!");
					// If the item is not a gun, but the item below it is
					int safeDurib = findSafeSpot(e.getPlayer().getItemInHand(), true, overrideURL)
							+ (overrideURL ? 0 : 3);

					// if (e.getItem().getDurability() == 1) {
					DEBUG("Safe Durib= " + (safeDurib) + "! ORG " + e.getPlayer().getItemInHand().getDurability());
					ItemStack is = e.getPlayer().getItemInHand();
					is.setDurability((short) (safeDurib));
					e.getPlayer().setItemInHand(is);
				}
			}
		} catch (Error | Exception e45) {
		}

		try {
			if (QualityArmoryVehicles.isVehicleByItem(e.getPlayer().getInventory().getItemInOffHand())) {
				if (!e.getPlayer().getInventory().getItemInOffHand().getItemMeta().isUnbreakable()) {
					DEBUG("A player is using a breakable item that reached being a gun!");
					// If the item is not a gun, but the item below it is
					int safeDurib = findSafeSpot(e.getPlayer().getInventory().getItemInOffHand(), true,
							overrideURL) + (overrideURL ? 0 : 3);

					// if (e.getItem().getDurability() == 1) {
					DEBUG("Safe Durib= " + (safeDurib) + "! ORG "
							+ e.getPlayer().getInventory().getItemInOffHand().getDurability());
					ItemStack is = e.getPlayer().getInventory().getItemInOffHand();
					is.setDurability((short) (safeDurib));
					e.getPlayer().getInventory().setItemInOffHand(is);
				}
			}
		} catch (Error | Exception e45) {
		}

		if (!QualityArmoryVehicles.isVehicleByItem(e.getPlayer().getItemInHand())) {
			ItemStack offhand = e.getPlayer().getInventory().getItemInOffHand();
			if (offhand == null || !QualityArmoryVehicles.isVehicleByItem(offhand))
				return;
		}

		if (kickIfDeny && sentResourcepack.containsKey(e.getPlayer().getUniqueId())
				&& System.currentTimeMillis() - sentResourcepack.get(e.getPlayer().getUniqueId()) >= 3000) {
			// the player did not accept resourcepack, and got away with it
			e.setCancelled(true);
			e.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', "&c You have been kicked because you did not accept the resourcepack. \n&f If you want to rejoin the server, edit the server entry and set \"Resourcepack Prompts\" to \"Accept\" or \"Prompt\"'"));
			return;
		}

		if (e.getItem() != null) {
			final ItemStack origin = e.getItem();
			final int slot = e.getPlayer().getInventory().getHeldItemSlot();
			if (!isVersionHigherThan(1, 9)) {
				ItemStack temp1 = null;
				try {
					temp1 = e.getPlayer().getInventory().getItemInOffHand();
				} catch (Error | Exception re453) {
				}
				final ItemStack temp2 = temp1;

				Main.foliaLib.getScheduler().runNextTick(task -> {
					if (origin.getDurability() != e.getPlayer().getItemInHand().getDurability()
							&& slot == e.getPlayer().getInventory().getHeldItemSlot()
							&& (e.getPlayer().getItemInHand() != null
							&& e.getPlayer().getItemInHand().getType() == origin.getType())) {
						try {
							if (temp2 != null
									&& temp2.getDurability() == e.getPlayer().getItemInHand().getDurability())
								return;
						} catch (Error | Exception re54) {
						}
						e.getPlayer().setItemInHand(origin);
						DEBUG("The item in the player's hand changed! Origin " + origin.getDurability() + " New "
								+ e.getPlayer().getItemInHand().getDurability());
					}
				});
			}

			// ItemStack usedItem = e.getPlayer().getItemInHand();

			// Sedn the resourcepack if the player does not have it.
			if (shouldSend && !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
				DEBUG("Player does not have resourcepack!");
				sendResourcepack(e.getPlayer(), true);
			}

		}
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent e) {
		resourcepackReq.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent e) {
		if (XReflection.MINOR_NUMBER == 7) {
			Bukkit.broadcastMessage(Main.prefix
					+ " QualityArmory does not support versions older than 1.9, and may crash clients");
			Bukkit.broadcastMessage(
					"Since there is no reason to stay on outdated updates, (1.7 and 1.8 has quite a number of exploits) update your server.");
			if (shouldSend) {
				shouldSend = false;
				Bukkit.broadcastMessage(Main.prefix + ChatColor.RED + " Disabling resourcepack.");
			}
		}

		if (sendOnJoin) {
			sendResourcepack(e.getPlayer(), sendTitleOnJoin);
		} else {
			for (ItemStack i : e.getPlayer().getInventory().getContents()) {
				if (i != null && (QualityArmoryVehicles.isVehicleByItem(i))) {
					if (shouldSend && !resourcepackReq.contains(e.getPlayer().getUniqueId())) {
						Main.foliaLib.getScheduler().runNextTick(task -> {
							sendResourcepack(e.getPlayer(), false);
						});
					}
					break;
				}
			}
		}
	}
}
	/*
	public static class MaterialStorage {

		private static List<MaterialStorage> store = new ArrayList<MaterialStorage>();

		public static MaterialStorage getMS(Material m, int d, int var) {
			return getMS(m, d, var, null);
		}

		public static MaterialStorage getMS(Material m, int d, int var, String extraValue) {
			return getMS(m, d, var, extraValue, null);
		}

		public static MaterialStorage getMS(Material m, int d, int var, String extraValue, String ev2) {
			for (MaterialStorage k : store) {
				if (matchesMaterials(k, m, d) && matchVarients(k, var) && matchHeads(k, extraValue, ev2))
					return k;
			}
			MaterialStorage mm = new MaterialStorage(m, d, var, extraValue, ev2);
			store.add(mm);
			return mm;
		}

		private static boolean matchesMaterials(MaterialStorage k, Material m, int d) {
			return (k.m == m && (k.d == d || k.d == -1));
		}

		public static boolean matchVarients(MaterialStorage k, int var) {
			return (k.varient == var || var == -1);
		}

		public static boolean matchHeads(MaterialStorage k, String ex1, String ex2) {
			boolean exb1 = (!k.hasSpecialValue() || k.hasSpecialValue2()
					|| (ex1 != null && (ex1.equals("-1") || k.getSpecialValue().equals(ex1))));
			boolean exb2 = (!k.hasSpecialValue2()
					|| (ex2 != null && (ex2.equals("-1") || k.getSpecialValue2().equals(ex2))));
			return exb1 && exb2;
		}

		@SuppressWarnings("deprecation")
		public static MaterialStorage getMS(ItemStack is) {
			Material skull_Compare = null;
			try {
				skull_Compare = Material.valueOf("SKULL_ITEM");
			} catch (Error | Exception e45) {
			}
			if (skull_Compare == null) {
				skull_Compare = Material.matchMaterial("PLAYER_HEAD");
			}
			return getMS(is.getType(), is.getDurability(), getVarient(is),
					is.getType() == skull_Compare ? ((SkullMeta) is.getItemMeta()).getOwner() : null);
		}

		private int d;
		private Material m;
		private int varient = 0;
		private String specialValues = null;
		private String specialValues2 = null;

		public int getData() {
			return d;
		}

		public boolean hasSpecialValue() {
			return specialValues != null;
		}

		public String getSpecialValue() {
			return specialValues;
		}

		public void setSpecialValue(String s) {
			this.specialValues = s;
		}

		public boolean hasSpecialValue2() {
			return specialValues2 != null;
		}

		public String getSpecialValue2() {
			return specialValues2;
		}

		public void setSpecialValue2(String s) {
			this.specialValues2 = s;
		}

		public Material getMat() {
			return m;
		}

		private MaterialStorage(Material m, int d) {
			this.m = m;
			this.d = d;
		}

		private MaterialStorage(Material m, int d, int var) {
			this(m, d, var, null);
		}

		private MaterialStorage(Material m, int d, int var, String extraData) {
			this(m, d, var, extraData, null);
		}

		private MaterialStorage(Material m, int d, int var, String extraData, String ed2) {
			this.m = m;
			this.d = d;
			this.varient = var;
			this.specialValues = extraData;
			this.specialValues2 = ed2;
		}

		public boolean isVarient() {
			return varient > 0;
		}

		public int getVarient() {
			return varient;
		}

		public static int getVarient(ItemStack is) {
			if (is != null)
				if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
					for (String lore : is.getItemMeta().getLore()) {
						if (lore.startsWith(S_ITEM_VARIENTS_NEW)) {
							try {
								int id = Integer.parseInt(lore.split(S_ITEM_VARIENTS_NEW)[1].trim());
								return id;
							} catch (Error | Exception e4) {
								e4.printStackTrace();
								return 0;
							}
						} 
					}
				}
			return 0;
		}
	}
}*/
