package me.casiebarie.casiebounce.other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.casiebarie.casiebounce.Main;

public class TabComplete implements TabCompleter {
	private enum complete {ReloadConfig, Info, GetErrors, GetRegionSettings}
	private Main plugin;
	private String wGF = "WorldGuard";
	public TabComplete(Main plugin) {this.plugin = plugin; plugin.getCommand("CB").setTabCompleter(this);}
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
						if(wGF.toUpperCase().startsWith(args[1].toUpperCase())) {completions.add(wGF);}
					} else {completions.add(wGF);}
				}
			}
		}
		if(!plugin.wgEnabled || !(sender instanceof Player)) {completions.remove("GetRegionSettings");}
		if(!(sender instanceof Player)) {completions.remove("Info"); completions.remove("WorldGuard");}
		if(plugin.canBounce) {completions.remove("GetErrors");}
		Collections.sort(completions);
		return completions;
	}
}