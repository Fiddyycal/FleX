package org.fukkit.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;

public class JsonBuffer implements CharSequence, Serializable {
	
	private static final long serialVersionUID = 6094766172447703133L;

	private List<JsonComponent> components = new ArrayList<>();
	
	private StringBuilder builder = new StringBuilder();

	public JsonBuffer append(JsonComponent component) {
		
		this.components.add(component);
		this.builder.append(component.getText());
		return this;
		
	}

	public JsonBuffer append(JsonBuffer other) {
		
		for (JsonComponent component : other.components)
			this.append(component);
		
		return this;
		
	}
	
	public JsonBuffer replace(String placeholder, JsonComponent replacement) {
		
	    List<JsonComponent> newComponents = new ArrayList<>();

	    for (JsonComponent comp : this.components) {
	    	
	        String text = comp.getText();
	        
	        if (text.contains(placeholder)) {
	        	
	            String[] parts = text.split(placeholder, -1);
	            
	            for (int i = 0; i < parts.length; i++) {
	            	
	                if (!parts[i].isEmpty())
	                    newComponents.add(new JsonComponent(parts[i]));
	                
	                if (i < parts.length - 1)
	                    newComponents.add(replacement);
	                
	            }
	            
	        } else newComponents.add(comp);
	        
	    }

	    // reset
	    this.components.clear();
	    this.builder.setLength(0);

	    for (JsonComponent comp : newComponents)
	        this.append(comp);

	    return this;
	}

	public JsonBuffer replace(String placeholder, JsonBuffer replacement) {
		
	    List<JsonComponent> newComponents = new ArrayList<>();
	    
	    for (JsonComponent comp : this.components) {
	    	
	        String text = comp.getText();
	        
	        if (text.contains(placeholder)) {
	        	
	            String[] parts = text.split(placeholder, -1);
	            
	            for (int i = 0; i < parts.length; i++) {
	            	
	                if (!parts[i].isEmpty())
	                    newComponents.add(new JsonComponent(parts[i]));
	                
	                if (i < parts.length - 1)
	                    newComponents.addAll(replacement.components);
	                
	            }
	            
	        } else newComponents.add(comp);
	        
	    }
	    
	    // reset
	    this.components.clear();
	    this.builder.setLength(0);
	    
	    for (JsonComponent comp : newComponents)
	        this.append(comp);
	    
	    return this;
	}

	public String getRaw() {
		return this.builder.toString();
	}

	@Override
	public int length() {
		return this.builder.length();
	}

	@Override
	public char charAt(int index) {
		return this.builder.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return this.builder.subSequence(start, end);
	}

	@Override
	public String toString() {
		
		JsonArray jsonArray = new JsonArray();
		
		// Minecraft requires the first element to be an empty string for "text"
		jsonArray.add("");

		for (JsonComponent comp : this.components)
			jsonArray.add(comp.toJson());

		return jsonArray.toString();
		
	}
	
}
