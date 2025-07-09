package org.fukkit.history.variance;

import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

import org.fukkit.Memory;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.History;
import org.fukkit.reward.Badge;

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
		
	}
	
	public void onBadgeRemove(Badge badge, @Nullable String reason) {
		
		if (badge == null)
			return;
		
		if (!this.log.containsValue(badge.toString()))
			super.add("- " + badge.toString() + " [" + (reason != null ? reason : "No reason found") + "]");
		
	}
	
}
