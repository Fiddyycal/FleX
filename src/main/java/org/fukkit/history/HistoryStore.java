package org.fukkit.history;

import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.variance.BadgeHistory;
import org.fukkit.history.variance.ChatHistory;
import org.fukkit.history.variance.CommandHistory;
import org.fukkit.history.variance.ConnectionHistory;
import org.fukkit.history.variance.DisguiseHistory;
import org.fukkit.history.variance.IpHistory;
import org.fukkit.history.variance.LocalRecordHistory;
import org.fukkit.history.variance.NameHistory;
import org.fukkit.history.variance.PunishmentHistory;
import org.fukkit.history.variance.RankHistory;
import org.fukkit.history.variance.SkinHistory;

public class HistoryStore {
	
	private BadgeHistory badges;
	
	private ChatHistory messages;
	
	private CommandHistory commands;
	
	private ConnectionHistory connections;
	
	private DisguiseHistory disguises;
	
	private IpHistory ips;
	
	private LocalRecordHistory records;
	
	private NameHistory names;
	
	private PunishmentHistory punishments;
	
	private RankHistory ranks;
	
	private SkinHistory skins;
	
	public HistoryStore(FleXHumanEntity player) {
		
		this.badges = new BadgeHistory(player);
		
		this.messages = new ChatHistory(player);
		
		this.commands = new CommandHistory(player);
		
		this.connections = new ConnectionHistory(player);
		
		this.disguises = new DisguiseHistory(player);
		
		this.ips = new IpHistory(player);
		
		this.records = new LocalRecordHistory(player);
		
		this.names = new NameHistory(player);
		
		this.punishments = new PunishmentHistory(player);
		
		this.ranks = new RankHistory(player);
		
		this.skins = new SkinHistory(player);
		
	}
	
	public BadgeHistory getBadges() {
		return this.badges;
	}
	
	public ChatHistory getMessages() {
		return this.messages;
	}
	
	public CommandHistory getCommands() {
		return this.commands;
	}
	
	public ConnectionHistory getConnections() {
		return this.connections;
	}
	
	public DisguiseHistory getDisguises() {
		return this.disguises;
	}
	
	public IpHistory getIps() {
		return this.ips;
	}
	
	public LocalRecordHistory getFlowRecords() {
		return this.records;
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
	
	public SkinHistory getSkins() {
		return this.skins;
	}

}
