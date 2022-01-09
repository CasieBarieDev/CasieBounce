package me.casiebarie.casiebounce.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import me.casiebarie.casiebounce.Main;
import me.casiebarie.casiebounce.sqlite.Database;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.minecraft.advancements.critereon.CriterionConditionPlayer.d;
@SuppressWarnings("unused")
public class PapiExpansion extends PlaceholderExpansion {
	private Main plugin;
	private Database db;
	public PapiExpansion(Main plugin, Database db) {this.plugin = plugin; this.db = db;}
	@Override
	public boolean persist() {return true;}
	@Override
	public boolean canRegister() {return true;}
	@Override
	public String getAuthor() {return plugin.getDescription().getAuthors().toString();}
	@Override
	public String getIdentifier() {return "cb";}
	@Override
	public String getVersion() {return plugin.getDescription().getVersion();}
	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		if(player == null) {return "";}
		Boolean rounded = null;
		String[] idSplit = identifier.split("_");
		String[] regionSplit;
		if(idSplit.length == 0) {return "";}
		switch (idSplit[0].toLowerCase()) {
		case "total": if(idSplit.length != 2) {return "";}
			rounded = (idSplit[1].equalsIgnoreCase("number-full") ? false : (idSplit[1].equalsIgnoreCase("number-rounded") ? true : null));
			return rounded(db.getBounces("TOTAL", null, null), rounded);
		case "player": if(idSplit.length != 2) {return "";}
			rounded = (idSplit[1].equalsIgnoreCase("number-full") ? false : (idSplit[1].equalsIgnoreCase("number-rounded") ? true : null));
			return rounded(db.getBounces("PLAYER", player.getUniqueId().toString(), null), rounded);
		case "totalregion": if(idSplit.length != 3) {return "";}
			rounded = (idSplit[2].equalsIgnoreCase("number-full") ? false : (idSplit[2].equalsIgnoreCase("number-rounded") ? true : null));
			regionSplit = idSplit[1].split(":");
			return rounded(db.getBounces("TOTALREGION", regionSplit[0], regionSplit[1]), rounded);
		case "playerregion": if(idSplit.length != 3) {return "";}
			regionSplit = idSplit[1].split(":");
			rounded = (idSplit[2].equalsIgnoreCase("number-full") ? false : (idSplit[2].equalsIgnoreCase("number-rounded") ? true : null));
			return rounded(db.getBounces("PLAYERREGION", regionSplit[0] + player.getUniqueId().toString(), regionSplit[1]), rounded);
		case "leaderboard":
			return getLeaderboardOutput(db.getLeaderboard("TOTAL", null, null), Integer.parseInt(idSplit[1]), idSplit[2]);
		case "leaderboardregion":
			regionSplit = idSplit[1].split(":");
			return getLeaderboardOutput(db.getLeaderboard("REGION", regionSplit[0], regionSplit[1]), Integer.parseInt(idSplit[2]), idSplit[3]);
		default: break; } return "";
	}

	private String getLeaderboardOutput(Map<String, ArrayList<String>> map, Integer position, String mode) {
		try {
			map = sortByValue(map);
			position = position-1;
			ArrayList<String> values = new ArrayList<>();
			switch (mode.toUpperCase()) {
			case "NAME": return map.get(map.keySet().toArray()[position]).get(0);
			case "NUMBER-FULL": return map.get(map.keySet().toArray()[position]).get(1);
			case "NUMBER-ROUNDED": return rounded(Integer.parseInt(map.get(map.keySet().toArray()[position]).get(1)), true);
			case "BOTH-FULL":
				values = map.get(map.keySet().toArray()[position]);
				return values.get(0) + ": " + values.get(1);
			case "BOTH-ROUNDED":
				values = map.get(map.keySet().toArray()[position]);
				return values.get(0) + ": " + rounded(Integer.parseInt(values.get(1)), true);
			default: break;}
			return "";
		} catch (ArrayIndexOutOfBoundsException e) {return "";}
	}

	private String rounded(Number number, Boolean rounded) {
		if(rounded == null) {return "";}
		if(rounded == false) {return number+"";}
		long numValue = number.longValue();
	    if(numValue < 1000) return "" + numValue;
	    int exp = (int) (Math.log(numValue) / Math.log(1000));
	    return String.format("%.1f%c", numValue / Math.pow(1000, exp), "kMGTPE".charAt(exp-1));
	}

	private Map<String, ArrayList<String>> sortByValue(Map<String, ArrayList<String>> map) {
		Map<String, ArrayList<String>> sortedMap = new LinkedHashMap<>();
		List<Map.Entry<String, ArrayList<String>>> list = new ArrayList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, ArrayList<String>>>() {
			public int compare(Map.Entry<String, ArrayList<String>> l, Map.Entry<String, ArrayList<String>> r) {
				return Integer.compare(Integer.valueOf(r.getValue().get(1)), Integer.valueOf(l.getValue().get(1)));
			}
		});
		for(Map.Entry<String, ArrayList<String>> entry : list) {sortedMap.put(entry.getKey(), entry.getValue());} 
		return sortedMap;
	}
}