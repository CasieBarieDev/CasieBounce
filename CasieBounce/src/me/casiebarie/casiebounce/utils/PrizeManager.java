package me.casiebarie.casiebounce.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.casiebarie.casiebounce.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class PrizeManager {
	private Main plugin;
	private Economy econ;
	private Permission perm;
	public PrizeManager(Main plugin) {this.plugin = plugin; if(plugin.vaultPresent()) {this.econ = Main.econ; this.perm = Main.perm;}}
	@SuppressWarnings("deprecation")
	public void givePrize(Player player, String prize) {
		String[] prizeSplit = prize.split("@");
		switch (prizeSplit[0]) {
		case "MONEY": if(plugin.vaultPresent()) {econ.depositPlayer(player, Double.parseDouble(prizeSplit[1]));} break;
		case "PERMISSION": if(plugin.vaultPresent()) {perm.playerAdd(player, prizeSplit[1]);} break;
		case "ITEM": 
			ItemStack itemStack;
			if(prizeSplit[1].contains(":")) {
				String[] itemSplit = prizeSplit[1].split(":");
				itemStack = new ItemStack(Material.matchMaterial(itemSplit[0]), 1, Byte.parseByte(itemSplit[1]));
			} else {itemStack = new ItemStack(Material.matchMaterial(prizeSplit[1]));}
			player.getInventory().addItem(itemStack); break;
		default: break;}
	}
}