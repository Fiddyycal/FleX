package org.fukkit.json;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotLoadedException;
import org.fukkit.reward.Badge;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.emoji.Emoji;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.StringUtils;

import net.md_5.bungee.api.chat.ClickEvent.Action;

public class ChatJsonDisplayBuffer extends JsonBuffer {
	
	private static final long serialVersionUID = 6759819969891361278L;
	
	public ChatJsonDisplayBuffer(FleXPlayer player, FleXPlayer recipient) {
		
		Theme theme = recipient.getTheme();
		
		boolean mcgamer = theme.getName().equalsIgnoreCase("mcgamer");
		
		Badge badge = player.getBadge();
		
		List<String> badges = new LinkedList<String>();
		
		badges.add("<pc>Badge on display<pp>:<reset> <sc>" + (badge != null ? badge.getIcon() : "None"));
	    
		try {
			
			Set<Badge> all = player.getHistory().getBadges().badgeSet();
			
			if (!all.isEmpty()) {
				
				badges.add("");
				
				all.forEach(b -> {
					badges.add("<reset>" + b.getDisplay(theme, true) + "<reset> <pp>&l" + Emoji.DOUBLE_RIGHT_POINTING_ARROW + "<reset> <lore>" + b.getDescription(theme));
				});
				
			}
			
		} catch (FleXPlayerNotLoadedException e) {
			badges.add("<failure>Badges failed to load<pp>.");
		}
		
		Variable<?>[] variables = new Variable<?>[] {
			
			new Variable<String>("%player%", player.getName()),
			new Variable<String>("%display%", player.getDisplayName(theme)),
			new Variable<String>("%rank%", player.getRank().getDisplay(theme, true)),
			new Variable<String>("%badges%", StringUtils.join(badges, "\n")),
			
		};
		
		String[] format = ThemeMessage.CHAT_FORMAT_HOVER.format(theme, recipient.getLanguage(), variables);
		
		JsonComponent comp = new JsonComponent(theme.format(player.getDisplayName(theme)))
		
				.onHover(theme.format(StringUtils.join(format, "\n")))
				.onClick(Action.RUN_COMMAND, "/stats " + player.getName());
		
		this.append(comp);
		
		if (badge != null) {
			
			this.append(new JsonComponent(mcgamer ? theme.format("&a" + badge.getIcon()) : " " + badge.getDisplay(theme, false))
					
					.onHover(theme.format("<reset>" + badge.getDisplay(theme, true) + "<reset> <pp>&l" + Emoji.DOUBLE_RIGHT_POINTING_ARROW + "<reset> <lore>" + badge.getDescription(theme))));
			
		}
		
	}

}
