package org.fukkit.json;

import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;

import net.md_5.bungee.api.chat.ClickEvent.Action;

public class ChatJsonInteractableBuffer extends JsonBuffer {
	
	private static final long serialVersionUID = 9103544205964619964L;
	
	public ChatJsonInteractableBuffer(FleXPlayer player, FleXPlayer recipient) {
		
		Theme theme = recipient.getTheme();
		
		JsonComponent splitter = new JsonComponent(theme.format("&8|"));
		
		this.append(new JsonComponent(theme.format("&4" + (theme.getName().equalsIgnoreCase("mcgamer") ? "X" : "F")))
				
				.onHover(theme.format("<interactable>Open the FleX panel<pp>."))
				.onClick(Action.RUN_COMMAND, "/flex " + player.getName()));
		
		this.append(splitter);
		this.append(new JsonComponent(theme.format("&aK"))
				
				.onHover(theme.format("<interactable>Kick " + player.getDisplayName(theme) + "<reset> <interactable>from the network<pp>."))
				.onClick(Action.RUN_COMMAND, "/kick " + player.getName()));
		
		this.append(splitter);
		this.append(new JsonComponent(theme.format("&6M"))

				.onHover(theme.format("<interactable>Mute " + player.getDisplayName(theme) + "<pp>."))
				.onClick(Action.RUN_COMMAND, "/mute " + player.getName()));
		
		this.append(splitter);
		this.append(new JsonComponent(theme.format("&cB"))

				.onHover(theme.format("<interactable>Ban " + player.getDisplayName(theme) + "<reset> <interactable>from the network<pp>."))
				.onClick(Action.RUN_COMMAND, "/ban " + player.getName()));
		
	}

}
