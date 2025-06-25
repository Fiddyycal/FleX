package org.fukkit.consequence;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.fukkit.Fukkit;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;

import io.flex.commons.cache.LinkedCache;
import io.flex.commons.sql.SQLCondition;
import io.flex.commons.sql.SQLDatabase;
import io.flex.commons.sql.SQLRowWrapper;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.NumUtils;
import io.flex.commons.utils.StringUtils;

public class Ban extends Punishment {
	
	/**
	 * 
	 * Local cache for bans so database isn't being
	 * stressed everytime someone attempts to connect.
	 * 
	 * @see ConvictionListeners
	 *
	 */
	public static class BanCache extends LinkedCache<Ban, UUID> {
		
		private static final long serialVersionUID = -4132115098896228306L;
		
		public BanCache() {
			super((con, uid) -> con.uuid.equals(uid));
		}
		
		@SuppressWarnings("unchecked")
		public <C extends Consequence> C getByPlayer(FleXPlayer player) {
			return (C) this.stream().filter(b -> b.uuid.equals(player.getUniqueId())).findFirst().orElse(null);
		}
		
		@Override
		public boolean load() {
			
			SQLDatabase database = Fukkit.getConnectionHandler().getDatabase();
			
			try {
				
				for (SQLRowWrapper row : database.getRows("flex_punishment", SQLCondition.where("type").is(PunishmentType.BAN.name())))
					this.add(Ban.download(row.getLong("id")));
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return true;
			
		}
		
	}
	
	public Ban(FleXPlayer player, FleXPlayer by, Reason reason, boolean ip, boolean silent, String... evidence) {
		super(player, by, reason, ip, silent, evidence);
	}
	
	private Ban(long reference) throws SQLException {
		super(reference);
	}
	
	public static Ban download(long reference) throws SQLException {
		return new Ban(reference);
	}
	
	@Override
	public PunishmentType getType() {
		return PunishmentType.BAN;
	}

	@Override
	public void onConvict() {
		
		try {
			this.upload();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		FleXPlayer player = this.getPlayer();
		FleXPlayer by = this.getBy();
		
		boolean perm = this.getDuration() >= NumUtils.YEAR_TO_MILLIS;
		
		String duration = perm ? "Permanent" : NumUtils.asString(this.getDuration());
		
		this.gtfoAndDontComeBack(true);

		if (!this.silent) {
			
			Fukkit.getServerHandler().getOnlinePlayersUnsafe().stream().filter(p -> p != by).forEach(p -> {
				p.sendMessage(p.getTheme().format("<engine><failure>" + player.getDisplayName(p.getTheme()) + Theme.reset + " <failure>has been " + (perm ? "permanently" : "temporarily") + " banned<pp>."));
			});
			
		}
		
		Theme theme = by.getTheme();
		
		by.sendMessage(new String[] {

				theme.format("<engine><severe>&lYou have " + (perm ? "permanently" : "temporarily") + " banned" + Theme.reset + " " + player.getDisplayName(theme) + "<pp>."),
				theme.format("<engine><lore>Reason<sp>:" + Theme.reset + " <pc>" + this.reason),
				theme.format("<engine><lore>Offence<sp>:" + Theme.reset + " <pc>" + this.reason.getCategory()),
				theme.format("<engine><lore>Evidence<sp>:" + Theme.reset + " <pc>" + (this.evidence != null && this.evidence.length != 0 ? this.evidence[0] : "None")),
				theme.format("<engine><lore>Severity<sp>:" + Theme.reset + " " + this.severity()),
				theme.format("<engine><lore>Duration<sp>:" + Theme.reset + " <pc>" + duration)
				
		});
		
	}
	
	@Override
	public void onBypassAttempt() {
		this.gtfoAndDontComeBack(false);
	}
	
	public void onPreBypassAttempt(FleXPlayer player, AsyncPlayerPreLoginEvent event) {
		
		event.setLoginResult(Result.KICK_BANNED);
		
		event.setKickMessage(StringUtils.join(ArrayUtils.add(new String[] { "", "" }, Arrays.stream(this.message(player, false)).filter(l -> l != null).toArray()), "\n"));
		
	}
	
	private void gtfoAndDontComeBack(boolean initial) {
		
		FleXPlayer player = this.getPlayer();
		
		if (!player.isOnline())
			return;
		
		player.kick(this.message(player, initial));
		
	}
	
	private String[] message(FleXPlayer player, boolean initial) {
		
		boolean perm = this.duration >= NumUtils.YEAR_TO_MILLIS;
		
		String duration = perm ? "Permanent" : NumUtils.asString(initial ? this.duration : this.getRemaining());
		
		Theme theme = player.getTheme();
		
		return new String[] {
				
				theme.format("<severe>&lYou have been " + (perm ? "permanently" : "temporarily") + " &nbanned<severe>&l from the network<pp>."),
				"",
			    theme.format("<lore>Standing reason<sp>:" + Theme.reset + " <failure>" + this.getReason()),
			    theme.format("<lore>Severity level and offence<sp>:" + Theme.reset + " <failure>" + this.getReason().getCategory() + "\\s" + this.severity()),
			    theme.format("<lore>Punishment evidence<sp>:" + Theme.reset + " <failure>" + (this.evidence != null && this.evidence.length != 0 ? this.evidence[0] : "None")),
			    !perm ? theme.format("<lore>Upheld for<sp>:" + Theme.reset + " <failure>" + duration) : null,
			    "",
			    theme.format("<lore>If you believe you were unfairly punished<sp>," + Theme.reset + " <lore>or this was in error"),
			    theme.format("<lore>feel free to dispute your punishment at <sc>&ndispute.luminous.gg")
			    
		};
		
	}
	
	private String severity() {
		
		boolean perm = this.duration >= NumUtils.YEAR_TO_MILLIS;
		
		String severity = "<severe>&l||||||||||";
		
		if (!perm) {
			
			if (this.duration <= NumUtils.YEAR_TO_MILLIS)
				severity = "<severe>&l|||||||&8&l|||";
			
			if (this.duration <= NumUtils.MONTH_TO_MILLIS)
				severity = "<severe>&l|||||&8&l|||||";
			
			if (this.duration <= NumUtils.DAY_TO_MILLIS)
				severity = "<severe>&l|||&8&l|||||||";
			
			if (this.duration <= NumUtils.HOUR_TO_MILLIS)
				severity = "<severe>&l||&8&l||||||||";
			
			if (this.duration <= NumUtils.MINUTE_TO_MILLIS)
				severity = "<severe>&l|&8&l|||||||||";
			
		}
		
		return severity;
		
	}

}
