package org.fukkit.utils;

import java.util.NoSuchElementException;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

import io.flex.commons.Severity;
import io.flex.commons.console.Console;
import io.flex.commons.utils.StringUtils;

public class VersionUtils {
	
	public static Material material(String... values) {
		
		Material material = null;
		
		for (String string : values)
			try {
				
				/**
				 * TODO Remove "LEGACY_" from all VersionUtils.material usage,
				 * I am forcing the test for LEGACY materials AFTER normal ones.
				 * Some things depend on the logic to be this way unfortunately.
				 */
				if (string.startsWith("LEGACY_"))
					continue;
				
				material = Material.valueOf(string.toUpperCase());
				
			} catch (NoSuchElementException | IllegalArgumentException e) {}
		
		if (material == null)
			for (String string : values)
				try {
					material = Material.valueOf(("LEGACY_" + string).toUpperCase());
				} catch (NoSuchElementException | IllegalArgumentException e) {}
		
		if (material == null)
			Console.log("Version", Severity.NOTICE, new NoSuchElementException("Tried " + StringUtils.join(values, ", ") + " with no result. Is your version up to date?"));
		
		return material;
		
	}

	public static Sound sound(String... values) {
		
		Sound sound = null;
		int i = 0;
		
		while (sound == null) {
			
			if (i >= values.length)
				break;
			
			try {
				sound = Sound.valueOf(values[i]);
			} catch (NoSuchElementException | IllegalArgumentException e) {
				i++;
			}
			
			if (sound != null)
				return sound;
			
		}
		
		Console.log("Version", Severity.NOTICE, new NoSuchElementException("Tried " + StringUtils.join(values, ", ") + " with no result. Is your version up to date?"));
		
		return null;
		
	}

	public static Effect effect(String... values) {
		
		Effect effect = null;
		int i = 0;
		
		while (effect == null) {
			
			if (i >= values.length)
				break;
			
			try {
				effect = Effect.valueOf(values[i]);
			} catch (NoSuchElementException | IllegalArgumentException e) {
				i++;
			}
			
			if (effect != null)
				return effect;
			
		}
		
		Console.log("Version", Severity.NOTICE, new NoSuchElementException("Tried " + StringUtils.join(values, ", ") + " with no result. Is your version up to date?"));
		
		return null;
		
	}

	public static EntityType entity(String... values) {
		
		EntityType entity = null;
		int i = 0;
		
		while (entity == null) {
			
			if (i >= values.length)
				break;
			
			try {
				entity = EntityType.valueOf(values[i]);
			} catch (NoSuchElementException | IllegalArgumentException e) {
				i++;
			}
			
			if (entity != null)
				return entity;
			
		}
		
		Console.log("Version", Severity.NOTICE, new NoSuchElementException("Tried " + StringUtils.join(values, ", ") + " with no result. Is your version up to date?"));
		
		return null;
		
	}
	
}
