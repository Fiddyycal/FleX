package org.fukkit.fle.listeners;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.fukkit.event.FleXEventListener;

public class CriticalHitListeners extends FleXEventListener {

	@SuppressWarnings(value = { "unused", "deprecation" })
	public void event(EntityDamageByEntityEvent event) {
		
		if (event.isCancelled())
			return;
		
		if (event.getDamager() instanceof HumanEntity == false)
			return;
		
		HumanEntity damager = (HumanEntity) event.getDamager();
		LivingEntity damaged = (LivingEntity) event.getEntity();
		
		if (damager.isDead())
			return;
		
    	if (damaged.isDead())
    		return;
		
		ItemStack item = damager.getItemInHand();
		
    	boolean critical = damager.getFallDistance() > 0.0 && !damager.isOnGround() && !damager.isInsideVehicle() && !damager.hasPotionEffect(PotionEffectType.BLINDNESS);
		
		boolean arthropods = item.containsEnchantment(Enchantment.DAMAGE_ARTHROPODS) && (damaged instanceof Spider || damaged instanceof CaveSpider || damaged instanceof Silverfish);
		boolean smite = item.containsEnchantment(Enchantment.DAMAGE_UNDEAD) && (damaged instanceof Zombie || damaged instanceof Skeleton || damaged instanceof PigZombie);
		boolean painful = item.containsEnchantment(Enchantment.DAMAGE_ALL) || arthropods || smite;
		
		// TODO: Check if damage is normal, if it's not then damager is critical hitting, if the above check isn't true; the damager is possibly using a critical cheat.
		
	}

}
