package org.fukkit.consequence.gui;

import java.util.Arrays;
import java.util.List;

import org.fukkit.Fukkit;
import org.fukkit.clickable.Menu;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.consequence.PunishmentType;
import org.fukkit.consequence.EvidenceType;
import org.fukkit.consequence.Reason;
import org.fukkit.consequence.gui.button.ReasonButton;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.metadata.FleXFixedMetadataValue;
import org.fukkit.theme.Theme;

import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.NumUtils;

public class SanctionGui extends Menu {
	
	private PunishmentType consequenceType;
	
	public SanctionGui(FleXPlayer player, FleXPlayer threatened, PunishmentType consequenceType, boolean ip, boolean silent) {
		
		super(player.getTheme().format("<title>" + consequenceType.toString() + "<pp>: <sc>" + threatened.getName()), determineRows(consequenceType));
		
		this.consequenceType = consequenceType;
		
		if (ip)
			this.setMetadata("punishment_ip", new FleXFixedMetadataValue(true));
		
		if (silent)
			this.setMetadata("punishment_silent", new FleXFixedMetadataValue(true));
		
		Theme theme = player.getTheme();
		
		for (Reason reason : Reason.values()) {
			
			if (reason.isFlowVerdict())
				continue;
			
			if (consequenceType != PunishmentType.REPORT && reason.getConvictionType() != null && reason.getConvictionType() != consequenceType)
				continue;
			
			ExecutableButton button = new ReasonButton(theme, threatened, reason);
			List<String> lore = button.getLore();
			
			if (consequenceType != PunishmentType.REPORT) {
				
				if (!lore.get(lore.size() - 1).contains("Example"))
					lore.add("");
				
				if (consequenceType != PunishmentType.KICK && reason.getDuration() > 0) {
					String duration = reason.getDuration() >= NumUtils.YEAR_TO_MILLIS ? "Permanent" : NumUtils.toString(reason.getDuration());
					lore.add(theme.format("<tc>Duration<pp>:" + Theme.reset + " <lore>" + duration + (this.hasMetadata("punishment_ip") ? " IPv4" : "")));
				}
				
				lore.add(theme.format("<tc>Evidence Required<pp>:" + Theme.reset + " " + (reason.isEvidenceRequired() ? "" : "<failure>No evidence required<pp>.")));
				
				for (EvidenceType evidence : reason.getRequiredEvidence())
					lore.add(theme.format("<sp>*" + Theme.reset + " <success>" + evidence.toString().replace("(Automatic)", "<tc>&o(Automatic)")));
				
				if (reason == Reason.OTHER && reason.getRequiredEvidence() != null && reason.getRequiredEvidence().length > 0) {
					
					lore.add(theme.format("<sp>*" + Theme.reset + " <success>Verbal Confirmation"));
					lore.add(theme.format("<sp>*" + Theme.reset + " <success>Conversation Snippet from 2012"));
					lore.add(theme.format("<sp>*" + Theme.reset + " <success>A Potato"));
					
				}
				
			} else {
				
				if (Fukkit.getFlowLineEnforcementHandler().isFlowEnabled() && ArrayUtils.contains(reason.getRequiredEvidence(), EvidenceType.RECORDING_REFERENCE)) {
					lore.add("");
					lore.add(theme.format("<pp>&o[&6&oBeta<pp>&o]\\s&d&oThis will stage the FleX Overwatch System <pp>&o(&5&oFloW<pp>&o)"));
					lore.add(theme.format("&d&oto save a recording of the players actions as well as the"));
					lore.add(theme.format("&d&oactions of the surrounding bystanders for reviewal<pp>."));
				}
				
				if (ArrayUtils.contains(reason.getRequiredEvidence(), EvidenceType.CHAT_LOG)) {
					lore.add("");
					lore.add(theme.format("<spc>&oThis will give the FleX Overwatch System <pp>&o(<tc>&oFloW<pp>&o)\\s<spc>permission to"));
					lore.add(theme.format("<spc>&orecord and save a snapshot of the servers chat log, highlighting"));
					lore.add(theme.format("<spc>&othe offending player for reviewal by a staff member<pp>."));
				}
				
			}
			
			if (this.hasMetadata("punishment_ip")) {
				lore.add("");
				lore.add(theme.format("<severe>&oYou have used the ip punishment" + Theme.reset + " <pp>(<failure>-i<pp>)" + Theme.reset + " <severe>flag<pp>."));
				lore.add(theme.format("<severe>&oThis punishment will carry out across all accounts<pp>."));
			}
			
			if (this.hasMetadata("punishment_silent")) {
				lore.add("");
				lore.add(theme.format("<spc>&oYou have used the silent" + Theme.reset + " <pp>(<sc>-s<pp>)" + Theme.reset + " <spc>flag<pp>."));
				lore.add(theme.format("<spc>&oThis punishment will not be announced publicly<pp>."));
			}
			
			lore.add("");
			lore.add(theme.format("<sp>&oClick<pp>:\\s<sc>Select<pp>."));
			
			button.setLore(lore.toArray(new String[lore.size()]));
			
			this.addButton(button);
			
		}
		
	}
	
	public PunishmentType getConsequenceType() {
		return this.consequenceType;
	}
	
	private static int determineRows(PunishmentType consequenceType) {
		
		if (consequenceType == PunishmentType.REPORT)
			return 3;
		
		long items = Arrays.stream(Reason.values()).filter(r -> {
			return !r.isFlowVerdict() && r.getConvictionType() != null && r.getConvictionType() == consequenceType;
		}).count();
		
		return items > 18 ? 3 : items > 9 ? 2 : 1;
	}

}
