package org.fukkit.consequence;

import java.sql.SQLException;
import java.util.Set;

import org.fukkit.Fukkit;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.json.JsonBuffer;
import org.fukkit.json.JsonComponent;
import org.fukkit.theme.Theme;

import io.flex.commons.utils.NumUtils;

import net.md_5.bungee.api.chat.ClickEvent.Action;

public class Mute extends Punishment {

	public Mute(FleXPlayer player, FleXPlayer by, Reason reason, boolean ip, boolean silent, String... evidence) {
		super(player, by, reason, ip, silent, evidence);
	}
	
	private Mute(long reference) throws SQLException {
		super(reference);
	}
	
	public static Mute download(long reference) throws SQLException {
		return new Mute(reference);
	}
	
	@SuppressWarnings("unchecked")
	public static Set<Mute> download(FleXHumanEntity player) throws SQLException {
		return (Set<Mute>) Punishment.download(player, false, PunishmentType.MUTE);
	}

	@Override
	public PunishmentType getType() {
		return PunishmentType.MUTE;
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
		
		if (!player.isOnline())
			return;
		
		this.shutTheHoleInYourFace(true);
		
		boolean perm = this.duration >= NumUtils.YEAR_TO_MILLIS;
		
		String duration = perm ? "Permanent" : NumUtils.asString(this.duration);
		
		if (!this.silent) {
			
			Fukkit.getServerHandler().getOnlinePlayersUnsafe().stream().filter(p -> p != by).forEach(p -> {
				p.sendMessage(p.getTheme().format("<engine><failure>" + player.getDisplayName(p.getTheme()) + Theme.reset + " <failure>has been " + (perm ? "permanently" : "temporarily") + " muted<pp>."));
			});
			
		}
		
		Theme theme = by.getTheme();
		
		by.sendMessage(new String[] {
				
				theme.format("<engine><severe>&lYou have " + (perm ? "permanently" : "temporarily") + " muted" + Theme.reset + " " + player.getDisplayName(theme) + "<pp>."),
				theme.format("<engine><lore>Reason<sp>:" + Theme.reset + " <pc>" + this.reason),
				theme.format("<engine><lore>Offence<sp>:" + Theme.reset + " <pc>" + this.reason.getCategory()),
				theme.format("<engine><lore>Evidence<sp>:" + Theme.reset + " <pc>" + (this.evidence != null && this.evidence.length != 0 ? this.evidence[0] : "None")),
				theme.format("<engine><lore>Severity<sp>:" + Theme.reset + " " + this.severity())
				
		});
		
		if (!perm)
			by.sendMessage(theme.format("<engine><lore>Duration<sp>:" + Theme.reset + " <pc>" + duration));
		
	}
	
	@Override
	public void onBypassAttempt() {
		this.shutTheHoleInYourFace(false);
	}
	
	private void shutTheHoleInYourFace(boolean initial) {

		FleXPlayer player = this.getPlayer();
		
		if (!player.isOnline())
			return;
		
		boolean perm = this.duration >= NumUtils.YEAR_TO_MILLIS;
		
		String duration = perm ? "Permanent" : NumUtils.asString(initial ? this.duration : this.getRemaining());
		
		Theme theme = player.getTheme();
		
		player.sendMessage(new String[] {
				
				theme.format("<engine><severe>&lYou have been " + (perm ? "permanently" : "temporarily") + " muted."),
				theme.format("<engine><lore>Reason<sp>:" + Theme.reset + " <pc>" + this.reason),
				theme.format("<engine><lore>Offence<sp>:" + Theme.reset + " <pc>" + this.reason.getCategory()),
				theme.format("<engine><lore>Evidence<sp>:" + Theme.reset + " <pc>" + (this.evidence != null && this.evidence.length != 0 ? this.evidence[0] : "None")),
				theme.format("<engine><lore>Severity<sp>:" + Theme.reset + " " + this.severity())
				
		});
		
		if (!perm)
			player.sendMessage(theme.format("<engine><lore>Duration<sp>:" + Theme.reset + " <pc>" + duration));
		
		JsonBuffer buffer = new JsonBuffer();
		
		buffer = buffer.append(new JsonComponent(theme.format("<engine><lore>Was this mute mishandled<pp>?\\s<lore>Dispute it ")));
		buffer = buffer.append(new JsonComponent(theme.format("<clickable>here"))
				
				.onHover(theme.format("Dispute your active <interactable>mute<pp>."))
				.onClick(Action.OPEN_URL, "http://dispute.luminous.gg/"));
		
		buffer = buffer.append(new JsonComponent(theme.format("<pp>.")));
		
		player.sendJsonMessage(buffer);
		
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
