package me.casiebarie.casiebounce.utils;

import me.casiebarie.casiebounce.Main;
import me.casiebarie.casiebounce.worldguard.WorldGuardManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class Messages implements Listener {
	final Main plugin;
	final WorldGuardManager wgM;
	public void send(CommandSender sender, String message) {if(sender != null) {sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));}}
	private ComponentBuilder flags() {return new ComponentBuilder("").append("§d* cb-enabled\n§d* cb-bounceforce\n§d* cb-bouncesound\n§d* cb-bounceprize\n§d* cb-stopwhencrouch\n§d* cb-falldamage\n§d* cb-deathmessage\n§d* cb-requirepermission\n§d* cb-bounceblocks\n§d* cb-isblockblacklist");}
	public ComponentBuilder startMSG() {return new ComponentBuilder("§6<§9#§6> -------------> ").append("§b§lCasieBounce §3§lv" + plugin.getDescription().getVersion()).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§9By: §eCasieBarie").create())).append("§6 <------------- §6<§9#§6>").event((HoverEvent) null);}
	public String endMSG = "\n§6<§9#§6> ---------------------------------------------- <§9#§6>";
	public Messages(Main plugin, WorldGuardManager wgM) {this.plugin = plugin; this.wgM = wgM; Bukkit.getPluginManager().registerEvents(this, plugin);}

	private String fC(String msg, String color) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < msg.length(); i++){builder.append("§").append(color).append(msg.charAt(i));}
		return builder.toString();
	}

	private String colorSetting(Object setting, String type) {
		if(setting.equals("DEFAULT")) {return "§8§oDEFAULT";}
		switch (type) {
		case "BOOLEAN": return (setting.toString().equals("true")) ? "§a§oTrue" : "§c§oFalse";
		case "INT": return "§e§o" + setting;
		case "OBJECT": return "§9§o" + setting;
		case "STRING": return "§f§o" + setting;
		default: return "";}
	}

	public void errorMessage(CommandSender sender, ArrayList<String> errors, Boolean isCommand) {
		if(isCommand) {
			//PLAYER MSG
			send(sender, "&c-------------- &4CASIEBOUNCE CONFIG ERRORS &c--------------");
			for(String msg : errors) {send(sender, msg);}
			send(sender, "&c-----------------------------------------------------&r");
		} else {
			if(sender instanceof Player) {getErrorMSG((Player) sender);}
			//CONSOLE MSG
			sender = Bukkit.getConsoleSender();
			send(sender, "&c-------------------- &4CASIEBOUNCE CONFIG ERRORS &c--------------------");
			for(String msg : errors) {send(sender, msg);}
			send(sender, "&c-------------------------------------------------------------------");
		}
	}

	public void getErrorMSG(Player player) {player.spigot().sendMessage(new ComponentBuilder("§6[§bCasieBounce§6] ")
		.append(fC("There are errors in the config, check the console or type ", "c"))
		.append(fC("/cb GetErrors", "7")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a/cb GetErrors").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb GetErrors"))
		.append(fC(" for more info!", "c")).event((HoverEvent) null).event((ClickEvent) null)
		.create());
	}

	public void info(Player player) {player.spigot().sendMessage(startMSG()
		.append("\n" + fC("Thank you for downloading CasieBounce!", "d"))
		.append("\n\n" + fC("Please choose a topic and have fun!", "d"))
		.append("\n§d- (").append("§a§nSetup with WorldGuard").event(new ClickEvent(Action.RUN_COMMAND, "/cb Info SetupWithWorldGuard")).append("§d)").event((ClickEvent) null)
		.append("\n§d- (").append("§c§nSetup without WorldGuard").event(new ClickEvent(Action.RUN_COMMAND, "/cb Info SetupWithoutWorldGuard")).append("§d)").event((ClickEvent) null)
		.append("\n§d- (").append("§9§nPlaceholders").event(new ClickEvent(Action.RUN_COMMAND, "/cb Info Placeholders")).append("§d)").event((ClickEvent) null)
		.append("\n§d- (").append("§6§nPrizes").event(new ClickEvent(Action.RUN_COMMAND, "/cb Info Prizes")).append("§d)").event((ClickEvent) null)
		.append("\n\n§dMore info at:\n")
		.append("§b<§b§nDISCORD§b>").event(new ClickEvent(Action.OPEN_URL, "https://discord.gg/ZptCBHeHyg")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://discord.gg/ZptCBHeHyg").create()))
		.append(" ").event((ClickEvent) null).event((HoverEvent) null)
		.append("§a<§a§nWEBSITE§a>").event(new ClickEvent(Action.OPEN_URL, "https://www.casiebariedev.ga/")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://www.casiebariedev.ga/").create()))
		.append(" ").event((ClickEvent) null).event((HoverEvent) null)
		.append("§f<§f§nGITHUB§f>").event(new ClickEvent(Action.OPEN_URL, "https://github.com/CasieBarieDev/CasieBounce")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://github.com/CasieBarieDev/CasieBounce").create()))
		.append(" ").event((ClickEvent) null).event((HoverEvent) null)
		.append("§e<§e§nSPIGOT§e>").event(new ClickEvent(Action.OPEN_URL, "https://www.spigotmc.org/resources/.90967")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://www.spigotmc.org/resources/.90967").create()))
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
			.append("§5Region | §f" + regionName)
			.append("\n\n§5Enabled | " + colorSetting(regionSettings.get(0), "BOOLEAN"))
			.append("\n§5BounceForce | " + colorSetting(regionSettings.get(1), "INT"))
			.append("\n§5BounceSound | " + colorSetting(regionSettings.get(2), "OBJECT"))
			.append("\n§5BouncePrize | " + colorSetting(regionSettings.get(3), "STRING"))
			.append("\n§5StopWhenCrouch | " + colorSetting(regionSettings.get(4), "BOOLEAN"))
			.append("\n§5FallDamage | " + colorSetting(regionSettings.get(5), "BOOLEAN"))
			.append("\n§5DeathMessage | " + colorSetting(regionSettings.get(6), "STRING"))
			.append("\n§5RequirePermission | " + colorSetting(regionSettings.get(7), "BOOLEAN"))
			.append("\n§5BounceBlocks | " + colorSetting(regionSettings.get(8), "OBJECT"))
			.append("\n§5IsBlockBlackList | " + colorSetting(regionSettings.get(9), "BOOLEAN"))
			.append(endMSG).create());
	}

	public void setupWithWorldGuard(Player player) {player.spigot().sendMessage(startMSG()
		.append("\n" + fC("First of all its important to download ", "d")).append("§9§nWorldGuard").event(new ClickEvent(Action.OPEN_URL, "https://dev.bukkit.org/projects/worldguard")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://dev.bukkit.org/projects/worldguard").create())).append("§d.").event((ClickEvent) null).event((HoverEvent) null)
		.append("\n" + fC("If you have done that you have to set the setting 'WorldGuardFlags' in the config to ", "d") + "§atrue" + fC(". After that you have to ", "d") + "§4RESTART" + fC("your server to let CasieBounce register the flags.", "d"))
		.append("\n\n" + fC("Now you can edit each setting in a region using the ", "d")).append("§e§nFlags").event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, flags().create())).append(fC(". Any unspecified settings will default to the config.", "d")).event((HoverEvent) null)
		.append(endMSG).create());
	}

	public void setupWithoutWorldGuard(Player player) {player.spigot().sendMessage(startMSG()
		.append("\n" + fC("To go bouncing you first have to edit some settings in the config. After you've done that reload the config by typing '", "d"))
		.append(fC("/cb ReloadConfig", "5")).event(new ClickEvent(Action.SUGGEST_COMMAND, "/cb ReloadConfig")).append("§d`. ").event((ClickEvent) null)
		.append(fC("After that you can start right away with bouncing. Have Fun!", "d"))
		.append(endMSG).create());
	}

	public void placeholderAPIinfo(Player player) {player.spigot().sendMessage(startMSG()
		.append("\n" + fC("When you have ", "d")).append("§9§nPlaceholderAPI").event(new ClickEvent(Action.OPEN_URL, "https://www.spigotmc.org/resources/placeholderapi.6245")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://www.spigotmc.org/resources/placeholderapi.6245/").create())).append(fC(" you can show the bouncestatistics using the placeholders:", "d")).event((ClickEvent) null).event((HoverEvent) null)
		.append("\n\n" + fC("- %cb_total_<TYPE>% ", "5§l") + fC("| This will show the total bounces of the server.", "d"))
		.append("\n" + fC("- %cb_player_<TYPE>% ", "5§l") + fC("| This will show the total bounces of the player.", "d"))
		.append("\n" + fC("- %cb_totalregion_<REGION:WORLD>_<TYPE>% ", "5§l") + fC("| This will show the total bounces of a region.", "d"))
		.append("\n" + fC("- %cb_playerregion_<REGION:WORLD>_<TYPE>% ", "5§l") + fC("| This will show the total bounces of a player in a specific region.", "d"))
		.append("\n" + fC("<TYPE> = 'NUMBER-FULL' or 'NUMBER-ROUNDED' ", "5§l") + fC("| Number rounded will turn 45100 to 45.1K", "d"))
		.append("\n\n" + fC("- %cb_leaderboard_<POSITION>_<TYPE>% ", "5§l") + fC("| Get the value of a position in the leaderboard.", "d"))
		.append("\n" + fC("- %cb_leaderboardregion_<REGION:WORLD>_<POSITION>_<TYPE>% ", "5§l") + fC("| Get the value of a position in the leaderboard from a specific region.", "d"))
		.append("\n" + fC("<TYPE> = 'NAME', 'NUMBER-FULL', 'NUMBER-ROUNDED', 'BOTH-FULL' or 'BOTH-ROUNDED' ", "5§l") + fC("| Both will for example turn into 'CasieBarie: 349'", "d"))
		.append(endMSG).create());
	}

	public void prizeInfo(Player player) {player.spigot().sendMessage(startMSG()
		.append(fC("You can give a player a prize for each time they bounce. This can be money, item, permission or a command. To give a player money or a permission you have to make sure you have ", "d"))
		.append("§9§nVault").event(new ClickEvent(Action.OPEN_URL, "https://www.spigotmc.org/resources/vault.34315/")).event(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7https://www.spigotmc.org/resources/vault.34315/").create()))
		.append(fC(" installed. The value format is: ", "d") + fC("<TYPE>@<VALUE>", "5") + fC(" Command variables are: %player%, %X%, %Y%, %Z%, %PITCH%, %YAW%. The output of Plugin commands are disabled, vanilla commands can still spam the console! You can disable this by changing 'silent-commandblock-console' to true in the Spigot.yml. An error will show up if the format is wrong. \n\n", "5") + fC("<TYPE> = MONEY, ITEM or PERMISSION", "d")).event((ClickEvent) null).event((HoverEvent) null)
		.append(endMSG).create());
	}
}