package net.md_5.fungee.event;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.fukkit.entity.FleXPlayer;

public class FleXPlayerDeathEvent extends FleXEntityDeathEvent {
	
    private int newExp = 0;
    
    private String deathMessage = "";
    
    private int newLevel, newTotalExp = 0;
    
    private boolean keepLevel, keepInventory = false;

    public FleXPlayerDeathEvent(final FleXPlayer player, final List<ItemStack> drops, final int droppedExp, final String deathMessage) {
        this(player, drops, droppedExp, 0, deathMessage);
    }

    public FleXPlayerDeathEvent(final FleXPlayer player, final List<ItemStack> drops, final int droppedExp, final int newExp, final String deathMessage) {
        this(player, drops, droppedExp, newExp, 0, 0, deathMessage);
    }

    public FleXPlayerDeathEvent(final FleXPlayer player, final List<ItemStack> drops, final int droppedExp, final int newExp, final int newTotalExp, final int newLevel, final String deathMessage) {
    	
        super(player, drops, droppedExp);
        
        this.newExp = newExp;
        this.newTotalExp = newTotalExp;
        this.newLevel = newLevel;
        this.deathMessage = deathMessage;
        
        if (this.deathMessage != null)
        	this.entity.getWorld().getOnlinePlayers().stream().forEach(p -> p.sendMessage(this.deathMessage));
        
    }

    @Override
    public FleXPlayer getEntity() {
        return (FleXPlayer) this.entity;
    }
    
    public void setDeathMessage(String deathMessage) {
        this.deathMessage = deathMessage;
    }

    public String getDeathMessage() {
        return this.deathMessage;
    }

    public int getNewExp() {
        return this.newExp;
    }

    public void setNewExp(int exp) {
    	this.newExp = exp;
    }

    public int getNewLevel() {
        return this.newLevel;
    }

    public void setNewLevel(int level) {
    	this.newLevel = level;
    }

    public int getNewTotalExp() {
        return this.newTotalExp;
    }

    public void setNewTotalExp(int totalExp) {
    	this.newTotalExp = totalExp;
    }

    public boolean getKeepLevel() {
        return this.keepLevel;
    }

    public void setKeepLevel(boolean keepLevel) {
        this.keepLevel = keepLevel;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    public boolean getKeepInventory() {
        return keepInventory;
    }

}
