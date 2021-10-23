package me.casiebarie.casiebounce;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import me.casiebarie.casiebounce.managers.ConfigManager;
import me.casiebarie.casiebounce.managers.WorldGuardManager;
import net.md_5.bungee.api.ChatColor;

public class Bounce implements Listener {
	private Main plugin;
	private ConfigManager configManager;
	private WorldGuardManager wgM;
	private ArrayList<UUID> isBouncing = new ArrayList<>();
	private ArrayList<UUID> canDie = new ArrayList<>();
	public Bounce(Main plugin, WorldGuardManager wgM, ConfigManager configManager) {
		this.plugin = plugin;
		this.wgM = wgM;
		this.configManager = configManager;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	private boolean canBounce(Player player) {
		if(!plugin.canBounce) {return false;}
		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		List<Boolean> blockValid = new ArrayList<Boolean>();
		ArrayList<Object> finalSettings = new ArrayList<>();
		finalSettings = getFinalSettings(player, configManager.getConfigSettings());
		if(finalSettings == null) {return false;}
		if(finalSettings.get(5).equals(true) && player.hasPermission("CB.bounce")) {return false;}
		if(player.isSneaking() && finalSettings.get(3).equals(true)) {return false;}
		@SuppressWarnings("unchecked")
		List<String> bounceBlocks = (List<String>) finalSettings.get(8);
		for(String blockName : bounceBlocks) {
			if(blockName.equalsIgnoreCase(block.getType().name())) {blockValid.add(true);
			} else {blockValid.add(false);}
		}
		if(blockValid.contains(true)) {if(finalSettings.get(7).equals(false)) {return true;
		} else if(finalSettings.get(7).equals(true)) {return true;}}
		return false;
	}
	
	private void goBounce(Player player) {
		isBouncing.remove(player.getUniqueId());
		ArrayList<Object> finalSettings = new ArrayList<>();
		finalSettings = getFinalSettings(player, configManager.getConfigSettings());
		if(finalSettings == null) {return;}
		player.setVelocity(new Vector(0, (double) finalSettings.get(1), 0));
		player.setFallDistance(0);
		try {player.playSound(player.getLocation(), Sound.valueOf((String) finalSettings.get(2)), 1f, 1f);
		} catch (Exception e) {e.printStackTrace();}
		isBouncing.add(player.getUniqueId());
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if(isBouncing.contains(player.getUniqueId())) {return;}
		if(canBounce(player)) {goBounce(player);}
	}
	
	@EventHandler
	public void onPlayerLand(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Entity entity = player;
		if(!isBouncing.contains(player.getUniqueId())) {return;}
		if(!entity.isOnGround()) {return;}
		if(player.getFallDistance() < 0) {return;}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {public void run() {
			if(canBounce(player)) {goBounce(player);
			} else {isBouncing.remove(player.getUniqueId());}
		}}, 1l);
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) {return;}
		if(e.getCause() != DamageCause.FALL) {return;}
		Player player = (Player) e.getEntity();
		canDie.remove(player.getUniqueId());
		if(!isBouncing.contains(player.getUniqueId())) {return;}
		ArrayList<Object> finalSettings = new ArrayList<>();
		finalSettings = getFinalSettings(player, configManager.getConfigSettings());
		if(finalSettings == null) {return;}
		if(finalSettings.get(4).equals(false)) {e.setCancelled(true);}
		else {canDie.add(player.getUniqueId());}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		ArrayList<Object> finalSettings = new ArrayList<>();
		finalSettings = getFinalSettings(player, configManager.getConfigSettings());
		if(finalSettings == null) {return;}
		if(player.getLastDamageCause().getCause() == DamageCause.FALL && canDie.contains(player.getUniqueId()) && !finalSettings.get(5).toString().equals("")) {
			String deathMessage = finalSettings.get(5).toString().replaceAll("%player%", player.getDisplayName());
			e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', deathMessage));
		} canDie.remove(player.getUniqueId());
	}
	
	private ArrayList<Object> getFinalSettings(Player player, ArrayList<Object> configSettings) {
		ArrayList<Object> regionSettings = new ArrayList<>();
		ArrayList<Object> finalSettings = new ArrayList<>();
		if(configSettings.get(0).equals(true) && plugin.wgEnabled) {
			regionSettings = wgM.getRegionSettings(player);
			if(regionSettings.isEmpty()) {return null;}
			if(regionSettings.get(0).equals(false)) {return null;}
			for(int i = 0; i <= 8; i++) {finalSettings.add(i, (regionSettings.get(i).equals("DEFAULT") ? configSettings.get(i) : regionSettings.get(i)));}
		} else {finalSettings = configSettings;}
		return finalSettings;
	}
}