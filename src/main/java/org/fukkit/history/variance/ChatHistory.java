package org.fukkit.history.variance;

import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.History;

public class ChatHistory extends History<String> {
	
	public ChatHistory(FleXHumanEntity player) {
		super(player, "flex_history_chat");
	}

}
