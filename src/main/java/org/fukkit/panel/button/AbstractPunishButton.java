package org.fukkit.panel.button;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.consequence.Punishment;
import org.fukkit.consequence.PunishmentType;
import org.fukkit.consequence.Report;
import org.fukkit.consequence.gui.SanctionGui;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.entity.FleXPlayerNotLoadedException;
import org.fukkit.theme.Theme;

import io.flex.commons.file.Language;

public abstract class AbstractPunishButton extends ExecutableButton {
	
	private static final long serialVersionUID = 2984971389617295534L;
	
	protected FleXPlayer other;
	
	private PunishmentType convictionType;
	
	public AbstractPunishButton(Material material, Theme theme, Language language, FleXPlayer other, PunishmentType convictionType) {
		
		super(
				
				material,
				theme.format("<title>" + (convictionType == PunishmentType.REPORT ? "History<sp>:\\s<pc>" + convictionType + "s" : "Punish<sp>:\\s<pc>" + convictionType)),
				lore(theme, other, convictionType));
		
		this.other = other;
		
		this.convictionType = convictionType;
		
	}
	
	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action) {
		
		if (!action.isClick())
			return false;
		
		boolean ip = action.isShiftClick();
		boolean silent = action.isRightClick();
		
		if ((this.convictionType == PunishmentType.KICK || this.convictionType == PunishmentType.REPORT) && ip)
			return false;
		
		if (this.convictionType == PunishmentType.REPORT && silent) {
			
			try {
				
				List<String> all = Report.download(this.other, true).stream().map(p -> {
					
					SimpleDateFormat format = new SimpleDateFormat("(dd/MM/yy) [hh:mm:ss z]");
					
					return format.format(new Date(p.getTime())) + " " + p.getReason();
					
				}).collect(Collectors.toList());
				
				player.closeMenu();
				player.sendMessage(all.toArray(new String[all.size()]));
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return true;
			
		}
		
		if (action == ButtonAction.GUI_MIDDLE_CLICK || (action == ButtonAction.GUI_LEFT_CLICK && this.convictionType == PunishmentType.REPORT)) {
			
			List<String> all;
			
			try {
				
				all = this.asSet().stream().map(p -> {
					
					SimpleDateFormat format = new SimpleDateFormat("(dd/MM/yy) [hh:mm:ss z]");
					
					return format.format(new Date(p.getTime())) + " " + p.getReason();
					
				}).collect(Collectors.toList());
				
			} catch (FleXPlayerNotLoadedException e) {
				
				e.printStackTrace();
				
				all = new ArrayList<String>();
				
			}
			
			player.closeMenu();
			player.sendMessage(all.toArray(new String[all.size()]));
			return true;
			
		}
		
		player.openMenu(new SanctionGui(player, this.other, this.convictionType, ip, silent), false);
		return true;
		
	}
	
	private static String[] lore(Theme theme, FleXPlayer other, PunishmentType convictionType) {
		
		List<String> lore = new ArrayList<String>();
		List<String> latest = last5(theme, other, convictionType);

		if (convictionType != PunishmentType.REPORT)
			lore.add(theme.format("<lore>" + convictionType + " <sc>" + other.getDisplayName(theme, true).replace(ChatColor.RESET.toString(), ChatColor.WHITE.toString()) + "\\s<lore>" + (convictionType == PunishmentType.MUTE ? "on" : "from") + " the network<pp>."));
		
		else lore.add(theme.format("<lore>View <sc>" + other.getDisplayName(theme, true).replace(ChatColor.RESET.toString(), ChatColor.WHITE.toString()) + "<lore>'s report history<pp>."));
		
		if (convictionType == PunishmentType.REPORT) {
			lore.add("");
			lore.add(theme.format("<tc>Action<sp>/<tc>Location history logged and provided by <spc>FloW<pp>."));
		}
		
		lore.add("");
		lore.add(theme.format("<spc>Recent " + convictionType.toString().toLowerCase() + " history<pp>:" + Theme.reset +
				(convictionType != PunishmentType.KICK ? convictionType == PunishmentType.REPORT ? " <pp>(<success>Pardoned<sp>/<failure>Expired<sp>/<severe>Active<pp>)" : " <pp>(<success>Pardoned<sp>/<failure>Expired<sp>/<severe>Active<pp>)" : "")));
		
		if (!latest.isEmpty())
			lore.addAll(latest);
			
		else lore.add(theme.format("<failure>No recent " + convictionType.toString().toLowerCase() + " history found<pp>."));
		
		lore.add("");

		if (convictionType != PunishmentType.REPORT) {
			
			lore.add(theme.format("<sp>&oMiddle Click<pp>:\\s<sc>Show more<pp>."));
			lore.add(theme.format("<sp>&oLeft Click<pp>:\\s<sc>Standard " + convictionType.toString().toLowerCase() + "<pp>.\\s<sp>(<spc>Flags<sp>:\\s<tc>None<sp>)"));
			lore.add(theme.format("<sp>&oRight Click<pp>:\\s<sc>Silent " + convictionType.toString().toLowerCase() + "<pp>.\\s<sp>(<spc>Flags<sp>:\\s<tc>-s<sp>)"));
			
			if (convictionType != PunishmentType.KICK) {
				
				lore.add(theme.format("<sp>&o[Shift] + Left Click<pp>:\\s<sc>IPv4 " + convictionType.toString().toLowerCase() + "<pp>.\\s<sp>(<spc>Flags<sp>:\\s<tc>-i<sp>)"));
				lore.add(theme.format("<sp>&o[Shift] + Right Click<pp>:\\s<sc>Silent IPv4 " + convictionType.toString().toLowerCase() + "<pp>.\\s<sp>(<spc>Flags<sp>:\\s<tc>-i<sp>,\\s<tc>-s<sp>)"));
				
			}
			
		} else {
			
			lore.add(theme.format("<sp>&oLeft Click<pp>:\\s<sc>Show more reports<pp>."));
			lore.add(theme.format("<sp>&oRight Click<pp>:\\s<sc>Show reported<pp>."));
			
		}
		
		return lore.toArray(new String[lore.size()]);
		
	}
	
	private static List<String> last5(Theme theme, FleXPlayer other, PunishmentType convictionType) {
		
		Set<Punishment> convictions;
		
		try {
			
			convictions = other.getHistory().getPunishments().asMap().entrySet().stream().map(e -> e.getValue()).filter(c -> {
				return c.getType() == convictionType;
			}).collect(Collectors.toSet());
			
		} catch (FleXPlayerNotLoadedException e) {
			
			e.printStackTrace();
			
			return new ArrayList<String>();
			
		}
		
		List<Long> times = convictions.stream().map(p -> p.getTime()).sorted(/*Collections.reverseOrder()*/).collect(Collectors.toList());
		List<String> latest = new LinkedList<String>();
		
		SimpleDateFormat format = new SimpleDateFormat("(dd/MM/yy) [hh:mm:ss]");
		
		IntStream.range(0, 5).forEach(i -> {
			
			if (times.size() <= i || times.get(i) == null)
				return;
			
			Punishment conviction = convictions.stream().filter(b -> b.getTime() == times.get(i)).findFirst().orElse(null);
			
			String dateTime = format.format(new Date(conviction.getTime()))
			
			        .replace("(", "<pp>(<tc>")
					.replace("/", "<sp>/<tc>")
					.replace(")", "<pp>)" + Theme.reset)
					
					.replace("[", "<pp>[<spc>")
					.replace(":", "<sp>:<spc>")
			        .replace("]", "<pp>]" + Theme.reset);
			
			String line = dateTime + (conviction.isIp() ? "<severe>IPv4" + Theme.reset + " " : " ")
					+ (conviction.isPardoned() ? "<success>" : !conviction.isActive() ? "<failure>" : "<severe>") + conviction.getReason() + " <sp>&o(" + conviction.getReason().getCategory() + ")";
			
			latest.add(theme.format(line));
			
		});
		
		return latest;
		
	}
	
	public abstract <T extends Punishment> Set<T> asSet() throws FleXPlayerNotLoadedException;

}
