package org.fukkit.history.variance;

import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

import org.fukkit.Memory;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.history.History;
import org.fukkit.reward.Badge;
import org.fukkit.theme.Theme;
import org.fukkit.utils.VersionUtils;

import io.flex.commons.Nullable;

public class BadgeHistory extends History<String> {

	public static final String TABLE_NAME = "flex_history_badge";
	
	public BadgeHistory(FleXHumanEntity player) throws SQLException {
		super(player, TABLE_NAME);
	}
	
	/**
	 * @deprecated
	 * Use {@link #onBadgeReceive(Badge, String)} instead.
	 */
	@Override
	@Deprecated
	public void add(String badge) {
		this.onBadgeReceive(Memory.BADGE_CACHE.get(badge), null);
	}
	
	public Set<Badge> badgeSet() {
		return this.log.values().stream().map(b -> {
			
			String sign = b.substring(2);
			
			return Memory.BADGE_CACHE.get(sign.substring(0, sign.indexOf(' ')));
		
		}).filter(b -> b != null).collect(Collectors.toSet());
	}
	
	public void onBadgeReceive(Badge badge, @Nullable String reason) {
		
		if (badge == null)
			return;
		
		if (!this.log.containsValue(badge.toString()))
			super.add("+ " + badge.toString() + " [" + (reason != null ? reason : "No reason found") + "]");
		
		boolean fp = this.player instanceof FleXPlayer;
		boolean online = this.player.isOnline();
		
		Theme theme = fp && ((FleXPlayer)this.player).getTheme() != null ?((FleXPlayer)this.player).getTheme() : Memory.THEME_CACHE.getDefaultTheme();
		
		if (online) {
			
			if (fp)
				((FleXPlayer)this.player).getPlayer().playSound(this.player.getLocation(), VersionUtils.sound("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"), 1F, 0.1F);
			
			this.player.sendMessage(theme.format("<engine><success>You have been given the badge <spc>" + badge.getName() + "<pp>. (<sc>" + reason + "<pp>)"));
			
		}
		
		if (this.player.getBadge() == null) {
			
			this.player.setBadge(badge);
			
			if (online) {
				
				this.player.sendMessage(theme.format("<engine><pc>You are now displaying the badge <sc>" + badge.getName() + "<pp>."));
				this.player.sendMessage(theme.format("<engine><lore>Change this at any time in player settings<pp>."));
				
			}
				
		}
		
	}
	
	public void onBadgeRemove(Badge badge, @Nullable String reason) {
		
		if (badge == null)
			return;
		
		if (!this.log.containsValue(badge.toString()))
			super.add("- " + badge.toString() + " [" + (reason != null ? reason : "No reason found") + "]");
		
		boolean online = this.player.isOnline();
		
		Theme theme = this.player instanceof FleXPlayer && ((FleXPlayer)this.player).getTheme() != null ?((FleXPlayer)this.player).getTheme() : Memory.THEME_CACHE.getDefaultTheme();
		
		if (online)
			this.player.sendMessage(theme.format("<engine><failure>The badge <spc>" + badge.getName() + "<reset> <failure>has been taken from you<pp>. (<sc>" + reason + "<pp>)"));
		
		if (this.player.getBadge() == badge) {
			
			this.player.setBadge(null);
			
			if (online)
				this.player.sendMessage(theme.format("<engine><pc>You are no longer displaying the badge <sc>" + badge.getName() + "<pp>."));
				
		}
		
	}
	
}
