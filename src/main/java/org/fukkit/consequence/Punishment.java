package org.fukkit.consequence;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import org.fukkit.Fukkit;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;

import io.flex.commons.Nullable;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLMap;
import io.flex.commons.sql.SQLRowWrapper;
import io.flex.commons.utils.CollectionUtils;

public abstract class Punishment extends Consequence {
	
	protected long reference;
	
	protected String[] evidence;
	
	public Punishment(FleXPlayer player, FleXPlayer by, Reason reason, boolean ip, boolean silent, String... evidence) {
		
		super(player, by, reason, ip, silent);
		
		if (evidence != null)
			this.evidence = evidence;
		
		if (this.isReduced()) {
			
			this.duration = reason.getDuration() / 2;
			
			this.until = System.currentTimeMillis() + this.duration;
			
		}
		
		player.getHistoryAsync(history -> history.getPunishments().add(this));
		
	}
	
	protected Punishment(long reference) throws SQLException {
		
		super();
		
		this.load(reference);
		
	}
	
	public static <T extends Punishment> Set<T> download(FleXHumanEntity player) throws SQLException {
		return download(player, false);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Punishment> Set<T> download(FleXHumanEntity player, boolean outgoing) throws SQLException {
		return (Set<T>) download(player, outgoing, null);
	}
	
	public static Set<? extends Punishment> download(FleXHumanEntity player, boolean outgoing, @Nullable PunishmentType type) throws SQLException {
		
		SQLDatabase database = Fukkit.getConnectionHandler().getDatabase();
		
		Set<Punishment> convictions = new LinkedHashSet<Punishment>();
		
		for (SQLRowWrapper row : database.getRows("flex_punishment", SQLCondition.where(outgoing ? "by" : "uuid").is(player.getUniqueId()))) {
			
			PunishmentType check = null;
			
			try {
				check = PunishmentType.valueOf(row.getString("type"));
			} catch (IllegalArgumentException ignore) {
				continue;
			}
			
			if (type != null && check != type)
				continue;
			
			long reference = row.getLong("id");
			
			switch (check) {
			case BAN:
				
				convictions.add(Ban.download(reference));
				break;
				
			case KICK:
				
				convictions.add(Kick.download(reference));
				break;
				
			case MUTE:
				
				convictions.add(Mute.download(reference));
				break;
				
			case REPORT:
				
				convictions.add(Report.download(reference));
				break;
				
			default:
				break;
			}
			
		}
		
		return convictions;
		
	}
	
	public long getReference() {
		return this.reference;
	}
	
	public String[] getEvidence() {
		return this.evidence;
	}
	
	public long getRemaining() {
		return this.isActive() ? this.until - System.currentTimeMillis() : 0;
	}
	
	public void setEvidence(String... evidence) throws SQLException {
		
		SQLDatabase connection = Fukkit.getConnectionHandler().getDatabase();
		SQLRowWrapper row = connection.getRow("flex_punishment", SQLCondition.where("id").is(this.reference));
		
		row.set("evidence", Arrays.asList(this.evidence = evidence).toString());
		
	}
	
	public boolean hasEvidence() {
		return this.evidence != null && this.evidence.length != 0 && !Arrays.stream(this.evidence).anyMatch(e -> e.toUpperCase().contains(EvidenceType.NON_APPLICABLE));
	}
	
	public boolean isReduced() {
		return this.hasEvidence() && IntStream.range(0, this.evidence.length).anyMatch(i -> this.evidence[i].contains(EvidenceType.REDUCED));
	}
	
	public boolean isActive() {
		return System.currentTimeMillis() < this.until && !this.pardoned;
	}
	
	public void pardon() throws SQLException {

		SQLDatabase database = Fukkit.getConnectionHandler().getDatabase();
		SQLRowWrapper row = database.getRow("flex_punishment", SQLCondition.where("id").is(this.reference));
		
		long until = System.currentTimeMillis();
		
		if (until < this.until)
			row.set("until", this.until = until);
		
		row.set("pardoned", this.pardoned = true);
		
	}
	
	protected void upload() throws SQLException {

		SQLDatabase database = Fukkit.getConnectionHandler().getDatabase();
		
		if (!(this.reference > 0))
			this.reference = database.getTableSize("flex_punishment") + 1;
		
		SQLRowWrapper row = database.getRow("flex_punishment", SQLCondition.where("id").is(this.reference));
		
		if (row != null) {
			
			row.set("uuid", this.uuid);
			row.set("by", this.by);
			row.set("time", this.time);
			row.set("until", this.until);
			row.set("type", this.getType().name());
			row.set("reason", this.reason.name());
			row.set("evidence", Arrays.asList(this.evidence).toString());
			row.set("ip", this.ip);
			row.set("silent", this.silent);
			row.set("pardoned", this.pardoned);
			row.update();
		
	    } else {
	    	
	    	database.addRow("flex_punishment", SQLMap.of(
	    			
	    			SQLMap.entry("id", this.reference),
	    			SQLMap.entry("uuid", this.uuid),
	    			SQLMap.entry("by", this.by),
	    			SQLMap.entry("time", this.time),
	    			SQLMap.entry("until", this.until),
	    			SQLMap.entry("type", this.getType().name()),
	    			SQLMap.entry("reason", this.reason.name()),
	    			SQLMap.entry("evidence", Arrays.asList(this.evidence).toString()),
	    			SQLMap.entry("ip", this.ip),
	    			SQLMap.entry("silent", this.silent),
	    			SQLMap.entry("pardoned", this.pardoned)
	    			
	    	));
	    	
	    }
		
	}
	
	protected void load(long reference) throws SQLException {
		
		SQLDatabase connection = Fukkit.getConnectionHandler().getDatabase();
		SQLRowWrapper row = connection.getRow("flex_punishment", SQLCondition.where("id").is(this.reference = reference));
		
		if (row == null)
			return;
		
		try {
			this.reason = Reason.valueOf(row.getString("reason"));
		} catch (IllegalArgumentException e) {
			this.reason = Reason.OTHER;
		}
		
		this.duration = this.reason.getDuration();

		this.time = row.getLong("time");
		this.until = row.getLong("until");
		
		this.uuid = UUID.fromString(row.get("uuid").toString());
		this.by = UUID.fromString(row.get("by").toString());
		
		this.ip = row.getBoolean("ip");
		this.silent = row.getBoolean("silent");
		this.pardoned = row.getBoolean("pardoned");
		
		Collection<String> collect = CollectionUtils.toCollection(row.get("evidence"));
		
		String[] evidence = CollectionUtils.toCollection(row.get("evidence")).toArray(new String[collect.size()]);
		
		if (evidence != null)
			this.evidence = evidence;
		
	}

	public abstract void onConvict();

	public abstract void onBypassAttempt();

}
