package org.fukkit.panel.button;

import java.util.Set;

import org.bukkit.Material;
import org.fukkit.consequence.Punishment;
import org.fukkit.consequence.PunishmentType;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotLoadedException;
import org.fukkit.theme.Theme;

import io.flex.commons.file.Language;

public class ReportHistoryButton extends AbstractPunishButton {

	private static final long serialVersionUID = -808233538601808625L;

	public ReportHistoryButton(Theme theme, Language language, FleXPlayer other) {
		super(Material.NAME_TAG, theme, language, other, PunishmentType.REPORT);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Punishment> Set<T> asSet() throws FleXPlayerNotLoadedException {
		return (Set<T>) this.other.getHistory().getPunishments().reportSet();
	}

}
