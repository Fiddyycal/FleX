package org.fukkit.ai;

import org.bukkit.Material;
import org.fukkit.recording.RecordedAction;

public enum BotAnimation {
	
	DRAW_BOW(RecordedAction.USE_ITEM, RecordedAction.LAUNCH_PROJECTILE, Material.BOW),
	CAST_FISHING_ROD(RecordedAction.USE_ITEM, RecordedAction.STOP_USE_ITEM, Material.FISHING_ROD);
	
	private Material material;
	
	private RecordedAction start, end;
	
	BotAnimation(RecordedAction start, RecordedAction end, Material material) {
		this.start = start;
		this.end = end;
		this.material = material;
	}
	
	public RecordedAction getStartAction() {
		return this.start;
	}
	
	public RecordedAction getEndAction() {
		return this.end;
	}
	
	public Material getMaterial() {
		return this.material;
	}
	
}
