package me.casiebarie.casiebounce;

import me.casiebarie.casiebounce.database.Database;
import me.casiebarie.casiebounce.utils.ConfigManager;
import me.casiebarie.casiebounce.utils.PrizeManager;
import me.casiebarie.casiebounce.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Bounce implements Listener {
	final Main plugin; final Utils utils; final WorldGuardManager wgM; final ConfigManager cM; final PrizeManager pM; final Database db;
	final ArrayList<Player> isBouncing = new ArrayList<>(), wasBouncing = new ArrayList<>(), canDie = new ArrayList<>();
	public Bounce(Main plugin, Utils utils, WorldGuardManager wgM, ConfigManager cM, PrizeManager pM) {
		this.plugin = plugin; this.utils = utils; this.wgM = wgM; this.cM = cM; this.pM = pM; this.db = plugin.getDatabase();
		Bukkit.getPluginManager().registerEvents(this, plugin); repeat();
	}

	private void repeat() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(!((Entity) player).isOnGround() && player.getFallDistance() > 0) {wasBouncing(player); continue;}
				if(canDie.contains(player) || isBouncing.contains(player)) {continue;}
				if(canBounce(player)) {goBounce(player, (plugin.wgEnabled) ? wgM.getRegionName(player) : "Global");}
			}
		}, 0, 1L);
	}

	private void goBounce(Player player, String region) {
		isBouncing.remove(player);
		ArrayList<Object> finalSettings;
		finalSettings = getFinalSettings(player, cM.getConfigSettings());
		if(finalSettings == null) {return;}
		player.setVelocity(new Vector(0, (double) finalSettings.get(1), 0));
		player.setFallDistance(0);
		if(!finalSettings.get(2).equals("NONE")) {player.playSound(player.getLocation(), finalSettings.get(2).toString(), 1f, 1f);}
		pM.givePrize(player, finalSettings.get(3).toString());
		plugin.mBounces += 1;
		db.addBounces(region, player.getUniqueId(), player.getName(), player.getWorld(), 1);
		isBouncing.add(player);
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	private Boolean canBounce(Player player) {
		if(!plugin.canBounce) {return false;}
		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		List<Boolean> blockValid = new ArrayList<>();
		ArrayList<Object> finalSettings;
		finalSettings = getFinalSettings(player, cM.getConfigSettings());
		if((finalSettings == null)) {return false;}
		if(finalSettings.get(7).equals(true)) {if(!player.hasPermission("CB.bounce") && !player.hasPermission("CB.bounce." + wgM.getRegionName(player))) {return false;}}
		if(player.isSneaking() && finalSettings.get(4).equals(true)) {return false;}
		List<String> bounceBlocks = (List<String>) finalSettings.get(8);
		for(String blockName : bounceBlocks) {
			if(blockName.contains(":")) {
				String[] blockSplit = blockName.split(":");
				if(blockSplit[0].equalsIgnoreCase(block.getType().name()) && blockSplit[1].equals(String.valueOf(block.getData()))) {blockValid.add(true);
				} else {blockValid.add(false);}
			} else if (blockName.equalsIgnoreCase(block.getType().name())) {blockValid.add(true);
			} else {blockValid.add(false);}
		}
		if(blockValid.contains(true)) {if(finalSettings.get(9).equals(false)) {return true;
		} else return finalSettings.get(9).equals(true);}
		return false;
	}

	private ArrayList<Object> getFinalSettings(Player player, ArrayList<Object> configSettings) {
		ArrayList<Object> regionSettings;
		ArrayList<Object> finalSettings = new ArrayList<>();
		if(configSettings.get(0).equals(true) && plugin.wgEnabled) {
			regionSettings = wgM.getRegionSettings(player);
			if(regionSettings.isEmpty() || regionSettings.get(0).equals(false)) {return null;}
			for(int i = 0; i <= 9; i++) {finalSettings.add(i, (regionSettings.get(i).equals("DEFAULT") ? configSettings.get(i) : regionSettings.get(i)));}
		} else {finalSettings = configSettings;}
		return finalSettings;
	}

	private void wasBouncing(Player player) {
		isBouncing.remove(player);
		wasBouncing.add(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {wasBouncing.remove(player); canDie.remove(player);}, 5L);
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player) || (e.getCause() != DamageCause.FALL)) {return;}
		Player player = (Player) e.getEntity();
		canDie.remove(player);
		if(!wasBouncing.contains(player)) {return;}
		ArrayList<Object> finalSettings;
		finalSettings = getFinalSettings(player, cM.getConfigSettings());
		if(finalSettings == null) {return;}
		if(finalSettings.get(5).equals(false)) {e.setCancelled(true);}
		else {canDie.add(player);}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		ArrayList<Object> finalSettings;
		finalSettings = getFinalSettings(player, cM.getConfigSettings());
		if(finalSettings == null) {return;}
		if(player.getLastDamageCause().getCause() == DamageCause.FALL && canDie.contains(player) && !finalSettings.get(6).toString().equals("")) {
			String deathMessage = finalSettings.get(6).toString().replaceAll("%player%", player.getDisplayName());
			e.setDeathMessage(utils.hex(deathMessage));
		} canDie.remove(player);
	}
}