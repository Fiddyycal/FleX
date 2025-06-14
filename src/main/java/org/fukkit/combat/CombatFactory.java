package org.fukkit.combat;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;
import org.fukkit.Fukkit;

@SuppressWarnings("deprecation")
public interface CombatFactory {

	public static final float KNOCKBACK_VELOCITY_RAW = (float) Fukkit.getInstance().getConfig().getDouble("Combat.Knockback-Modifier.Velocity", 1.02);
	public static final float KNOCKBACK_HEIGHT_RAW = (float) Fukkit.getInstance().getConfig().getDouble("Combat.Knockback-Modifier.Height", 0.73);
	
	public static final float VANILLA_KNOCKBACK_HEIGHT_RAW = (float) 1.36;
	public static final float VANILLA_KNOCKBACK_VELOCITY_RAW = (float) 1.22;
	
	public static final double DAMAGE_DELAY = Fukkit.getInstance().getConfig().getDouble("Combat.Registration-Delay", 0.90);
	public static final double VANILLA_DAMAGE_DELAY = 1.0;
	
	public float getKnockbackHeight();
	
	public float getKnockbackVelocity();
	
	public double getDamageDelay();
	
	public void setDamageDelay(double damageDelay);
	
	public void setKnockbackHeight(float height);
	
	public void setKnockbackVelocity(float velocity);
	
	public void setAntiLockupKnockback(boolean noLockup);
	
	public void setKnockbackRealistic(boolean realistic);
	
	public void setLegacy(boolean legacy);
	
	public void setEnabled(boolean enabled);
	
	public boolean isAntiLockupKnockbackEnabled();
	
	public boolean isKnockbackRealistic();
	
	public boolean isLegacy();
	
	public boolean isEnabled();

	public default void knockback(LivingEntity entity, Entity source, double modifier) {
		
		if (entity == null || source == null || entity.isDead())
			return;
		
		Vector velocity;
		
		if (Fukkit.getCombatFactory().isLegacy()) {
			
			entity.setVelocity(new Vector());
			
			boolean projectile = source instanceof Projectile && ((Projectile)source).getShooter() instanceof LivingEntity;
				
			velocity = source.getLocation().toVector();
				
			double multiple = (this.getKnockbackVelocity() / 2) + modifier * 0.05;
				
			if (!projectile && entity instanceof Player && ((Player)entity).isSprinting())
				multiple *= 2.0;
				
			if (projectile)
				velocity = ((LivingEntity)((Projectile)source).getShooter()).getLocation().toVector().multiply(1);
				
			velocity = entity.getLocation().toVector().subtract(velocity.multiply(1)).normalize();
			velocity = velocity.normalize().multiply(multiple).setY(this.getKnockbackHeight() / 2);
			
			entity.setVelocity(velocity);
			
		} else {
		    
		    velocity = entity.getVelocity();
			
			double x = source.getLocation().getX() - entity.getLocation().getX();
			double z = source.getLocation().getZ() - entity.getLocation().getZ();
			
		    float horizontal = (float) Math.sqrt(x * x + z * z);
		    float vertical = (float) 0.4;
		    
		    velocity.setX(velocity.getX() / 2.0);
		    velocity.setY(velocity.getY() / 2.0);
		    velocity.setZ(velocity.getZ() / 2.0);
		    
		    velocity.setX(velocity.getX() - x / horizontal * vertical);
		    velocity.setY(velocity.getY() + vertical);
		    velocity.setZ(velocity.getZ() - z / horizontal * vertical);
		    
		    // 0.4000000059604645 as per vanilla.
		    if (velocity.getY() > 0.4000000059604645)
		    	velocity.setY(0.4000000059604645);
		    
		    boolean sprinting = source instanceof Player ? ((Player) source).isSprinting() : false;
		    
		    HumanEntity living = source instanceof HumanEntity ? (HumanEntity) source : null;
		    
			int i = (sprinting ? 1 : 0) + (living != null ? living.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) : 0);
		    
		    if (i > 0) {
		    	
		    	velocity.setX(velocity.getX() + -Math.sin((source.getLocation().getYaw() * 3.1415927 / 180.0)) * i * 0.5);
		    	velocity.setY(velocity.getY() + 0.1);
		    	velocity.setZ(velocity.getZ() + Math.cos((source.getLocation().getYaw() * 3.1415927 / 180.0)) * i * 0.5);
		    	
		    }
		    
		}
		
        entity.setVelocity(velocity);
	    
	}
	
}
