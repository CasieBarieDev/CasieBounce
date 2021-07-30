package me.casiebarie.casiebounce;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CBTabCompleter implements TabCompleter{
	public enum complete {reloadconfig, info, geterror}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		java.util.List<String> completions = new ArrayList<>();
		if(cmd.getName().equalsIgnoreCase("CB")) {
			if(args.length == 1 && sender.hasPermission("CB.admin")) {
				if(!args[0].equals("")) {
					for(complete modes : complete.values()) {
						if(modes.name().toUpperCase().startsWith(args[0].toUpperCase())) {completions.add(modes.name());}	
					}
				} else {for(complete modes : complete.values()) {completions.add(modes.name());}}
			}
		}
		if(Main.canBounce) {completions.remove("geterror");}
		Collections.sort(completions);
		return completions;
	}
}