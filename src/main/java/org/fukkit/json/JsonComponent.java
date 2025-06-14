package org.fukkit.json;

import java.io.Serializable;

import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.ClickEvent.Action;

public class JsonComponent implements CharSequence, Serializable {
	
	private static final long serialVersionUID = 6094766172447703133L;
	
	private String text, json;
	
	public JsonComponent(String text) {
		this.text = text;
		this.json = "{\"text\":\"" + text + "\"";
	}
	
	private JsonComponent() {}
	
	public JsonComponent onHover(String text) {
		
		JsonComponent component = new JsonComponent();

		component.text = this.text + text;
		component.json = this.json + ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + text + "\"}";
		
		return component;
		
	}
	
	public JsonComponent onHover(Entity entity) {
		
		JsonComponent component = new JsonComponent();

		// TODO:
		
		return component;
		
	}
	
	public JsonComponent onHover(ItemStack item) {
		
		JsonComponent component = new JsonComponent();

		// TODO:
		
		component.text = this.text + item;
		component.json = this.json + ",\"hoverEvent\":{\"action\":\"show_item\",\"value\":\"{id:cake,tag:{display:{Name:\\\"A Cake\\\",Lore:[\\\"Made by\\\",\\\"Steve & Alex\\\",\\\"With Love <3\\\"]}}}\"}";
		
		return component;
		
	}
	
	public JsonComponent onHover(Block block) {
		
		JsonComponent component = new JsonComponent();
		
		// TODO:
		
		component.text = this.text + block;
		component.json = this.json + ",\"hoverEvent\":{\"action\":\"show_item\",\"value\":\"{id:cake,tag:{display:{Name:\\\"A Cake\\\",Lore:[\\\"Made by\\\",\\\"Steve & Alex\\\",\\\"With Love <3\\\"]}}}\"}";
		
		return component;
		
	}
	
	/* ... Changed to advancement ...
	public JsonComponent onHover(Achievement achievement) {

		JsonComponent component = new JsonComponent();
		
		component.text = this.text + achievement;
		component.json = this.json + ",\"hoverEvent\":{\"action\":\"show_achievement\",\"value\":\"achievement." + achievement + "\"}";
		
		return component;
		
	}
	*/
	
	public JsonComponent onHover(Statistic statistic) {
		
		JsonComponent component = new JsonComponent();
		
		component.text = this.text + statistic;
		component.json = this.json + ",\"hoverEvent\":{\"action\":\"show_achievement\",\"value\":\"stat." + statistic + "\"}";
		
		return component;
		
	}
	
	public JsonComponent onClick(Action action, String text) {
		
		JsonComponent component = new JsonComponent();
		
		component.text = this.text + text;
		component.json = this.json + ",\"clickEvent\":{\"action\":\"" + action.name().toLowerCase() + "\",\"value\":\"" + text + "\"}";
		
		return component;
		
	}
	
	public String getText() {
		return this.text;
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
		return this.json + "}";
	}

}
