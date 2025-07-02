package org.fukkit.json;

import java.util.LinkedList;
import java.util.List;

import org.fukkit.Memory;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotLoadedException;
import org.fukkit.reward.Badge;
import org.fukkit.theme.Theme;

import io.flex.commons.utils.StringUtils;

import net.md_5.bungee.api.chat.ClickEvent.Action;

public class ChatJsonBuffer extends JsonBuffer {
	
	private static final long serialVersionUID = 9103544205964619964L;
	
	public ChatJsonBuffer(FleXPlayer player, FleXPlayer recipient, String message) {
		
		Theme theme = recipient.getTheme();
		
		JsonComponent splitter = new JsonComponent(theme.format("&8|"));
		
		if (recipient.getRank().isStaff()) {
			
			this.append(new JsonComponent(theme.format("<pp>[")));
			
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

			this.append(new JsonComponent(theme.format("<pp>]<reset> <reset>")));
			
		}
		
		Badge badge = player.getBadge();
		
		List<String> badges = new LinkedList<String>();
		
		badges.add("<pc>Badge on display<pp>:<reset> <sc>&l" + (badge != null ? badge.getIcon() : "None"));
		badges.add("");
	    
		try {
			player.getHistory().getBadges().badgeSet().forEach(b -> {
				badges.add("<reset>" + b.getDisplay(theme, true) + "<reset> <pp>-<reset> <pc>" + b.getName() + "<pp>:<reset> " + b.getDescription().replace("%rank%", Memory.RANK_CACHE.get("Owner").getDisplay(theme, false)));
			});
		} catch (FleXPlayerNotLoadedException e) {
			badges.add("<failure>Badges failed to load<pp>.");
		}
		
		this.append(new JsonComponent(player.getDisplayName(theme))
				
				.onHover(theme.format(StringUtils.join(badges, "\n"))));
		
		this.append(new JsonComponent(theme.format(badge != null ? "<sc>" + badge.getIcon() : "<pp>:<reset> <reset>")));
		this.append(new JsonComponent(message));
		
	}

}
