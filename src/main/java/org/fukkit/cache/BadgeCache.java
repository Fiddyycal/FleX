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
		
		this.add(new Badge('➀', "ATTENDANCE_2024", "Wanderer", Tier.TIER_THREE, "Awarded for 1 year with the Reunion Server"));
		this.add(new Badge('➁', "ATTENDANCE_2023", "Wanderer", Tier.TIER_TWO, "Awarded for 2 consecutive years with the Reunion Server"));
		this.add(new Badge('➂', "ATTENDANCE_2022", "Regular", Tier.TIER_ONE, "Awarded for 3 consecutive years with the Reunion Server"));
		this.add(new Badge('➃', "ATTENDANCE_2021", "Regular", Tier.TIER_ONE, "Awarded for 4 consecutive years with the Reunion Server"));
		this.add(new Badge('➄', "ATTENDANCE_2020", "Pro", Tier.TIER_ONE, "Awarded for 5 consecutive years with the Reunion Server"));
		this.add(new Badge('➅', "ATTENDANCE_2019", "Pro", Tier.TIER_ONE, "Awarded for 6 consecutive years with the Reunion Server"));
		this.add(new Badge('➆', "ATTENDANCE_2018", "Legend", Tier.TIER_ONE, "Awarded for 7 consecutive years with the Reunion Server"));
		this.add(new Badge('➇', "ATTENDANCE_2017", "Legend", Tier.TIER_ONE, "Awarded for 8 consecutive years with the Reunion Server"));
		
		this.add(new Badge('➀', "PARTICIPATION_2024", "Wanderer", Tier.TIER_THREE, "Achieved playing the network for 1 year"));
		this.add(new Badge('➁', "PARTICIPATION_2023", "Wanderer", Tier.TIER_TWO, "Achieved playing the network for 2 consecutive years"));
		this.add(new Badge('➂', "PARTICIPATION_2022", "Regular", Tier.TIER_ONE, "Achieved playing the network for 3 consecutive years"));
		this.add(new Badge('➃', "PARTICIPATION_2021", "Regular", Tier.TIER_ONE, "Achieved playing the network for 4 consecutive years"));
		this.add(new Badge('➄', "PARTICIPATION_2020", "Pro", Tier.TIER_ONE, "Achieved playing the network for 5 consecutive years"));
		this.add(new Badge('➅', "PARTICIPATION_2019", "Pro", Tier.TIER_ONE, "Achieved playing the network for 6 consecutive years"));
		this.add(new Badge('➆', "PARTICIPATION_2018", "Legend", Tier.TIER_ONE, "Achieved playing the network for 7 consecutive years"));
		this.add(new Badge('➇', "PARTICIPATION_2017", "Legend", Tier.TIER_ONE, "Achieved playing the network for 8 consecutive years"));
		
		this.add(new Badge('➊', "ONE_YEAR_STAFF", "Novice", Tier.TIER_TWO, "Achieved by completing 1 year as a member of the staff team"));
		this.add(new Badge('➋', "TWO_YEAR_STAFF", "Intermediate", Tier.TIER_ONE, "Achieved by completing 2 consecutive years as a member of the staff team"));
		this.add(new Badge('➌', "THREE_YEAR_STAFF", "Adept", Tier.TIER_ONE, "Achieved by completing 3 consecutive years as a member of the staff team"));
		this.add(new Badge('➍', "FOUR_YEAR_STAFF", "Expert", Tier.TIER_ONE, "Achieved by completing 4 consecutive years as a member of the staff team"));
		this.add(new Badge('➎', "FOUR_YEAR_STAFF", "Expert", Tier.TIER_ONE, "Achieved by completing 5 consecutive years as a member of the staff team"));
		
		this.add(new Badge('☾', "LUNAR", "Lunar", Tier.TIER_THREE, "Awarded for using the Lunar Client"));
		this.add(new Badge('☯', "LION", "Lion", Tier.TIER_THREE, "Awarded for using the Badlion Client"));
		
		/**
		 * MCGamer Reunion
		 */
		this.add(new Badge('☂', "FAB", "Fab", Tier.TIER_ONE, "Awarded for being fabulous")); // Alyssa
		this.add(new Badge('✿', "DOLLY", "ImNotYourDolly", Tier.TIER_ONE, "Awarded for repeated attendance to the MCGamer reunion"));
		this.add(new Badge('★', "HELPER", "Helper", Tier.TIER_TWO, "Awarded for helping out with the MCGamer Reunion"));
		this.add(new Badge('➲', "IMPORTED", "Imported", Tier.TIER_TWO, "Achieved by having survival games statistics imported from the MCGamer databases"));
		this.add(new Badge('❂', "VICTOR", "Victor", Tier.TIER_TWO, "Awarded for being one of the top 50 players in the MCGamer databases, a true capital darling"));
		
		this.add(new Badge('㊌', "TRANSLATOR", "Translator", Tier.TIER_TWO, "Awarded for translating for the network"));
		this.add(new Badge('⚠', "GLITCHED", "Glitched", Tier.TIER_TWO, "Awarded for contributing to development by reporting a bug or glitch"));
		this.add(new Badge('ℼ', "TESTER", "Tester", Tier.TIER_THREE, "Awarded for joining the server during a pre-alpha testing session"));
		this.add(new Badge('ℹ', "GUIDE", "Guide", Tier.TIER_TWO, "Achieved by creating a guide on the network forums"));
		this.add(new Badge('✉', "POSTED", "Posted", Tier.TIER_ONE, "Awarded for promoting the network"));
		this.add(new Badge('☘', "LUCKY", "Lucky", Tier.TIER_ONE, "Awarded for winning in a prize-pool tournament"));
		this.add(new Badge('♖', "MAP_MAKER", "Map Maker", Tier.TIER_ONE, "Awarded for submitting a map to the network"));
		
		this.add(new Badge('♬', "DJ", "DJ", Tier.TIER_THREE, "Achieved by playing a song on the networks plug.dj"));
		this.add(new Badge('♕', "HOST", "Host", Tier.TIER_TWO, "Achieved by hosting an event for the network"));
		this.add(new Badge('⚔', "STAFF", "Staff", Tier.TIER_TWO, "Achieved by donating time and effort into the network"));
		this.add(new Badge('⚒', "DEVELOPMENT", "Development", Tier.TIER_ONE, "Awarded for developing the network"));
		this.add(new Badge('⚡', "ADMINISTRATION", "Administration", Tier.TIER_ONE, "Awarded for administering the network"));
		this.add(new Badge('⚜', "OWNERSHIP", "Owner", Tier.TIER_ONE, "Awarded for running the show"));
		
		this.add(new Badge('♿', "RETIRED", "Retired", Tier.TIER_TWO, "Awarded for resigning from a staff role on good terms"));
		this.add(new Badge('✔', "LINKED", "Linked", Tier.TIER_THREE, "Achieved by linking your discord account to your Minecraft account"));
		this.add(new Badge('✪', "VERIFIED", "Verified", Tier.TIER_ONE, "Achieved by being accepted when applying for account verification"));
		
		this.add(new Badge('☣', "TOXIC", "Toxic", Tier.TIER_TWO, "Achieved by being reported by 10 different people"));
		this.add(new Badge('☮', "PEACEFUL", "Peaceful", Tier.TIER_THREE, "Achieved by winning a game without killing anyone"));
		
		//this.add(new Badge('✿', "GIRLFRIEND", "Missus", Tier.TIER_ONE, "<sc>5Ocal<lore>'s girlfriend<pp>."));
		return true;
		
	}

}
