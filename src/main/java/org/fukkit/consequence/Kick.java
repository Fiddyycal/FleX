package org.fukkit.consequence;

import java.sql.SQLException;
import java.util.Set;

import org.fukkit.Fukkit;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;

public class Kick extends Conviction {

	public Kick(FleXPlayer player, FleXPlayer by, Reason reason, boolean silent, String... evidence) {
		super(player, by, reason, false, silent, evidence);
	}
	
	private Kick(long reference) throws SQLException {
		super(reference);
	}
	
	public static Kick download(long reference) throws SQLException {
		return new Kick(reference);
	}
	
	@SuppressWarnings("unchecked")
	public static Set<Kick> download(FleXHumanEntity player) throws SQLException {
		return (Set<Kick>) Conviction.download(player, false, ConvictionType.KICK);
	}

	@Override
	public ConvictionType getType() {
		return ConvictionType.KICK;
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
		
		this.gtfo();
		
		if (!this.silent) {
			
			Fukkit.getServerHandler().getOnlinePlayersUnsafe().stream().filter(p -> p != by).forEach(p -> {
				p.sendMessage(p.getTheme().format("<engine><failure>" + player.getDisplayName(p.getTheme()) + Theme.reset + " <failure>has been kicked<pp>."));
			});
			
		}
		
		Theme theme = by.getTheme();
		
		by.sendMessage(new String[] {

				theme.format("<engine><failure>You have kicked" + Theme.reset + " " + player.getDisplayName(theme) + "<pp>."),
				theme.format("<engine><lore>Reason<sp>:" + Theme.reset + " <pc>" + this.getReason()),
				theme.format("<engine><lore>Evidence<sp>:" + Theme.reset + " <pc>" + (this.getEvidence() != null && this.getEvidence().length != 0 ? this.getEvidence()[0] : "None"))
				
		});
		
	}
	
	@Override
	public void onBypassAttempt() {
		/**
		 * Players can bypass kicks.
		 */
	}
	
	private void gtfo() {
		
		FleXPlayer player = this.getPlayer();
		
		if (!player.isOnline())
			return;
		
		Theme theme = player.getTheme();
		
		player.kick(null,
					
				theme.format("<severe>&lYou have been &nkicked<severe>&l from the network<pp>."),
				"",
				theme.format("<lore>Standing reason<sp>:" + Theme.reset + " <failure>" + this.getReason() + Theme.reset + " <sp>&o(" + this.getReason().getCategory() + ")"),
				this.hasEvidence() ? theme.format("<lore>Evidence<sp>:" + Theme.reset + " <failure>" + this.getEvidence()[0]) : null,
				"",
				theme.format("<lore>If you believe you were unfairly punished<sp>," + Theme.reset + " <lore>or this was in error"),
			    theme.format("<lore>feel free to dispute your punishment at <sc>&ndispute.luminous.gg"));
		
	}

}
