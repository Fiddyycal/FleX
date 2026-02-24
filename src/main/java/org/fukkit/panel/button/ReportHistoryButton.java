package org.fukkit.panel.button;

import java.util.Set;

import org.bukkit.Material;
import org.fukkit.consequence.Punishment;
import org.fukkit.consequence.PunishmentType;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerHistoryNotLoadedException;
import org.fukkit.history.variance.PunishmentHistory;
import org.fukkit.theme.Theme;

import io.flex.commons.file.Language;

public class ReportHistoryButton extends AbstractPunishButton {

	public ReportHistoryButton(Theme theme, Language language, FleXPlayer other) {
		super(Material.NAME_TAG, theme, language, other, PunishmentType.REPORT);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Punishment> Set<T> asSet() throws FleXPlayerHistoryNotLoadedException {
		return (Set<T>) this.other.getHistory(PunishmentHistory.class).reportSet();
	}

}
