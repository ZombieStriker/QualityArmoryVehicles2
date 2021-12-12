package me.zombie_striker.qav.easygui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ClickData {

	private final InventoryClickEvent raw;
	private final Inventory inv;
	private final Player clicker;
	private final ItemStack clickedItem;
	private final int slot;
	private final EasyGUI gui;

	public ClickData(InventoryClickEvent raw, EasyGUI gui){
		this.raw = raw;
		this.clickedItem = raw.getCurrentItem();
		this.clicker = (Player) raw.getWhoClicked();
		this.slot = raw.getSlot();
		this.inv = raw.getClickedInventory();
		this.gui = gui;
	}
	public int getSlot(){
		return slot;
	}
	public ItemStack getClickedItem(){
		return clickedItem;
	}
	public Player getClicker(){
		return clicker;
	}
	public InventoryClickEvent getRaw(){
		return raw;
	}
	public Inventory getInventory(){
		return inv;
	}
	public void cancelPickup(boolean b){
		raw.setCancelled(b);
	}
	public boolean isCanceled(){
		return raw.isCancelled();
	}
	public EasyGUI getClickedGUI(){
		return gui;
	}
}
