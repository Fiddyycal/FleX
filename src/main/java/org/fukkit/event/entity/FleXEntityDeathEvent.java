package org.fukkit.event.entity;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.fukkit.entity.FleXLivingEntity;

public class FleXEntityDeathEvent extends FleXEntityEvent {
	
    private final List<ItemStack> drops;
    
    private int dropExp = 0;

    public FleXEntityDeathEvent(final FleXLivingEntity entity, final List<ItemStack> drops) {
        this(entity, drops, 0);
    }

    public FleXEntityDeathEvent(final FleXLivingEntity entity, final List<ItemStack> drops, final int droppedExp) {
    	
        super(entity, false);
        
        this.drops = drops;
        this.dropExp = droppedExp;
        
    }

    @Override
    public FleXLivingEntity getEntity() {
        return (FleXLivingEntity) this.entity;
    }
    
    public int getDroppedExp() {
        return this.dropExp;
    }

    public void setDroppedExp(int exp) {
        this.dropExp = exp;
    }

    public List<ItemStack> getDrops() {
        return this.drops;
    }


}
