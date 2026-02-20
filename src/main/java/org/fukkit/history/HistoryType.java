package org.fukkit.history;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.variance.BadgeHistory;
import org.fukkit.history.variance.ChatCommandHistory;
import org.fukkit.history.variance.ConnectionHistory;
import org.fukkit.history.variance.DisguiseHistory;
import org.fukkit.history.variance.IPHistory;
import org.fukkit.history.variance.NameHistory;
import org.fukkit.history.variance.PunishmentHistory;
import org.fukkit.history.variance.RankHistory;

public enum HistoryType {

	BADGES(BadgeHistory.class),
	CHAT_AND_COMMANDS(ChatCommandHistory.class),
	CONNECTIONS(ConnectionHistory.class),
	DISGUISES(DisguiseHistory.class),
	IPS(IPHistory.class),
	RECORDINGS(null)/*TODO*/,
	NAMES(NameHistory.class),
	PUNISHMENTS(PunishmentHistory.class),
	RANKS(RankHistory.class);
	
	private Class<? extends History<?>> clas;
	
	private HistoryType(Class<? extends History<?>> clas) {
		this.clas = clas;
	}
	
	public History<?> create(FleXHumanEntity player) throws SQLException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return this.clas.getDeclaredConstructor(FleXHumanEntity.class).newInstance(player);
    }
	
}
