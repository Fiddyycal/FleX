package org.fukkit.command.defaults;

import org.bukkit.GameMode;
import org.fukkit.command.Command;

@Command(name = "spectator", usage = "/<command> [player]", aliases = { "gm3" })
public class SpectatorCommand extends AbstractGameModeCommand {

	@Override
	public GameMode getGameMode() {
		return GameMode.SPECTATOR;
	}

}
