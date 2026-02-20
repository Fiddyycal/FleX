package org.fukkit.panel.button;

import java.util.Set;
import org.fukkit.consequence.Punishment;
import org.fukkit.consequence.PunishmentType;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerHistoryNotLoadedException;
import org.fukkit.history.HistoryType;
import org.fukkit.history.variance.PunishmentHistory;
import org.fukkit.theme.Theme;
import org.fukkit.utils.VersionUtils;

import io.flex.commons.file.Language;

public class KickButton extends AbstractPunishButton {

	public KickButton(Theme theme, Language language, FleXPlayer other) {
		super(VersionUtils.material("GOLD_AXE", "GOLDEN_AXE"), theme, language, other, PunishmentType.KICK);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Punishment> Set<T> asSet() throws FleXPlayerHistoryNotLoadedException {
		return (Set<T>) ((PunishmentHistory)this.other.getHistory(HistoryType.PUNISHMENTS)).kickSet();
	}

}
