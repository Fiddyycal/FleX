package org.fukkit.history.variance;

import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.History;

public class ChatCommandHistory extends History<String> {

	public static final String TABLE_NAME = "flex_history_chat";
	
	public ChatCommandHistory(FleXHumanEntity player) {
		super(player, "flex_history_chat");
	}

}
