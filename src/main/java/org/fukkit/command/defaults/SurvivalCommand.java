package org.fukkit.command.defaults;

import org.bukkit.GameMode;
import org.fukkit.command.Command;

@Command(name = "survival", usage = "/<command> [player]", aliases = { "gm0" })
public class SurvivalCommand extends AbstractGameModeCommand {

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

}
