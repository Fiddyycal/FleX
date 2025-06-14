package org.fukkit.command.defaults;

import org.bukkit.GameMode;
import org.fukkit.command.Command;

@Command(name = "spectator", usage = "/<command> [player]", aliases = { "gamemodespectator", "gamemode3", "gamemodesp", "modespectator", "mode3", "modesp", "gmspectator", "gm3", "gmsp" })
public class SpectatorCommand extends AbstractGameModeCommand {

	@Override
	public GameMode getGameMode() {
		return GameMode.SPECTATOR;
	}

}
