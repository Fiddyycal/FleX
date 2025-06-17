package org.fukkit.panel.button;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.fukkit.consequence.Punishment;
import org.fukkit.consequence.PunishmentType;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;

import io.flex.commons.file.Language;

public class MuteButton extends AbstractPunishButton {

	private static final long serialVersionUID = -808233538601808625L;

	public MuteButton(Theme theme, Language language, FleXPlayer other) {
		super(Material.PAPER, theme, language, other, PunishmentType.MUTE);
	}

	@Override
	public Set<Punishment> punishmentSet() {
		return this.other.getHistory().getPunishments().muteSet().stream().collect(Collectors.toSet());
	}

}
