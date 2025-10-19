package org.fukkit.recording;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.fukkit.utils.ItemUtils;
import org.fukkit.utils.WorldUtils;

import io.flex.commons.Nullable;
import io.flex.commons.utils.ArrayUtils;

public class Frame {
	
	private static final String
	
	time_key = "time=",
	action_key = "actions=",
	location_key = "location=",
	interacted_key  = "interacted=",
	item_key = "item=",
	message_key = "message=";
	
	private long time;
	
	private RecordedAction[] actions = new RecordedAction[0];
	
	private Location location, interacted;
	
	private ItemStack item;
	
	private String message, timestamp;
	
	private Frame(long time, Location location, @Nullable Location interacted, @Nullable ItemStack item, @Nullable String message, @Nullable RecordedAction... actions) {
		
		this.setTime(time);
		
		if (actions != null && actions.length > 0)
			this.actions = actions;
		
		this.location = location;
		this.interacted = interacted;
		this.item = item;
		this.message = message;
		
	}
	
	public Frame(Location location) {
		this(System.currentTimeMillis(), location, null, null, null);
	}
	
	public String getTimeStamp() {
		return this.timestamp;
	}
	
	public RecordedAction[] getActions() {
		return this.actions;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public Location getInteractAtLocation() {
		return this.interacted;
	}
	
	public ItemStack getItem() {
		return this.item;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void addAction(RecordedAction action) {
		
		this.setTime(System.currentTimeMillis());
		
		if (!ArrayUtils.contains(this.actions, action))
			this.actions = ArrayUtils.add(this.actions, action);
		
	}
	
	public void setLocation(Location location) {
		this.setTime(System.currentTimeMillis());
		this.location = location;
	}
	
	public void setInteractAtLocation(Location interacted) {
		this.setTime(System.currentTimeMillis());
		this.interacted = interacted;
	}
	
	public void setItem(ItemStack item) {
		this.setTime(System.currentTimeMillis());
		this.item = item;
	}
	
	public void setMessage(String message) {
		this.setTime(System.currentTimeMillis());
		this.message = message;
	}
	
	private void setTime(long time) {
		this.time = time;
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		this.timestamp = format.format(time);
	}
	
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		for (RecordedAction action : this.actions) {
			
		    if (builder.length() > 0)
		    	builder.append(",");
		    
		    builder.append(action.name().toLowerCase());
		    
		}
		
		String actions = builder.length() > 0 ? builder.toString() : RecordedAction.NONE.name().toLowerCase();
		String frame = time_key + this.time + " " + action_key + actions + " " + location_key + (this.location != null ? this.location.toString() : "null");
		
		if (this.interacted != null)
			frame += " " + interacted_key + this.interacted.toString();
		
		if (this.item != null)
			frame += " " + item_key + ItemUtils.serializeB64(this.item);
		
		if (this.message != null) {
			// Escape single quotes inside the message
			String escaped = this.message.replace("'", "\\'");
			frame += " " + message_key + "'" + escaped + "'";
		}
		
		return frame;
	}
	
	public static Frame from(String serialization) {
		
		if (serialization == null || serialization.equalsIgnoreCase("null"))
			return new Frame(null);
		
		long time = -1;
		
		RecordedAction[] actions = { RecordedAction.NONE };
		
		Location loc = null, obj = null;
		
		ItemStack item = null;
		
		String message = null;
		
		boolean quotes = false;
		
		StringBuilder current = new StringBuilder();
		List<String> tokens = new ArrayList<String>();

		for (int i = 0; i < serialization.length(); i++) {
			
			char c = serialization.charAt(i);
			
			if (c == '\'' && (i == 0 || serialization.charAt(i - 1) != '\\')) {
				
				quotes = !quotes;
				continue;
				
			}
			
			if (c == ' ' && !quotes) {
				
				if (current.length() > 0) {
					tokens.add(current.toString());
					current.setLength(0);
				}
				
			} else current.append(c);
			
		}
		
		if (current.length() > 0)
			tokens.add(current.toString());
		
		for (String key : tokens) {
			
			if (key.startsWith(time_key)) {
				String str = key.substring(time_key.length());
				time = str != null && !str.equalsIgnoreCase("-1") ? Long.valueOf(str) : -1;
			}
			
			if (key.startsWith(action_key)) {
				
				String str = key.substring(action_key.length());
				
				if (str != null && !str.equalsIgnoreCase("null")) {
					
					String[] all = str.split(",");
					
					actions = new RecordedAction[all.length];
					
					for (int i = 0; i < all.length; i++)
						actions[i] = RecordedAction.valueOf(all[i].toUpperCase());
					
				}
				
			}
			
			if (key.startsWith(location_key)) {
				String str = key.substring(location_key.length());
				loc = str != null && !str.equalsIgnoreCase("null") ? WorldUtils.locationFromString(str) : null;
			}
			
			if (key.startsWith(interacted_key)) {
				String str = key.substring(interacted_key.length());
				obj = str != null && !str.equalsIgnoreCase("null") ? WorldUtils.locationFromString(str) : null;
			}
			
			if (key.startsWith(item_key)) {
				String str = key.substring(item_key.length());
				item = str != null && !str.equalsIgnoreCase("null") ? ItemUtils.deserializeB64(str) : null;
			}
			
			if (key.startsWith(message_key)) {
				String str = key.substring(message_key.length());
				// Unescaping quotes
				message = str.replace("\\'", "'");
			}
			
		}
		
		return new Frame(time, loc, obj, item, message, actions);
		
	}
}