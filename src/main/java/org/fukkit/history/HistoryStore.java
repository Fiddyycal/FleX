package org.fukkit.history;

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

public class HistoryStore {
	
	private BadgeHistory badges;
	
	private ChatCommandHistory messages;
	
	private ConnectionHistory connections;
	
	private DisguiseHistory disguises;
	
	private IPHistory ips;
	
	private NameHistory names;
	
	private PunishmentHistory punishments;
	
	private RankHistory ranks;
	
	public HistoryStore(FleXHumanEntity player) throws SQLException {
		
		this.badges = new BadgeHistory(player);
		
		this.messages = new ChatCommandHistory(player);
		
		this.connections = new ConnectionHistory(player);
		
		this.disguises = new DisguiseHistory(player);
		
		this.ips = new IPHistory(player);
		
		this.names = new NameHistory(player);
		
		this.punishments = new PunishmentHistory(player);
		
		this.ranks = new RankHistory(player);
		
	}
	
	public BadgeHistory getBadges() {
		return this.badges;
	}
	
	public ChatCommandHistory getChatAndCommands() {
		return this.messages;
	}
	
	public ConnectionHistory getConnections() {
		return this.connections;
	}
	
	public DisguiseHistory getDisguises() {
		return this.disguises;
	}
	
	public IPHistory getIps() {
		return this.ips;
	}
	
	public NameHistory getNames() {
		return this.names;
	}
	
	public PunishmentHistory getPunishments() {
		return this.punishments;
	}
	
	public RankHistory getRanks() {
		return this.ranks;
	}

}
