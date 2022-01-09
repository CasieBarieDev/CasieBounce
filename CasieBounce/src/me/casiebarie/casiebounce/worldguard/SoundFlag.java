package me.casiebarie.casiebounce.worldguard;

import org.bukkit.Sound;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

public class SoundFlag extends Flag<Sound>{
	public SoundFlag(String name) {super(name);}
	@Override
	public Object marshal(Sound o) {return o.name();}
	@Override
	public Sound parseInput(FlagContext context) throws InvalidFlagFormat {
		Sound sound = Sound.valueOf(context.getUserInput());
		if(sound != null) {return sound;}
		else {throw new InvalidFlagFormat("Unable to find the sound! Please refer to https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html for valid ids");}
	}
	@Override
	public Sound unmarshal(Object o) {
		Sound sound = Sound.valueOf(o.toString());
		if(sound == null) {sound = Sound.valueOf(o.toString());}
		return sound;
	}
}