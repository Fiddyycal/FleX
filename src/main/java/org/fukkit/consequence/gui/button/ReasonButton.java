package org.fukkit.consequence.gui.button;

import org.fukkit.Fukkit;
import org.fukkit.clickable.button.ButtonAction;
import org.fukkit.clickable.button.ExecutableButton;
import org.fukkit.consequence.PunishmentType;
import org.fukkit.consequence.Consequence;
import org.fukkit.consequence.Reason;
import org.fukkit.consequence.gui.SanctionGui;
import org.fukkit.entity.FleXPlayer;
import org.fukkit.event.consequence.FleXPreConsequenceEvent;
import org.fukkit.theme.Theme;

import io.flex.commons.utils.ArrayUtils;

public class ReasonButton extends ExecutableButton {

	private static final long serialVersionUID = 8853569532472199379L;
	
	private FleXPlayer threatened;
	
	private Reason reason;
	
	public ReasonButton(Theme theme, FleXPlayer threatened, Reason reason) {
		
		super(reason.getMaterial(), theme.format("<title>" + reason.toString().replace(":", "<pp>:" + Theme.reset + " <failure>")), 1, (short) 0);
		
		this.threatened = threatened;
		
		this.reason = reason;
		
		String[] lore = new String[reason.getDescriptionAsLore().length + 2];
		
		lore[0] = theme.format("<subtitle>" + reason.getCategory());
		lore[1] = "";
		
		for (int i = 2; i < lore.length; i++)
			lore[i] = theme.format("<lore>" + reason.getDescriptionAsLore()[i - 2] + (i == lore.length - 1 ? "<pp>." : ""));
			
		if (reason.getExample() != null) {
			lore = ArrayUtils.add(lore, "");
			lore = ArrayUtils.add(lore, theme.format("<tc>Example<pp>:" + Theme.reset + " <lore>" + reason.getExample().replace("%name%", threatened.getName()) + (!reason.getExample().endsWith("\"") ? "<pp>." : "")));
		}
		
		this.setLore(lore);
		
	}

	@Override
	public boolean onExecute(FleXPlayer player, ButtonAction action) {
		
		if (action.isClick()) {
			
			SanctionGui menu = (SanctionGui) this.getClickable();
			PunishmentType type = menu.getConsequenceType();
			
			Consequence consequence = new Consequence(this.threatened, player, this.reason, menu.hasMetadata("punishment_ip"), menu.hasMetadata("punishment_silent")) {
				
				@Override
		        public PunishmentType getType() {
				    return type;
			    }
				
		    };
			
			FleXPreConsequenceEvent event = new FleXPreConsequenceEvent(consequence, false);
			
			Fukkit.getEventFactory().call(event);
			
			if (!event.isCancelled()) {
				player.closeMenu();
				return true;
			}
			
			return false;
			
		}
			
		return false;
		
	}

}
