package org.fukkit.cache;

import org.fukkit.reward.Badge;

import io.flex.commons.Tier;
import io.flex.commons.cache.LinkedCache;

public class BadgeCache extends LinkedCache<Badge, String> {
	
	private static final long serialVersionUID = 1896219879102160101L;
	
	public BadgeCache() {
		super((badge, key) -> badge.toString().equalsIgnoreCase(key));
	}
	
	public Badge getByName(String name) {
		return this.stream().filter(b -> b.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	@Override
	public boolean load() {
		
		if (!this.isEmpty())
			this.clear();
		
		/**
		 * 
		 * ➀➁➂➃
		 * ➊➋➌➍
		 * ☾☯
		 * ㊌⚠ℼℹ✉☘
		 * ♖⚔⚒⚡⚜♿
		 * 
		 * ✔✪
		 * 
		 * ☂
		 * ✿
		 * 
		 * ☣☮
		 * 
		 */
		
		this.add(new Badge('➀', "ONE_YEAR_VETARAN", "Expert", Tier.TIER_TWO, "<lore>Achieved playing the network for <sv>1<reset> <lore>year<pp>."));
		this.add(new Badge('➀', "TWO_YEAR_VETARAN", "Expert", Tier.TIER_TWO, "<lore>Achieved playing the network for <sv>2<reset> <lore>consecutive years<pp>."));
		this.add(new Badge('➀', "THREE_YEAR_VETARAN", "Expert", Tier.TIER_TWO, "<lore>Achieved playing the network for <sv>3<reset> <lore>consecutive years<pp>."));
		this.add(new Badge('➀', "FOUR_YEAR_VETARAN", "Expert", Tier.TIER_TWO, "<lore>Achieved playing the network for <sv>4<reset> <lore>consecutive years<pp>."));
		
		this.add(new Badge('➊', "ONE_YEAR_STAFF", "Veteran", Tier.TIER_ONE, "<lore>Achieved by completing <sv>1<reset> <lore>year as a member of the staff team<pp>."));
		this.add(new Badge('➋', "TWO_YEAR_STAFF", "Veteran", Tier.TIER_ONE, "<lore>Achieved by completing <sv>2<reset> <lore>consecutive years as a member of the staff team<pp>."));
		this.add(new Badge('➌', "THREE_YEAR_STAFF", "Veteran", Tier.TIER_ONE, "<lore>Achieved by completing <sv>3<reset> <lore>consecutive years as a member of the staff team<pp>."));
		this.add(new Badge('➍', "FOUR_YEAR_STAFF", "Veteran", Tier.TIER_ONE, "<lore>Achieved by completing <sv>4<reset> <lore>consecutive years as a member of the staff team<pp>."));
		
		this.add(new Badge('☾', "LUNAR", "Lunar", Tier.TIER_THREE, "<lore>Awarded for using the <sc>Lunar Client<pp>."));
		this.add(new Badge('☯', "LION", "Lion", Tier.TIER_THREE, "<lore>Awarded for using the <sc>Badlion Client<pp>."));
		
		this.add(new Badge('㊌', "TRANSLATOR", "Translator", Tier.TIER_THREE, "<lore>Awarded for translating for the network."));
		this.add(new Badge('⚠', "GLITCHED", "Glitched", Tier.TIER_THREE, "<lore>Awarded for contributing to development by reporting a bug or glitch<pp>."));
		this.add(new Badge('ℼ', "TESTER", "Tester", Tier.TIER_THREE, "<lore>Awarded for joining the server during a pre-alpha testing session<pp>."));
		this.add(new Badge('ℹ', "GUIDE", "Guide", Tier.TIER_THREE, "<lore>Achieved by creating a guide on the network forums<pp>."));
		this.add(new Badge('✉', "POSTED", "Posted", Tier.TIER_TWO, "<lore>Awarded for promoting the network<pp>."));
		this.add(new Badge('☘', "LUCKY", "Lucky", Tier.TIER_ONE, "<lore>Awarded for winning in a prize-pool tournament<pp>."));
		this.add(new Badge('♖', "MAP_MAKER", "Map Maker", Tier.TIER_TWO, "<lore>Awarded for submitting a map to the network<pp>."));
		
		this.add(new Badge('♬', "DJ", "DJ", Tier.TIER_THREE, "<lore>Achieved by playing a song on the networks plug.dj<pp>."));
		this.add(new Badge('♕', "HOST", "Host", Tier.TIER_TWO, "<lore>Achieved by hosting an event for the network<pp>."));
		this.add(new Badge('⚔', "STAFF", "Staff", Tier.TIER_TWO, "<lore>Achieved by donating time and effort into the network<pp>."));
		this.add(new Badge('⚒', "DEVELOPMENT", "Development", Tier.TIER_ONE, "<lore>Awarded for developing the network<pp>."));
		this.add(new Badge('⚡', "ADMINISTRATION", "Administration", Tier.TIER_ONE, "<lore>Awarded for administering the network<pp>."));
		this.add(new Badge('⚜', "OWNERSHIP", "Owner", Tier.TIER_ONE, "<lore>Awarded for running the show<pp>."));
		
		this.add(new Badge('♿', "RETIRED", "Retired", Tier.TIER_TWO, "<lore>Awarded for completing a staff role on good terms<pp>."));
		this.add(new Badge('✔', "LINKED", "Linked", Tier.TIER_THREE, "<lore>Achieved by linking your forum account and discord to your Minecraft account<pp>."));
		this.add(new Badge('✪', "VERIFIED", "Verified", Tier.TIER_THREE, "<lore>Achieved by being accepted when applying for account verification<pp>."));
		
		this.add(new Badge('☣', "TOXIC", "Toxic", Tier.TIER_ONE, "<lore>Achieved by being reported by 10 different people<pp>."));
		this.add(new Badge('☮', "PEACEFUL", "Peaceful", Tier.TIER_THREE, "<lore>Achieved by winning a game without killing anyone<pp>."));
		
		this.add(new Badge('☂', "FAB", "Fab", Tier.TIER_ONE, "<lore>Awarded for being fabulous<pp>."));
		
		this.add(new Badge('✿', "GIRLFRIEND", "Missus", Tier.TIER_ONE, "<sc>5Ocal<lore>'s girlfriend<pp>."));
		return true;
		
	}

}
