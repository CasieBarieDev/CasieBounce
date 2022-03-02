package me.casiebarie.casiebounce.utils;

import me.casiebarie.casiebounce.Main;
import me.casiebarie.casiebounce.database.Database;
import me.casiebarie.casiebounce.database.ResetData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
	final Main plugin;
	final Database db;
	final ConfigManager cM;
	final Messages msg;
	final ResetData rD;
	private enum complete {ReloadConfig, Info, GetErrors, GetRegionSettings, ResetData}
	private enum resetdata {RegionPlayer, Region, Player, All}
	public void onlyPlayer(CommandSender sender) {msg.send(sender, "&cThis command can only be executed by a player!");}
	public Commands(Main plugin, Database db, ConfigManager cM, Messages msg, ResetData rD) {
		this.plugin = plugin; this.db = db; this.cM = cM; this.msg = msg; this.rD = rD;
		plugin.getCommand("CB").setExecutor(this); plugin.getCommand("CB").setTabCompleter(this);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("CB") && args.length >= 1) {
			if(sender.hasPermission("CB.admin")) {
				switch (args[0].toUpperCase()) {
				case "RELOADCONFIG": cM.reloadConfig(sender); return true;
				case "GETREGIONSETTINGS": if(sender instanceof Player) {msg.regionInfo((Player) sender);} else {onlyPlayer(sender);} return true;
				case "INFO":
					if(sender instanceof Player) {
						if(args.length == 2) {
							switch (args[1].toUpperCase()) {
							case "SETUPWITHWORLDGUARD": msg.setupWithWorldGuard((Player) sender); break;
							case "SETUPWITHOUTWORLDGUARD": msg.setupWithoutWorldGuard((Player) sender); break;
							case "PLACEHOLDERS": msg.placeholderAPIinfo((Player) sender); break;
							case "PRIZES": msg.prizeInfo((Player) sender); break;
							default: msg.info((Player) sender); break;}
						} else {msg.info((Player) sender);}
					} else {onlyPlayer(sender);} return true;
				case "GETERRORS": cM.checkConfig(sender, true); return true;
				case "RESETDATA":
					switch (args[1].toUpperCase()) {
					case "REGIONPLAYER":
						if(args.length == 4) {rD.createMessage(sender, "REGIONPLAYER", args[2] + "@" + args[3]);
						} else {msg.send(sender, "&4USAGE: &c/cb ResetData RegionPlayer <Region:World> <PlayerName(UUID)>");} break;
					case "REGION":
						if(args.length == 3) {rD.createMessage(sender, "REGION", args[2]);
						} else {msg.send(sender, "&4USAGE: &c/cb ResetData Region <Region:World>");} break;
					case "PLAYER":
						if(args.length == 3) {rD.createMessage(sender, "PLAYER", args[2]);
						} else {msg.send(sender, "&4USAGE: &c/cb ResetData Player <PlayerName(UUID)>");} break;
					case "ALL": rD.createMessage(sender, "ALL", "(ALL:ALL)"); break;
					default: msg.send(sender, "&4USAGE: &c/cb ResetData <RegionPlayer/Region/Player/All>");} return true;
				case "CONFIRM": rD.confirm(sender, !(sender instanceof Player)); break;
				default: msg.send(sender, "&4USAGE: &c/cb <ReloadConfig/Info/GetErrors/GetRegionSettings/ResetData>");} return true;
			}
		} return false;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
		List<String> completions = new ArrayList<>();
		if(cmd.getName().equalsIgnoreCase("CB") && sender.hasPermission("CB.admin")) {
			if(args.length == 1) {
				if(!args[0].equals("")) {
					for(complete mode : complete.values()) {
						if(mode.name().toUpperCase().startsWith(args[0].toUpperCase())) {completions.add(mode.name());}
					}
				} else {for(complete mode : complete.values()) {completions.add(mode.name());}}
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("ResetData")) {
					if(!args[1].equals("")) {
						for(resetdata mode : resetdata.values()) {
							if(mode.name().toUpperCase().startsWith(args[1].toUpperCase())) {completions.add(mode.name());}
						}
					} else {for(resetdata mode : resetdata.values()) {completions.add(mode.name());}}
				}
			} else if(args.length == 3) {
				List<String> dataBase = new ArrayList<>();
				switch (args[1].toUpperCase()) {
					case "REGIONPLAYER":
					case "REGION": dataBase.addAll(db.getCompletions("REGION", null)); break;
					case "PLAYER": dataBase.addAll(db.getCompletions("PLAYER", null)); break;
					default: break;}
				if(!args[2].equals("")) {for(String data : dataBase) {if(data.toUpperCase().startsWith(args[2].toUpperCase())) {completions.add(data);}}
				} else {completions.addAll(dataBase);}
			} else if(args.length == 4) {
				List<String> dataBase = new ArrayList<>();
				if(args[1].equalsIgnoreCase("RegionPlayer")) {dataBase.addAll(db.getCompletions("REGIONPLAYER", args[2]));}
				if(!args[3].equals("")) {for(String data : dataBase) {if(data.toUpperCase().startsWith(args[3].toUpperCase())) {completions.add(data);}}
				} else {completions.addAll(dataBase);}
			}
		}
		if(!(plugin.wgEnabled && sender instanceof Player)) {completions.remove("GetRegionSettings");}
		if(!(sender instanceof Player)) {completions.remove("Info"); completions.remove("WorldGuard");}
		if(plugin.canBounce) {completions.remove("GetErrors");}
		Collections.sort(completions);
		return completions;
	}
}