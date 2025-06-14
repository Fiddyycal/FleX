package org.fukkit.command.defaults;

import org.bukkit.GameMode;
import org.fukkit.command.Command;

@Command(name = "survival", usage = "/<command> [player]", aliases = { "gamemodesurvival", "gamemode0", "gamemodes", "modesurvival", "mode0", "modes", "gmsurvival", "gm0", "gms" })
public class SurvivalCommand extends AbstractGameModeCommand {

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

}
