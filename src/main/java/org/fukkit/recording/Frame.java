package org.fukkit.recording;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.fukkit.utils.ItemUtils;
import org.fukkit.utils.WorldUtils;

import io.flex.commons.Nullable;

public class Frame {
	
	private RecordedAction action;
	
	private Location location, object;
	
	private ItemStack item;
	
	public Frame(RecordedAction action, Location location, @Nullable Location object, @Nullable ItemStack item) {
		this.action = action;
		this.location = location;
		this.object = object;
		this.item = item;
	}
	
	public Frame(RecordedAction action, Location location, @Nullable Location object) {
		this(action, location, object, null);
	}
	
	public Frame(RecordedAction action, Location location, @Nullable ItemStack item) {
		this(action, location, null, item);
	}
	
	public Frame(RecordedAction action, Location location) {
		this(action, location, null, null);
	}
	
	public RecordedAction getAction() {
		return this.action;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public Location getObject() {
		return this.object;
	}
	
	public ItemStack getItem() {
		return this.item;
	}
	
	@Override
	public String toString() {
		
		String frame = "action=" + (this.action != null ? this.action.name().toLowerCase() : RecordedAction.NONE) + " location=" + (this.location != null ? this.location.toString() : null);
		
		if (this.object != null)
			frame = frame + " object=" + this.object.toString();
		
		if (this.item != null)
			frame = frame + " item=" + ItemUtils.serializeB64(item);
		
		return frame;
		
	}
	
	public static Frame from(String serialization) {
		
		try {
			
			if (serialization == null || serialization.equalsIgnoreCase("null"))
				return new Frame(RecordedAction.NONE, null);
			
			String[] split = serialization.split(" ");
			
			RecordedAction action = RecordedAction.valueOf(split[0].split("action=")[1].toUpperCase());
			
			Location loc = null, obj = null;
			
			ItemStack item = null;
			
			for (String key : split) {
				
				if (key.startsWith("action=")) {
					String str = key.split("action=")[1];
					action = str != null && !str.equalsIgnoreCase("null") ? RecordedAction.valueOf(str.toUpperCase()) : RecordedAction.IDLE;
				}
				
				if (key.startsWith("location=")) {
					String str = key.split("location=")[1];
					loc = str != null && !str.equalsIgnoreCase("null") ? WorldUtils.locationFromString(key.split("location=")[1]) : null;
				}
				
				if (key.startsWith("object=")) {
					String str = key.split("object=")[1];
					obj = str != null && !str.equalsIgnoreCase("null") ? WorldUtils.locationFromString(key.split("object=")[1]) : null;
				}
				
				if (key.startsWith("item=")) {
					String str = key.split("item=")[1];
					item = str != null && !str.equalsIgnoreCase("null") ? ItemUtils.deserializeB64(key.split("item=")[1]) : null;
				}
				
			}
			
			return new Frame(action, loc, obj, item);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}

}
