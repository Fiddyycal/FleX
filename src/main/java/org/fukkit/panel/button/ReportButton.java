package org.fukkit.panel.button;

import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.fukkit.consequence.Punishment;
import org.fukkit.consequence.PunishmentType;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;

import io.flex.commons.file.Language;

public class ReportButton extends AbstractPunishButton {

	private static final long serialVersionUID = -808233538601808625L;

	public ReportButton(Theme theme, Language language, FleXPlayer other) {
		super(Material.NAME_TAG, theme, language, other, PunishmentType.REPORT);
	}

	@Override
	public Set<Punishment> punishmentSet() {
		return this.other.getHistory().getPunishments().reportSet().stream().collect(Collectors.toSet());
	}

}
