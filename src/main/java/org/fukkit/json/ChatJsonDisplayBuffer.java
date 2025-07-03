package org.fukkit.json;

import java.util.LinkedList;
import java.util.List;

import org.fukkit.Memory;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotLoadedException;
import org.fukkit.reward.Badge;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;

import io.flex.commons.emoji.Emoji;
import io.flex.commons.file.Variable;
import io.flex.commons.utils.StringUtils;

public class ChatJsonDisplayBuffer extends JsonBuffer {
	
	private static final long serialVersionUID = 6759819969891361278L;
	
	public ChatJsonDisplayBuffer(FleXPlayer player, FleXPlayer recipient) {
		
		Theme theme = recipient.getTheme();
		
		Badge badge = player.getBadge();
		
		List<String> badges = new LinkedList<String>();
		
		badges.add("<pc>Badge on display<pp>:<reset> <sc>&l" + (badge != null ? badge.getIcon() : "None"));
		badges.add("");
	    
		try {
			player.getHistory().getBadges().badgeSet().forEach(b -> {
				badges.add("<reset>" + b.getDisplay(theme, true) + "<reset> <pp>&l" + Emoji.DOUBLE_RIGHT_POINTING_ARROW + "<reset> <lore>" + b.getDescription().replace("%rank%", Memory.RANK_CACHE.get("Owner").getDisplay(theme, false)));
			});
		} catch (FleXPlayerNotLoadedException e) {
			badges.add("<failure>Badges failed to load<pp>.");
		}
		
		Variable<?>[] variables = new Variable<?>[] {
			
			new Variable<String>("%player%", player.getName()),
			new Variable<String>("%display%", player.getDisplayName(theme)),
			new Variable<String>("%rank%", player.getRank().getDisplay(theme, true)),
			new Variable<String>("%badges%", StringUtils.join(badges, "\n")),
			
		};
		
		String format = ThemeMessage.CHAT_FORMAT_HOVER.format(theme, recipient.getLanguage(), variables)[0];
		
		this.append(new JsonComponent(player.getDisplayName(theme))
				
				.onHover(format));
		
		this.append(new JsonComponent(theme.format(badge != null ? "<sc>" + badge.getIcon() : "<reset>")));
		
	}

}
