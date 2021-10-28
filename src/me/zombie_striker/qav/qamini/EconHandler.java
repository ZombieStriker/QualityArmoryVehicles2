package me.zombie_striker.qav.qamini;

import me.zombie_striker.qav.api.QualityArmoryVehicles;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconHandler {

	public static Economy econ;

	public static void setupEconomy() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
			return;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return;
		}
		econ = rsp.getProvider();
		QualityArmoryVehicles.getPlugin().getLogger().info("Hooked with Vault");
	}

	public static boolean hasEnough(int cost, Player player) {
		return !isVault() || (econ.getBalance(player) >= cost);
	}

	public static void pay(int cost, Player player) {
		try {
			me.zombie_striker.qg.handlers.EconHandler.pay(cost, player);
			return;
		} catch (Error | Exception ignored) {
		}
		if (isVault())
			econ.withdrawPlayer(player, cost);
	}

	public static void deposit(int cost, Player player) {
		if (isVault())
			econ.depositPlayer(player, cost);
	}

	public static boolean isVault() {
		return econ != null;
	}
}
