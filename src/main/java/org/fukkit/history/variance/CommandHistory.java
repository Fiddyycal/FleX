package org.fukkit.history.variance;

import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.History;

public class CommandHistory extends History<String> {
	
	public CommandHistory(FleXHumanEntity player) {
		super(player, "flex_history_command");
	}

}
