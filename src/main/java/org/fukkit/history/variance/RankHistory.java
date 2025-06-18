package org.fukkit.history.variance;

import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

import org.fukkit.Memory;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.History;
import org.fukkit.reward.Rank;

import io.flex.commons.Nullable;

public class RankHistory extends History<String> {

	public static final String TABLE_NAME = "flex_history_rank";
	
	public RankHistory(FleXHumanEntity player) throws SQLException {
		super(player, "flex_history_rank");
	}
	
	/**
	 * @deprecated
	 * Use {@link #onRankReceive(Rank, String)} instead.
	 */
	@Override
	@Deprecated
	public void add(String rank) {
		this.onRankReceive(Memory.RANK_CACHE.get(rank), null);
	}
	
	public Set<Rank> rankSet() {
		return this.log.values().stream().map(r -> {
			
			String sign = r.substring(2);
			
			return Memory.RANK_CACHE.get(sign.substring(0, sign.indexOf(' ')));
			
		}).filter(r -> r != null).collect(Collectors.toSet());
	}
	
	public void onRankReceive(Rank rank, @Nullable String reason) {
		
		if (rank == null)
			return;
		
		if (!this.log.containsValue(rank.getName()))
			super.add("+ " + rank.getName() + " [" + (reason != null ? reason : "No reason found") + "]");
		
	}
	
}
