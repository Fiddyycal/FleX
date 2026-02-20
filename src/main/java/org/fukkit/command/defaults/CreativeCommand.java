package org.fukkit.command.defaults;

import org.bukkit.GameMode;
import org.fukkit.command.Command;

@Command(name = "creative", usage = "/<command> [player]", aliases = { "gm1", "gmc" })
public class CreativeCommand extends AbstractGameModeCommand {

	@Override
	public GameMode getGameMode() {
		return GameMode.CREATIVE;
	}

}
