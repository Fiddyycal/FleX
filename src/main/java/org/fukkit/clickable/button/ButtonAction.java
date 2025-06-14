package org.fukkit.clickable.button;

public enum ButtonAction {

	GUI_LEFT_CLICK, GUI_RIGHT_CLICK,
	
	GUI_DOUBLE_LEFT_CLICK, GUI_SHIFT_LEFT_CLICK, GUI_SHIFT_RIGHT_CLICK,
	
	GUI_CONTROL_DROP, GUI_DROP, GUI_MIDDLE_CLICK, GUI_NUMBER_PRESS, 
	
	GUI_CREATIVE_CLICK, GUI_WINDOW_BORDER_LEFT_CLICK, GUI_WINDOW_BORDER_RIGHT_CLICK,
	
	INTERACT_LEFT_CLICK_AIR, INTERACT_RIGHT_CLICK_AIR,
	
	INTERACT_LEFT_CLICK_BLOCK, INTERACT_RIGHT_CLICK_BLOCK,
	
	INTERACT_SHIFT_LEFT_CLICK_BLOCK, INTERACT_SHIFT_LEFT_CLICK_AIR,
	
	INTERACT_SHIFT_RIGHT_CLICK_BLOCK, INTERACT_SHIFT_RIGHT_CLICK_AIR,
	
	INTERACT_DROP,
	
	/*
	 * Reference: https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/event/block/Action.java
	 * Stepping onto or into a block (Ass-pressure).
     *
     * Examples:
     * Jumping on soil
     * Standing on pressure plate
     * Triggering redstone ore
     * Triggering tripwire
	 * 
	 */
	
	INTERACT_PHYSICAL, UNKNOWN, NONE;
	
	public boolean isCreativeClick() {
        return (this == GUI_MIDDLE_CLICK) || (this == GUI_CREATIVE_CLICK);
    }
	
    public boolean isRightClick() {
        return (this == GUI_RIGHT_CLICK) || (this == GUI_SHIFT_RIGHT_CLICK)
        		|| (this == INTERACT_RIGHT_CLICK_BLOCK) || (this == INTERACT_RIGHT_CLICK_AIR) || (this == INTERACT_SHIFT_RIGHT_CLICK_AIR) || (this == INTERACT_SHIFT_RIGHT_CLICK_BLOCK);
    }
    
    public boolean isLeftClick() {
        return (this == GUI_LEFT_CLICK) || (this == GUI_SHIFT_LEFT_CLICK) || (this == GUI_DOUBLE_LEFT_CLICK) || (this == GUI_CREATIVE_CLICK)
        		|| (this == INTERACT_LEFT_CLICK_BLOCK) || (this == INTERACT_LEFT_CLICK_AIR) || (this == INTERACT_LEFT_CLICK_AIR) || (this == INTERACT_SHIFT_LEFT_CLICK_BLOCK);
    }
    
    public boolean isShiftClick() {
        return (this == GUI_SHIFT_LEFT_CLICK) || (this == GUI_SHIFT_RIGHT_CLICK)
        		|| (this == INTERACT_SHIFT_LEFT_CLICK_AIR) || (this == INTERACT_SHIFT_LEFT_CLICK_BLOCK) || (this == INTERACT_SHIFT_RIGHT_CLICK_AIR) || (this == INTERACT_SHIFT_RIGHT_CLICK_BLOCK);
    }
    
    public boolean isPhysical() {
        return (this == INTERACT_PHYSICAL);
    }
    
    public boolean isDrop() {
        return (this == ButtonAction.GUI_DROP) || (this == ButtonAction.GUI_CONTROL_DROP);
    }
    
    public boolean isClick() {
    	return (this.isLeftClick()) || (this.isRightClick()) || (this.isShiftClick()) || (this.isCreativeClick());
    }
	
}
