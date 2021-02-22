package me.zombie_striker.qav.qamini;

import me.zombie_striker.qav.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconHandler {

	public static Economy econ;

	public static boolean setupEconomy() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public static boolean hasEnough(int cost, Player player) {
		if (Main.minihandler == null) {
			try {
				return me.zombie_striker.qg.handlers.EconHandler.hasEnough(cost, player);
			} catch (Error | Exception e4) {
			}
		}
		QAMini.DEBUG("Check has enough: " + player.getName() + " isnull=" + (econ == null));
		return (econ.getBalance(player) >= cost);
	}

	public static void pay(int cost, Player player) {
		QAMini.DEBUG("Take money: " + player.getName() + " isnull=" + (econ == null));
		try {
			me.zombie_striker.qg.handlers.EconHandler.pay(cost, player);
			return;
		} catch (Error | Exception e4) {
		}
		econ.withdrawPlayer(player, cost);
	}

	public static void deposit(int cost, Player player) {
		QAMini.DEBUG("Deposit money: " + player.getName() + " isnull=" + (econ == null));
		try {
			me.zombie_striker.qg.handlers.EconHandler.deposit(cost, player);
			return;
		} catch (Error | Exception e4) {
		}
		econ.depositPlayer(player, cost);
	}
}
