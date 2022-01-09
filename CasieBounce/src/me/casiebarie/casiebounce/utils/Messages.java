package me.casiebarie.casiebounce.utils;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.casiebarie.casiebounce.Main;
import me.casiebarie.casiebounce.worldguard.WorldGuardManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

@SuppressWarnings("deprecation")
public class Messages implements Listener {
	private Main plugin;
	private WorldGuardManager wgM;
	public void send(CommandSender sender, String message) {if(sender != null) {sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));}}
	public ComponentBuilder startMSG() {return new ComponentBuilder("§9<§6-§9<§6-------------- ").append("§b§lCasieBounce §3§lv" + plugin.getDescription().getVersion()).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§9By: §eCasieBarie").create())).append("§6 --------------§9>§6-§9>").event((HoverEvent) null);}
	public String endMSG = "\n§9<§6-§9<§6----------------------------------------------§9>§6-§9>";
	public Messages(Main plugin, WorldGuardManager wgM) {
		this.plugin = plugin;
		this.wgM = wgM;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private String colorSetting(Object setting, String type) {
		if(setting.equals("DEFAULT")) {return "§8§oDEFAULT";}
		switch (type) {
		case "BOOLEAN": return (setting.toString().equals("true")) ? "§a§oTrue" : "§c§oFalse";
		case "INT": return "§d§o" + setting;
		case "OBJECT": return "§9§o" + setting;
		case "STRING": return "§f§o" + setting;
		default: return "";}
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
			if(sender instanceof Player) {getErrorMSG((Player) sender);}
			//CONSOLE MSG
			sender = Bukkit.getConsoleSender();
			send(sender, "&c-------------------- &4CASIEBOUNCE CONFIG ERRORS &c--------------------");
			for(int i = 0; i < errors.size(); i += 2) {
				if(errors.get(i).toString().equals("BounceBlocks_")) {send(sender, "&c- The block '" + errors.get(i + 1) + "' is not recognized!");
				} else {send(sender, "&c- The config section '" + errors.get(i) + "' with the value '" + errors.get(i + 1) + "' is not recognized!");}
			} send(sender, "&c-------------------------------------------------------------------");
		}
	}

	public void getErrorMSG(Player player) {
		player.spigot().sendMessage(new ComponentBuilder("§6[§bCasieBounce§6] ")
			.append("§cThere are errors in the config, check the console or type ")
			.append("§7/cb GetErrors").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a/cb GetErrors").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb GetErrors"))
			.append("§c for more info!").event((HoverEvent) null).event((ClickEvent) null)
			.create());
	}

	public void info(Player player) {
		player.spigot().sendMessage(startMSG()
			.append("\n§7Thanks for downloading CasieBounce!\n\n§3§lSETUP")
			.append("\n\n§a{With WorldGuard} \n§7Type '").append("§8/cb Info WorldGuard").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8/cb Info WorldGuard").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb Info WorldGuard")).append("§7'.").event((ClickEvent) null).event((HoverEvent) null)
			.append("\n\n§c{Without WorldGuard} \n§7Tweak the default settings in the config to your liking. Reload the config by typing '").append("§8/cb ReloadConfig").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8/cb ReloadConfig").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb ReloadConfig")).append("§7'.").event((ClickEvent) null).event((HoverEvent) null)
			.append("\n\n§7If you want information about the placeholders please type '").append("§8/cb Info PlaceholderAPI").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8/cb Info PlaceholderAPI").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb Info PlaceholderAPI")).append("§7'.").event((ClickEvent) null).event((HoverEvent) null)
			.append("\n\n§3§lLINKS ")
			.append("\n§e§nSPIGOT").event(new ClickEvent(Action.OPEN_URL, "https://www.spigotmc.org/resources/.90967")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://www.spigotmc.org/resources/.90967").create()))
			.append(" §7| ").event((ClickEvent) null).event((HoverEvent) null)
			.append("§b§nDISCORD").event(new ClickEvent(Action.OPEN_URL, "https://discord.gg/ZptCBHeHyg")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://discord.gg/ZptCBHeHyg").create()))
			.append(" §7| ").event((ClickEvent) null).event((HoverEvent) null)
			.append("§a§nWEBSITE").event(new ClickEvent(Action.OPEN_URL, "https://www.casiebariedev.ga/")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://www.casiebariedev.ga/").create()))
			.append(" §7| ").event((ClickEvent) null).event((HoverEvent) null)
			.append("§f§nGITHUB").event(new ClickEvent(Action.OPEN_URL, "https://github.com/CasieBarieDev/CasieBounce")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://github.com/CasieBarieDev/CasieBounce").create()))
			.append(endMSG).event((ClickEvent) null).event((HoverEvent) null)
			.create());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if(!plugin.canBounce && player.hasPermission("CB.admin")) {getErrorMSG(player);}
		if(plugin.wgError && player.hasPermission("CB.admin")) {send(player, "&6[&bCasieBounce&6] &cDue to a reload the WorldGuard flags are disabled. Please fully restart your server to fix it!");}
	}

	public void regionInfo(Player player) {
		if(!plugin.wgEnabled) {send(player, "&6[&bCasieBounce&6] &cWorldGuard is not enabled!"); return;}
		ArrayList<Object> regionSettings = wgM.getRegionSettings(player);
		String regionName = wgM.getRegionName(player);
		regionName = (regionName != null) ? regionName : "GLOBAL";
		player.spigot().sendMessage(startMSG()
			.append("\n§3§lREGION SETTINGS:\n")
			.append("§7Region | §f" + regionName)
			.append("\n\n§7Enabled | " + colorSetting(regionSettings.get(0), "BOOLEAN"))
			.append("\n§7BounceForce | " + colorSetting(regionSettings.get(1), "INT"))
			.append("\n§7BounceSound | " + colorSetting(regionSettings.get(2), "OBJECT"))
			.append("\n§7StopWhenCrouch | " + colorSetting(regionSettings.get(3), "BOOLEAN"))
			.append("\n§7FallDamage | " + colorSetting(regionSettings.get(4), "BOOLEAN"))
			.append("\n§7DeathMessage | " + colorSetting(regionSettings.get(5), "STRING"))
			.append("\n§7RequirePermission | " + colorSetting(regionSettings.get(6), "BOOLEAN"))
			.append("\n§7BounceBlocks | " + colorSetting(regionSettings.get(7), "OBJECT"))
			.append("\n§7IsBlockBlackList | " + colorSetting(regionSettings.get(8), "BOOLEAN"))
			.append(endMSG)
			.create());
	}

	public void worldGuardInfo(Player player) {
		player.spigot().sendMessage(startMSG()
			.append("\n§a{WorldGuard} §7In the config set 'WorldGuardFlags' to 'true'. Make sure to restart the server to let the plugin register the new flags to WorldGuard! Create a region and use the flag 'cb-enabled' allow to allow players to bounce in that region. Use the different flags for the settings in that region: 'cb-bounceforce' 'cb-deathmessage' 'cb-falldamage' 'cb-bouncesound' 'cb-bounceblocks' 'cb-stopewhencrouch' 'cb-requirepermission' 'cb-isblockblacklist'. Any unspecified settings will default to the setting set in the config. Please be aware that the plugin gets the FallDamage and DeathMessage settings from the region the player is landing in. This means that if a player starts bouncing in a region where FallDamage is Disabled and lands in a region where it is Enabled, the player still gets FallDamage. To get the settings of a region, stand in that region and type '")
			.append("§8/cb GetRegionSettings").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8/cb GetRegionSettings").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb GetRegionSettings")).append("§7'.").reset()
			.append(endMSG)
			.create());
	}

	public void placeholderAPIinfo(Player player) {
		player.spigot().sendMessage(startMSG()
			.append("\n§7If you have ").append("§8PlaceholderAPI").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8https://www.spigotmc.org/resources/placeholderapi.6245/").create())).event(new ClickEvent(Action.OPEN_URL, "https://www.spigotmc.org/resources/placeholderapi.6245/")).append(" §7installed you can view the bounce statistics of your server using placeholders:").event((ClickEvent) null).event((HoverEvent) null)
			.append("\n\n§7- §f%cb_total_<TYPE>% \n§7This will show the total bounces of the server.")
			.append("\n§7- §f%cb_player_<TYPE>% \n§7This will show the total bounces of the player.")
			.append("\n§7- §f%cb_totalregion_<REGION:WORLD>_<TYPE>% \n§7This will show the total bounces of a region.")
			.append("\n§7- §f%cb_playerregion_<REGION:WORLD>_<TYPE>% \n§7This will show the total bounces of a player in a specific region.")
			.append("\n§7<TYPE> = 'NUMBER-FULL' or 'NUMBER-ROUNDED' | Number rounded will turn 45100 to 45.1K")
			.append("\n\n§7- §f%cb_leaderboard_<POSITION>_<TYPE>% \n§7Get the value of a position in the leaderboard.")
			.append("\n§7- §f%cb_leaderboardregion_<REGION:WORLD>_<POSITION>_<TYPE>%\n§7Get the value of a position in the leaderboard from a specific region.")
			.append("\n§7<TYPE> = 'NAME', 'NUMBER-FULL', 'NUMBER-ROUNDED', 'BOTH-FULL' or 'BOTH-ROUNDED' | Both will for example turn into 'CasieBarie: 349'")
			.append("\n\n§4§lNOTE: §cThe data for this will be saved in '§4bounce_stats.db§c'. §c§nDO NOT DELETE!")
			.append(endMSG)
			.create());
	}
}