package org.fukkit.command.defaults;

import org.bukkit.GameMode;
import org.fukkit.command.Command;

@Command(name = "adventure", usage = "/<command> [player]", aliases = { "gm2" })
public class AdventureCommand extends AbstractGameModeCommand {

	@Override
	public GameMode getGameMode() {
		return GameMode.ADVENTURE;
	}

}
