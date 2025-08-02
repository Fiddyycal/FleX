package org.fukkit.json;

import java.io.Serializable;

import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.chat.ClickEvent.Action;

public class JsonComponent implements CharSequence, Serializable {

    private static final long serialVersionUID = 3173343685650932944L;

    private final String text;
    private JsonObject json;

    public JsonComponent(String text) {
    	
        this.text = text;
        this.json = new JsonObject();
        this.json.addProperty("text", text);
        
    }

    private JsonComponent(String text, JsonObject json) {
    	
        this.text = text;
        this.json = json;
        
    }

    public JsonComponent onHover(String hoverText) {
    	
        JsonObject newJson = this.json.deepCopy();
        JsonObject hoverEvent = new JsonObject();
        
        hoverEvent.addProperty("action", "show_text");

        JsonObject value = new JsonObject();
        
        value.addProperty("text", "");
        
        JsonArray extra = new JsonArray();

        JsonObject hoverTextObj = new JsonObject();
        
        hoverTextObj.addProperty("text", hoverText);
        
        extra.add(hoverTextObj);

        value.add("extra", extra);
        hoverEvent.add("value", value);

        newJson.add("hoverEvent", hoverEvent);

        return new JsonComponent(this.text, newJson);
        
    }
    
    // TODO
    public JsonComponent onHover(ItemStack item) {
        // maybe serialize ItemStack to JSON format?
        // For now, just placeholder text:
        return this.onHover("Item: " + item.getType().name());
    }

    public JsonComponent onHover(Block block) {
        return this.onHover("Block: " + block.getType().name());
    }

    public JsonComponent onHover(Statistic statistic) {
    	
        JsonObject newJson = this.json.deepCopy();
        JsonObject hoverEvent = new JsonObject();
        
        hoverEvent.addProperty("action", "show_achievement");
        hoverEvent.addProperty("value", "stat." + statistic.name());

        newJson.add("hoverEvent", hoverEvent);
        
        return new JsonComponent(this.text, newJson);
        
    }

    public JsonComponent onClick(Action action, String value) {
    	
        JsonObject newJson = this.json.deepCopy();
        JsonObject clickEvent = new JsonObject();
        
        clickEvent.addProperty("action", action.name().toLowerCase());
        clickEvent.addProperty("value", value);

        newJson.add("clickEvent", clickEvent);
        
        return new JsonComponent(this.text, newJson);
        
    }

    public String getText() {
        return this.text;
    }
    
    public JsonObject toJson() {
        return this.json.deepCopy();
    }

    @Override
    public int length() {
        return this.text.length();
    }

    @Override
    public char charAt(int index) {
        return this.text.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return this.text.subSequence(start, end);
    }

    @Override
    public String toString() {
        return this.json.toString();
    }
    
}
