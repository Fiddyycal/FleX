package org.fukkit.command.defaults;

import org.bukkit.GameMode;
import org.fukkit.command.Command;

@Command(name = "adventure", usage = "/<command> [player]", aliases = { "gamemodeadventure", "gamemode2", "gamemodea", "modeadventure", "mode2", "modea", "gmadventure", "gm2", "gma" })
public class AdventureCommand extends AbstractGameModeCommand {

	@Override
	public GameMode getGameMode() {
		return GameMode.ADVENTURE;
	}

}
