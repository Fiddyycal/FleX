package org.fukkit.panel.button;

import java.util.Set;
import java.util.stream.Collectors;

import org.fukkit.consequence.Punishment;
import org.fukkit.consequence.PunishmentType;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;
import org.fukkit.utils.VersionUtils;

import io.flex.commons.file.Language;

public class KickButton extends AbstractPunishButton {

	private static final long serialVersionUID = -808233538601808625L;

	public KickButton(Theme theme, Language language, FleXPlayer other) {
		super(VersionUtils.material("GOLD_AXE", "GOLDEN_AXE"), theme, language, other, PunishmentType.KICK);
	}

	@Override
	public Set<Punishment> punishmentSet() {
		return this.other.getHistory().getPunishments().kickSet().stream().collect(Collectors.toSet());
	}

}
