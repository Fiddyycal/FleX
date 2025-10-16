package org.fukkit.consequence;

import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.fukkit.Fukkit;
import org.fukkit.PlayerState;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.flow.Overwatch;
import org.fukkit.theme.Theme;
import org.fukkit.theme.ThemeMessage;
import org.fukkit.utils.BukkitUtils;
import org.fukkit.utils.ThemeUtils;
import org.fukkit.world.FleXWorld;

import io.flex.commons.Nullable;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;

public class Report extends Punishment {
	
	public Report(FleXPlayer player, FleXPlayer by, Reason reason, String... evidence) {
		super(player, by, reason, false, true, evidence);
	}
	
	private Report(long reference) throws SQLException {
		super(reference);
	}
	
	public static Report download(long reference) throws SQLException {
		return new Report(reference);
	}
	
	public static Set<Report> download() throws SQLException {
		return download((SQLCondition<?>)null);
	}
	
	public static Set<Report> download(@Nullable SQLCondition<?> condition) throws SQLException {

		SQLDatabase database = Fukkit.getConnectionHandler().getDatabase();
		
		Set<Report> convictions = new LinkedHashSet<Report>();
		
		for (SQLRowWrapper row : database.getRows("flex_punishment", condition)) {
			
			PunishmentType check = null;
			
			try {
				check = PunishmentType.valueOf(row.getString("type"));
			} catch (IllegalArgumentException ignore) {
				continue;
			}
			
			if (check != PunishmentType.REPORT)
				continue;
			
			long reference = row.getLong("id");
			
			convictions.add(download(reference));
			
		}
		
		return convictions;
		
	}
	
	@SuppressWarnings("unchecked")
	public static Set<Report> download(FleXHumanEntity player) throws SQLException {
		return (Set<Report>) Punishment.download(player, false, PunishmentType.REPORT);
	}

	@Override
	public PunishmentType getType() {
		return PunishmentType.REPORT;
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
		
		Theme theme = this.getBy().getTheme();
		
		if (!player.isOnline() || player.getState() != PlayerState.INGAME) {
			
			BukkitUtils.asyncThread(() -> {
				try {
					Fukkit.getFlowLineEnforcementHandler().setPending(player);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});
			
			// TODO: [FLOW_RECORDING_PENDING]
			this.getBy().sendMessage(theme.format("<flow><pc>FloW has been staged to monitor<reset> <sc>" + player.getDisplayName(theme) + "<pp>."));
			
		} else this.watchLikeAFuckinHawk(true);
		
	}
	
	@Override
	public void onBypassAttempt() {
		this.watchLikeAFuckinHawk(false);
	}
	
	private void watchLikeAFuckinHawk(boolean instant) {
		
		BukkitUtils.asyncThread(() -> {
			
			try {

				FleXPlayer player = this.getPlayer();
				FleXWorld world = player.getWorld();
				FleXPlayer[] record = null;
				
				Fukkit.getFlowLineEnforcementHandler().setRecording(player);
				
				if (world != null) {
					
					record = world.getOnlinePlayers()
							
							.stream()
							.filter(p -> p.getState() == PlayerState.INGAME)
							.toArray(FleXPlayer[]::new);
					
				} else {
					
					record = Fukkit.getOnlinePlayers()
							
							.stream()
							.filter(p -> p.getState() == PlayerState.INGAME)
							.toArray(FleXPlayer[]::new);
					
				}
				
				if (record != null && record.length > 0)
					watch(this, record);
				
			} catch (FileAlreadyExistsException ignore) {
				System.err.println("Overwatch file already exists, this may be due to multiple reports of the same person. You can ignore this.");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			BukkitUtils.mainThread(() -> {
				
				FleXPlayer by = this.getBy();
				
				if (by.isOnline())
					by.sendMessage(ThemeMessage.FLOW_RECORDING_STARTED.format(by.getTheme(), by.getLanguage(), ThemeUtils.getNameVariables(this.getPlayer(), by.getTheme())));
				
			});
			
		});
		
	}
	
	public static synchronized void watch(Report report, FleXPlayer... players) throws FileAlreadyExistsException {
		new Overwatch(report, players).start(report.getPlayer().getPlayer().getWorld(), 400L, players);
	}

}
