package me.casiebarie.casiebounce.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.casiebarie.casiebounce.Main;

public class Commands implements CommandExecutor, TabCompleter {
	private Main plugin;
	private ConfigManager cM;
	private Messages msg;
	private enum complete {ReloadConfig, Info, GetErrors, GetRegionSettings}
	private enum info {WorldGuard, PlaceholderAPI}
	public void onlyPlayer(CommandSender sender) {msg.send(sender, "&cThis command can only be executed by a player!");}
	public Commands(Main plugin, ConfigManager cM, Messages msg) {
		this.plugin = plugin;
		this.cM = cM;
		this.msg = msg;
		plugin.getCommand("CB").setExecutor(this);
		plugin.getCommand("CB").setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("CB") && args.length >= 1) {
			if(sender.hasPermission("CB.admin")) {
				switch (args[0].toUpperCase()) {
				case "RELOADCONFIG": cM.reloadConfig(sender); return true;
				case "GETREGIONSETTINGS": if(sender instanceof Player) {msg.regionInfo((Player) sender);} else {onlyPlayer(sender);} return true;
				case "INFO":
					if(sender instanceof Player) {
						if(args.length == 2) {
							switch (args[1].toUpperCase()) {
							case "WORLDGUARD": msg.worldGuardInfo((Player) sender); break;
							case "PLACEHOLDERAPI": msg.placeholderAPIinfo((Player) sender); break;
							default:msg.info((Player) sender); break;}
						} else {msg.info((Player) sender);}
					} else {onlyPlayer(sender);} return true;
				case "GETERRORS": cM.checkConfig(sender, true); return true;
				default: msg.send(sender, "&4USAGE: &c/cb <ReloadConfig/Info/GetErrors/GetRegionSettings> [WorldGuardFlags]"); return true;}
			}
		} return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		if(cmd.getName().equalsIgnoreCase("CB") && sender.hasPermission("CB.admin")) {
			if(args.length == 1) {
				if(!args[0].equals("")) {
					for(complete modes : complete.values()) {
						if(modes.name().toUpperCase().startsWith(args[0].toUpperCase())) {completions.add(modes.name());}
					}
				} else {for(complete modes : complete.values()) {completions.add(modes.name());}}
			} else if(args.length == 2) {
				if(args[0].toUpperCase().equals("INFO")) {
					if(!args[1].equals("")) {
						for(info infoMode : info.values()) {
							if(infoMode.name().toUpperCase().startsWith(args[1].toUpperCase())) {completions.add(infoMode.name());}
						}
					} else {for(info infoMode : info.values()) {completions.add(infoMode.name());}}
				}
			}
		}
		if(!(plugin.wgEnabled && sender instanceof Player)) {completions.remove("GetRegionSettings");}
		if(!(sender instanceof Player)) {completions.remove("Info"); completions.remove("WorldGuard");}
		if(plugin.canBounce) {completions.remove("GetErrors");}
		Collections.sort(completions);
		return completions;
	}
}