package me.casiebarie.casiebounce;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.md_5.bungee.api.ChatColor;

public class BounceLegacy implements Listener{
	public Main plugin;
	private ArrayList<UUID> isBouncing = new ArrayList<>();
	private ArrayList<UUID> canDie = new ArrayList<>();
	public BounceLegacy(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void BounceEvent(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		List<Boolean> isInConfig = new ArrayList<Boolean>();
		if(!Main.canBounce) {return;}
		if(CBCommand.requirePermission && !player.hasPermission("CB.bounce")){return;}
		if(player.isSneaking() && CBCommand.stopWhenCrouch){return;}
		for(int i = 0; i < CBCommand.bounceBlockTypes.size(); i++ ) {
			if(CBCommand.bounceBlockTypes.get(i).equalsIgnoreCase(block.getType().name()) && (CBCommand.bounceBlockData.get(i).equals(block.getData()) || CBCommand.bounceBlockData.get(i).equals(Byte.parseByte("-1")))) {isInConfig.add(true);
			} else {isInConfig.add(false);}
		}
		if(isInConfig.contains(true)) {
			if(!CBCommand.IsBlockBlacklist) {
				if(plugin.worldGuardPlugin == null) {goBounce(player);
				} else {
					for(String regionName : CBCommand.regionNames) {
						if(regionName.equalsIgnoreCase("GLOBAL")) {
							if(!CBCommand.IsRegionBlacklist) {goBounce(player);}
						} else {
							Map<String, ProtectedRegion> rgsMap = WGBukkit.getRegionManager(player.getWorld()).getRegions();
							Region1 : for(String configRegions : CBCommand.regionNames) {
								ProtectedRegion rg = rgsMap.get(configRegions);
								try {
									boolean inside = rg.contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
									if (inside && !CBCommand.IsRegionBlacklist) {goBounce(player);
									} else if (!inside && CBCommand.IsRegionBlacklist) {goBounce(player);
									} else {continue Region1;}
								} catch (Exception e2) {continue Region1;}
							}
						}
					}
				}
			} else {return;}
		} else {
			if(CBCommand.IsBlockBlacklist) {
				if(plugin.worldGuardPlugin == null) {goBounce(player);
				} else {
					for(String regionName : CBCommand.regionNames) {
						if(regionName.equalsIgnoreCase("GLOBAL")) {
							if(!CBCommand.IsRegionBlacklist) {goBounce(player);}
						} else {
							Map<String, ProtectedRegion> rgsMap = WGBukkit.getRegionManager(player.getWorld()).getRegions();
							Region2 : for(String configRegions : CBCommand.regionNames) {
								ProtectedRegion rg = rgsMap.get(configRegions);
								try {
									boolean inside = rg.contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
									if (inside && !CBCommand.IsRegionBlacklist) {goBounce(player);
									} else if (!inside && CBCommand.IsRegionBlacklist) {goBounce(player);
									} else {continue Region2;}
								} catch (Exception e2) {continue Region2;}
							}
						}
					}
				}
			} else {return;}
		} return;
	}
	public void goBounce(Player player) {
		player.setVelocity(new Vector(0,CBCommand.bounceForce,0));
		player.setFallDistance(0);
		if(!isBouncing.contains(player.getUniqueId())){
			try {player.playSound(player.getLocation(), Sound.valueOf(CBCommand.bounceSound), 1f, 1f);
			} catch (Exception e) {}
			isBouncing.add(player.getUniqueId());
		}
	}
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) {return;}
		Player player = (Player) e.getEntity();
		if(e.getCause() != DamageCause.FALL) {return;}
		canDie.remove(player.getUniqueId());
		if(isBouncing.contains(player.getUniqueId())) {
			canDie.add(player.getUniqueId());
			double fallDistance = player.getFallDistance() - (CBCommand.bounceForce * 10);
			if(CBCommand.fallDamage.equals("DISABLED")) {e.setCancelled(true);}
			if(CBCommand.fallDamage.equals("REDUCED")) {
				if(fallDistance <= 3) {e.setCancelled(true);
				} else {e.setDamage((fallDistance * 0.5) + 0.5);}
			}
		}
	}
	@EventHandler
	public void onPlayerLand(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if(isBouncing.contains(player.getUniqueId())) {
			if(player.getFallDistance() > 0) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {if(player.getFallDistance() == 0) {isBouncing.remove(player.getUniqueId());}}
				}, 1l);
			}
		}
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		if(e.getEntity().getLastDamageCause().getCause() == DamageCause.FALL && canDie.contains(player.getUniqueId()) && !CBCommand.deathMessage.equals("")) {
			String deathMessage = CBCommand.deathMessage.replaceAll("%player%", player.getDisplayName());
			e.setDeathMessage(ChatColor.translateAlternateColorCodes('&', deathMessage));
		}
		canDie.remove(player.getUniqueId());
	}
}