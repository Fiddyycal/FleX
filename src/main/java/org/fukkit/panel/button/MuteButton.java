package org.fukkit.panel.button;

import java.util.Set;
import org.bukkit.Material;
import org.fukkit.consequence.Punishment;
import org.fukkit.consequence.PunishmentType;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotLoadedException;
import org.fukkit.theme.Theme;

import io.flex.commons.file.Language;

public class MuteButton extends AbstractPunishButton {

	private static final long serialVersionUID = -808233538601808625L;

	public MuteButton(Theme theme, Language language, FleXPlayer other) {
		super(Material.PAPER, theme, language, other, PunishmentType.MUTE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Punishment> Set<T> asSet() throws FleXPlayerNotLoadedException {
		return (Set<T>) this.other.getHistory().getPunishments().muteSet();
	}

}
