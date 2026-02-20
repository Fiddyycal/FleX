package org.fukkit.history.variance;

import java.sql.SQLException;

import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.History;
import org.fukkit.history.HistoryType;

public class DisguiseHistory extends History<String> {

	public static final String TABLE_NAME = "flex_history_disguise";
	
	public DisguiseHistory(FleXHumanEntity player) throws SQLException {
		super(HistoryType.DISGUISES, player); // TODO
	}

}
