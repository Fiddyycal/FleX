package org.fukkit.history.variance;

import java.sql.SQLException;

import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.History;
import org.fukkit.history.HistoryType;

public class ChatCommandHistory extends History<String> {

	public static final String TABLE_NAME = "flex_history_chat";
	
	public ChatCommandHistory(FleXHumanEntity player) throws SQLException {
		super(HistoryType.CHAT_AND_COMMANDS, player, TABLE_NAME);
	}

}
