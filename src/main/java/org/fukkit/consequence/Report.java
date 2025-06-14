package org.fukkit.consequence;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.fukkit.Fukkit;
import org.fukkit.PlayerState;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.fle.flow.OverwatchReplay;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.ThemeUtils;

import io.flex.commons.Nullable;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;

public class Report extends Conviction {
	
	public Report(FleXPlayer player, FleXPlayer by, Reason reason, String... evidence) {
		super(player, by, reason, false, true, evidence);
	}
	
	private Report(long reference) throws SQLException {
		super(reference);
	}
	
	public static Report download(long reference) throws SQLException {
		return new Report(reference);
	}
	
	public static Report[] download() throws SQLException {
		return download((SQLCondition<?>)null);
	}
	
	public static Report[] download(FleXHumanEntity player) throws SQLException {
		
		Set<Report> convictions = new LinkedHashSet<Report>();
		
		SQLDatabase database = Fukkit.getConnectionHandler().getDatabase();

		for (SQLRowWrapper row : database.getRows("flex_punishment", SQLCondition.where("uuid").is(player.getUniqueId()))) {
			
			if (ConvictionType.valueOf(row.getString("type")) != ConvictionType.REPORT)
				continue;
			
			convictions.add(new Report(row.getLong("id")));
			
		}
		
		return convictions.toArray(new Report[convictions.size()]);
		
	}
	
	public static Report[] download(@Nullable SQLCondition<?> condition) throws SQLException {
		
		Set<Report> convictions = new LinkedHashSet<Report>();

		SQLDatabase database = Fukkit.getConnectionHandler().getDatabase();

		for (SQLRowWrapper row : database.getRows("flex_punishment", condition)) {
			
			if (ConvictionType.valueOf(row.getString("type")) != ConvictionType.REPORT)
				continue;
			
			convictions.add(new Report(row.getLong("id")));
			
		}
		
		return convictions.toArray(new Report[convictions.size()]);
		
	}
	
	public static Report[] download(FleXPlayer player) throws SQLException {
		return download(player, false);
	}
	
	public static Report[] download(FleXPlayer player, boolean outgoing) throws SQLException {
		return download(SQLCondition.where(outgoing ? "by" : "uuid").is(player.getUniqueId()));
	}

	@Override
	public ConvictionType getType() {
		return ConvictionType.REPORT;
	}
	
	@Override
	public void onConvict() {
		
		try {
			this.upload();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.getBy().sendMessage(ThemeMessage.REPORT_SUCCESS.format(this.getBy().getTheme(), this.getBy().getLanguage(), ThemeUtils.getNameVariables(this.getPlayer(), this.getBy().getTheme())));
		
		if (!Fukkit.getFlowLineEnforcementHandler().isFlowEnabled())
			return;
		
		FleXPlayer player = this.getPlayer();
		
		if (!player.isOnline() || player.getState() != PlayerState.INGAME) {
			
			Fukkit.getFlowLineEnforcementHandler().setPending(player, true);
			
			// TODO:
			this.getBy().sendMessage("[ThemeMessage=FLOW_RECORDING_PENDING]");
			
		} else this.watchLikeAFuckinHawk(true);
		
	}
	
	@Override
	public void onBypassAttempt() {
		this.watchLikeAFuckinHawk(false);
	}
	
	private void watchLikeAFuckinHawk(boolean initial) {
		
		// TODO Place new recording logic here, NOTE: MAKE SURE- that if more than 1 report is made, only 1 recording is made and reference that for all reports.
		// ^^^^^^^^^^^^ just check if a recording is already started............ FLE.isRecording(), make sure setRecording is set when stage recording starts.
		// TODO world copy logic.
		
		if (Fukkit.getFlowLineEnforcementHandler().isRecording(this.getPlayer()))
			return;
		
		System.out.println("Starting recording............................ 1");
		
		Fukkit.getFlowLineEnforcementHandler().setRecording(this.getPlayer(), true);
		
		new OverwatchReplay(this);
		
		if (initial && this.getBy().isOnline())
			this.getBy().sendMessage(ThemeMessage.FLOW_RECORDING_STARTED.format(this.getBy().getTheme(), this.getBy().getLanguage(), ThemeUtils.getNameVariables(this.getPlayer(), this.getBy().getTheme())));
		
	}

}
