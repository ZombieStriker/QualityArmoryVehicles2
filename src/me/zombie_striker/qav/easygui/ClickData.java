package me.zombie_striker.qav.easygui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ClickData {

	private InventoryClickEvent raw;
	private Inventory inv;
	private Player clicker;
	private ItemStack clickedItem;
	private int slot;
	private EasyGUI gui;

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
