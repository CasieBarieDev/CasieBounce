package me.casiebarie.casiebounce.other;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.casiebarie.casiebounce.Main;
import me.casiebarie.casiebounce.managers.WorldGuardManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class Messages implements Listener {
	private Main plugin;
	private WorldGuardManager regionManager;
	public void send(CommandSender sender, String message) {if(sender != null) {sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));}}
	public Messages(Main plugin, WorldGuardManager regionManager) {
		this.plugin = plugin;
		this.regionManager = regionManager;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private String colorSetting(Object setting, String type) {
		if(setting.equals("DEFAULT")) {return "§8§oDEFAULT";}
		if(type.equals("BOOLEAN")) {return (setting.toString().equals("true")) ? "§a§oTrue" : "§c§oFalse";}
		if(type.equals("INT")) {return "§d§o" + setting;}
		if(type.equals("OBJECT")) {return "§9§o" + setting;}
		if(type.equals("STRING")) {return "§f§o" + setting;}
		return "";
	}

	public void errorMessage(CommandSender sender, ArrayList<Object> errors, Boolean isCommand) {
		if(isCommand) {
			//PLAYER MSG
			send(sender, "&c-------------- &4CASIEBOUNCE CONFIG ERRORS &c--------------");
			for(int i = 0; i < errors.size(); i += 2) {
				if(errors.get(i).toString().equals("BounceBlocks_")) {send(sender, "&c- The block '" + errors.get(i + 1) + "' is not recognized!");
				} else {send(sender, "&c- The config section '" + errors.get(i) + "' with the value '" + errors.get(i + 1) + "' is not recognized!");}
			} send(sender, "&c-----------------------------------------------------&r");
		} else {
			if(sender instanceof Player) {getErrorMSG(sender);}
			//CONSOLE MSG
			sender = Bukkit.getConsoleSender();
			send(sender, "&c-------------------- &4CASIEBOUNCE CONFIG ERRORS &c--------------------");
			for(int i = 0; i < errors.size(); i += 2) {
				if(errors.get(i).toString().equals("BounceBlocks_")) {send(sender, "&c- The block '" + errors.get(i + 1) + "' is not recognized!");
				} else {send(sender, "&c- The config section '" + errors.get(i) + "' with the value '" + errors.get(i + 1) + "' is not recognized!");}
			} send(sender, "&c-------------------------------------------------------------------");
		}
	}

	@SuppressWarnings("deprecation")
	public void getErrorMSG(CommandSender sender) {
		sender.spigot().sendMessage(new ComponentBuilder("§6[§bCasieBounce§6]")
				.append("§cThere are errors in the config, check the console or type ")
				.append("§7/cb GetErrors").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a/cb GetErrors").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb GetErrors"))
				.append("§c for more info!").event((HoverEvent) null).event((ClickEvent) null)
				.create());
	}

	@SuppressWarnings("deprecation")
	public void info(CommandSender sender) {
		sender.spigot().sendMessage(new ComponentBuilder("§9<§6-§9<§6-------------- ").append("§b§lCasieBounce §3§lv" + plugin.getDescription().getVersion()).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§9By: §eCasieBarie").create())).append("§6 --------------§9>§6-§9>").event((HoverEvent) null)
				.append("\n§7Thanks for downloading CasieBounce!\n\n§3§lSETUP")
				.append("\n§a{With WorldGuard} §7Type '").append("§8/cb Info WorldGuard").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8/cb Info WorldGuard").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb Info WorldGuard")).append("§7'.").event((ClickEvent) null).event((HoverEvent) null)
				.append("\n§c{Without WorldGuard} §7Tweak the default settings in the config to your liking. Reload the config by typing '").append("§8/cb ReloadConfig").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8/cb ReloadConfig").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb ReloadConfig")).append("§7'.").event((ClickEvent) null).event((HoverEvent) null)
				.append("\n\n§3§lLINKS ")
				.append("\n§e§nSPIGOT").event(new ClickEvent(Action.OPEN_URL, "https://www.spigotmc.org/resources/.90967")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://www.spigotmc.org/resources/.90967").create()))
				.append(" §7| ").event((ClickEvent) null).event((HoverEvent) null)
				.append("§b§nDISCORD").event(new ClickEvent(Action.OPEN_URL, "https://discord.gg/ZptCBHeHyg")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://discord.gg/ZptCBHeHyg").create()))
				.append(" §7| ").event((ClickEvent) null).event((HoverEvent) null)
				.append("§a§nWEBSITE").event(new ClickEvent(Action.OPEN_URL, "https://www.casiebariedev.ga/")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://www.casiebariedev.ga/").create()))
				.append(" §7| ").event((ClickEvent) null).event((HoverEvent) null)
				.append("§f§nGITHUB").event(new ClickEvent(Action.OPEN_URL, "https://github.com/CasieBarieDev/CasieBounce")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://github.com/CasieBarieDev/CasieBounce").create()))
				.append("\n§9<§6-§9<§6----------------------------------------------§9>§6-§9>").event((HoverEvent) null).event((ClickEvent) null)
				.create());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if(!plugin.canBounce && player.hasPermission("CB.admin")) {getErrorMSG(player);}
		if(plugin.wgError && player.hasPermission("CB.admin")) {send(player, "&6[&bCasieBounce&6] &cDue to a reload the WorldGuard flags are disabled. Please fully restart your server to fix it!");}
	}

	@SuppressWarnings("deprecation")
	public void regionInfo(CommandSender sender) {
		if(!plugin.wgEnabled) {send(sender, "&6[&bCasieBounce&6] &cWorldGuard is not enabled!"); return;}
		ArrayList<Object> regionSettings = regionManager.getRegionSettings((Player) sender);
		String regionName = regionManager.getRegionName((Player) sender);
		regionName = (regionName != null) ? regionName : "GLOBAL";
		sender.spigot().sendMessage(new ComponentBuilder("§9<§6-§9<§6-------------- ").append("§b§lCasieBounce §3§lv" + plugin.getDescription().getVersion()).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§9By: §eCasieBarie").create())).append("§6 --------------§9>§6-§9>").event((HoverEvent) null)
				.append("\n§3§lREGION SETTINGS:\n")
				.append("§7Region | §f" + regionName)
				.append("\n\n§7Enabled | " + colorSetting(regionSettings.get(0), "BOOLEAN"))
				.append("\n§7BounceForce | " + colorSetting(regionSettings.get(1), "INT"))
				.append("\n§7BounceSound | " + colorSetting(regionSettings.get(2), "OBJECT"))
				.append("\n§7StopWhenCrouch | " + colorSetting(regionSettings.get(3), "BOOLEAN"))
				.append("\n§7FallDamage | " + colorSetting(regionSettings.get(4), "BOOLEAN"))
				.append("\n§7DeathMessage | " + colorSetting(regionSettings.get(5), "STRING"))
				.append("\n§7RequirePermission | " + colorSetting(regionSettings.get(6), "BOOLEAN"))
				.append("\n§7BounceBlocks | " + colorSetting(regionSettings.get(8), "OBJECT"))
				.append("\n§7IsBlockBlackList | " + colorSetting(regionSettings.get(7), "BOOLEAN"))
				.append("\n\n§9<§6-§9<§6----------------------------------------------§9>§6-§9>")
				.create());
	}

	@SuppressWarnings("deprecation")
	public void WorldGuardInfo(CommandSender sender) {
		sender.spigot().sendMessage(new ComponentBuilder("§9<§6-§9<§6-------------- ").append("§b§lCasieBounce §3§lv" + plugin.getDescription().getVersion()).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§9By: §eCasieBarie").create())).append("§6 --------------§9>§6-§9>").event((HoverEvent) null)
				.append("\n§a{WorldGuard} §7In the config set 'WorldGuardFlags' to 'true'. Make sure to restart the server to let the plugin register the new flags to WorldGuard! Create a region and use the flag 'cb-enabled' allow to allow players to bounce in that region. Use the different flags for the settings in that region: 'cb-bounceforce' 'cb-deathmessage' 'cb-falldamage' 'cb-bouncesound' 'cb-bounceblocks' 'cb-stopewhencrouch' 'cb-requirepermission' 'cb-isblockblacklist'. Any unspecified settings will default to the setting set in the config. Please be aware that the plugin gets the FallDamage and DeathMessage settings from the region the player is landing in. This means that if a player starts bouncing in a region where FallDamage is Disabled and lands in a region where it is Enabled, the player still gets FallDamage. To get the settings of a region, stand in that region and type '")
				.append("§8/cb GetRegionSettings").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8/cb GetRegionSettings").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb GetRegionSettings")).append("§7'.").event((ClickEvent) null).event((HoverEvent) null)
				.append("\n§9<§6-§9<§6----------------------------------------------§9>§6-§9>")
				.create());
	}
}