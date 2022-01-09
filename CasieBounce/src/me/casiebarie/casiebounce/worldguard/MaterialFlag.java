package me.casiebarie.casiebounce.worldguard;

import org.bukkit.Material;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

public class MaterialFlag extends Flag<String> {
	public MaterialFlag(String name) {super(name);}
	@Override
	public Object marshal(String o) {return o;}
	@Override
	public String parseInput(FlagContext context) throws InvalidFlagFormat {
		String input = context.getUserInput();
		if(input.equals("NONE")) {return input;}
		Material material;
		if(input.contains(":")) {material = Material.matchMaterial(input.split(":")[0]);
		} else {material = Material.matchMaterial(input);}
		if(material != null) {return input;
		} else {throw new InvalidFlagFormat("Unable to find the material! Please refer to https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html for valid ids");}
	}
	@Override
	public String unmarshal(Object o) {
		String input = o.toString();
		Material material;
		if(input.contains(":")) {material = Material.matchMaterial(input.split(":")[0]);
		} else {material = Material.matchMaterial(input);}
		if (material == null) {material = Material.matchMaterial(o.toString());}
		return input;
	}
}