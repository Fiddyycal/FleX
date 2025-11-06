package org.fukkit.item;

import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;

import io.flex.commons.utils.ArrayUtils;

public enum ChartedItem {
	
	AIR(1.0, "LEGACY_AIR"),
	
	NETHERITE_HELMET(5.0), // TODO Test if this is correct
	NETHERITE_CHESTPLATE(10.0), // TODO Test if this is correct
	NETHERITE_LEGGINGS(8.0), // TODO Test if this is correct
	NETHERITE_BOOTS(5.0), // TODO Test if this is correct
	
	NETHERITE_SWORD(13.0), // TODO Test if this is correct
	NETHERITE_AXE(12.0), // TODO Test if this is correct
	NETHERITE_PICKAXE(11.0), // TODO Test if this is correct
	NETHERITE_SHOVEL(10.0), // TODO Test if this is correct
	NETHERITE_HOE(1.005),
	
	DIAMOND_HELMET(3.0, "LEGACY_DIAMOND_HELMET"),
	DIAMOND_CHESTPLATE(8.0, "LEGACY_DIAMOND_CHESTPLATE"),
	DIAMOND_LEGGINGS(6.0, "LEGACY_DIAMOND_LEGGINGS"),
	DIAMOND_BOOTS(3.0, "LEGACY_DIAMOND_BOOTS"),
	
	DIAMOND_SWORD(8.0, "LEGACY_DIAMOND_SWORD"),
	DIAMOND_AXE(7.0, "LEGACY_DIAMOND_AXE"),
	DIAMOND_PICKAXE(6.0, "LEGACY_DIAMOND_PICKAXE"),
	DIAMOND_SHOVEL(5.0, "LEGACY_DIAMOND_SHOVEL", "LEGACY_DIAMOND_SPADE", "DIAMOND_SPADE"),
	DIAMOND_HOE(1.004, "LEGACY_DIAMOND_HOE", "DIAMOND_HOE"), // TODO Test if this is correct
	
	IRON_HELMET(2.2, "LEGACY_IRON_HELMET"),
	IRON_CHESTPLATE(6.0, "LEGACY_IRON_CHESTPLATE"),
	IRON_LEGGINGS(5.0, "LEGACY_IRON_LEGGINGS"),
	IRON_BOOTS(2.0, "LEGACY_IRON_BOOTS"),
	
	IRON_SWORD(7.0, "LEGACY_IRON_SWORD"),
	IRON_AXE(6.0, "LEGACY_IRON_AXE"),
	IRON_PICKAXE(5.0, "LEGACY_IRON_PICKAXE"),
	IRON_SHOVEL(4.0, "LEGACY_IRON_SHOVEL", "LEGACY_IRON_SPADE", "IRON_SPADE"),
	IRON_HOE(1.003, "LEGACY_IRON_HOE", "IRON_HOE"), // TODO Test if this is correct
	
	CHAINMAIL_HELMET(2.001, "LEGACY_CHAINMAIL_HELMET"),
	CHAINMAIL_CHESTPLATE(5.001, "LEGACY_CHAINMAIL_CHESTPLATE"),
	CHAINMAIL_LEGGINGS(4.0, "LEGACY_CHAINMAIL_LEGGINGS"),
	CHAINMAIL_BOOTS(1.002, "LEGACY_CHAINMAIL_BOOTS"),
	
	STONE_SWORD(6.0, "LEGACY_STONE_SWORD"),
	STONE_AXE(5.0, "LEGACY_STONE_AXE"),
	STONE_PICKAXE(4.0, "LEGACY_STONE_PICKAXE"),
	STONE_SHOVEL(3.0, "LEGACY_STONE_SHOVEL", "LEGACY_STONE_SPADE", "STONE_SPADE"),
	STONE_HOE(1.002, "LEGACY_STONE_HOE", "STONE_HOE"), // TODO Test if this is correct
	
	GOLDEN_HELMET(2.0, "LEGACY_GOLDEN_HELMET", "LEGACY_GOLD_HELMET", "LEGACY_GOLD_HELMET"),
	GOLDEN_CHESTPLATE(5.0, "LEGACY_GOLDEN_CHESTPLATE", "LEGACY_GOLD_CHESTPLATE", "GOLD_CHESTPLATE"),
	GOLDEN_LEGGINGS(3.0, "LEGACY_GOLDEN_LEGGINGS", "LEGACY_GOLD_LEGGINGS", "GOLD_LEGGINGS"),
	GOLDEN_BOOTS(1.001, "LEGACY_GOLDEN_BOOTS", "LEGACY_GOLD_BOOTS", "GOLD_BOOTS"),
	
	GOLDEN_SWORD(5.001, "LEGACY_GOLDEN_SWORD", "LEGACY_GOLD_SWORD", "GOLD_SWORD"),
	GOLDEN_AXE(4.001, "LEGACY_GOLDEN_AXE", "LEGACY_GOLD_AXE", "GOLD_AXE"),
	GOLDEN_PICKAXE(3.001, "LEGACY_GOLDEN_PICKAXE", "LEGACY_GOLD_PICKAXE", "GOLD_PICKAXE"),
	GOLDEN_SHOVEL(2.001, "LEGACY_GOLDEN_SHOVEL", "LEGACY_GOLD_SPADE", "GOLD_SPADE"),
	GOLDEN_HOE(1.001, "LEGACY_GOLDEN_HOE", "LEGACY_GOLD_HOE", "GOLD_HOE"), // TODO Test if this is correct
	
	LEATHER_HELMET(1.0, "LEGACY_LEATHER_HELMET"),
	LEATHER_CHESTPLATE(3.0, "LEGACY_LEATHER_CHESTPLATE"),
	LEATHER_LEGGINGS(2.0, "LEGACY_LEATHER_LEGGINGS"),
	LEATHER_BOOTS(1.0, "LEGACY_LEATHER_BOOTS"),
	
	WOODEN_SWORD(5.0, "LEGACY_WOODEN_SWORD", "LEGACY_WOOD_SWORD", "WOOD_SWORD"),
	WOODEN_AXE(4.0, "LEGACY_WOODEN_AXE", "LEGACY_WOOD_AXE", "WOOD_AXE"),
	WOODEN_PICKAXE(3.0, "LEGACY_WOODEN_PICKAXE", "LEGACY_WOOD_PICKAXE", "WOOD_PICKAXE"),
	WOODEN_SHOVEL(2.0, "LEGACY_WOODEN_SHOVEL", "LEGACY_WOOD_SPADE", "WOOD_SPADE"),
	WOODEN_HOE(1.0, "LEGACY_WOODEN_HOE", "LEGACY_WOOD_HOE", "WOOD_HOE"),
	
	BOW(1.0, "LEGACY_BOW", "BOW"),
	EXP_BOTTLE(1.0, "LEGACY_EXP_BOTTLE", "EXP_BOTTLE", "EXPERIENCE_BOTTLE", "LEGACY_EXPERIENCE_BOTTLE"),
	SPLASH_POTION(1.0, "LEGACY_SPLASH_POTION", "SPLASH_POTION"),
	ENDER_PEARL(1.0, "LEGACY_ENDER_PEARL", "ENDER_PEARL"),
	EGG(1.0, "LEGACY_EGG", "EGG"),
	SNOW_BALL(1.0, "LEGACY_SNOW_BALL", "SNOW_BALL");
	
	private double amplifier;
	
	private String[] names;
	
	private ChartedItem(double amplifier, String... legacy) {
		this.amplifier = amplifier;
		this.names = ArrayUtils.add(legacy != null && legacy.length > 0 ? legacy : new String[0], this.name());
	}
	
	public double getDamage(int level) {
		return this.isWeapon() ? this.amplifier + (level > 0 ? 1.25 * level : 0) : -1;
	}
	
	public double getProtection(int level) {
		return this.isArmor() ? this.amplifier + (level > 0 ? 0.027200043201446533 * level : 0) : -1;
	}
	
	public double getAmplifier() {
		return this.amplifier;
	}
	
	public boolean isTool() {
		return this.isAxe() || this.isPickaxe() || this.isShovel() || this.isHoe();
	}
	
	public boolean isWeapon() {
		return this.isSword() || this.isAxe() || this.isPickaxe() || this.isShovel();
	}
	
	public boolean isSword() {
		return this.name().endsWith("_SWORD");
	}
	
	public boolean isAxe() {
		return this.name().endsWith("_AXE");
	}
	
	public boolean isPickaxe() {
		return this.name().endsWith("_PICKAXE");
	}
	
	public boolean isShovel() {
		return this.name().endsWith("_SHOVEL");
	}
	
	public boolean isHoe() {
		return this.name().endsWith("_HOE");
	}
	
	public boolean isArmor() {
		return (this.isHelmet()) || (this.isChestplate()) || (this.isLeggings()) || (this.isBoots());
	}
	
	public boolean isHelmet() {
		return this.name().endsWith("_HELMET");
	}
	
	public boolean isChestplate() {
		return this.name().endsWith("_CHESTPLATE");
	}
	
	public boolean isLeggings() {
		return this.name().endsWith("_LEGGINGS");
	}
	
	public boolean isBoots() {
		return this.name().endsWith("_BOOTS");
	}
	
	public boolean isProjectile() {
		return this == EXP_BOTTLE || this == SPLASH_POTION || this == ENDER_PEARL || this == EGG || this == SNOW_BALL;
	}
	
	public boolean isProjectileSource() {
		return this == BOW;
	}
	
	public static boolean isCharted(Material material) {
		return valueOf(material) != null;
	}
	
	public static ChartedItem valueOf(Material material) {
		
		for (ChartedItem charted : ChartedItem.values())
			if (charted.name().equals(material.name()) || ArrayUtils.contains(charted.names, material.name()))
				return charted;
		
		return null;
		
	}
	
	public Class<? extends Projectile> projectile() {
		
		switch (this) {
		case EXP_BOTTLE:
			return ThrownExpBottle.class;
		case SPLASH_POTION:
			return ThrownPotion.class;
		case ENDER_PEARL:
			return EnderPearl.class;
		case EGG:
			return Egg.class;
		case SNOW_BALL:
			return Snowball.class;
		default:
			throw new UnsupportedOperationException(this.name() + " is not a thrown projectile.");
		}
		
	}
	
}
