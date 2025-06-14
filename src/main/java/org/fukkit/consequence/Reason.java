package org.fukkit.consequence;

import io.flex.commons.Nullable;
import io.flex.commons.utils.ArrayUtils;
import io.flex.commons.utils.NumUtils;

import org.fukkit.utils.VersionUtils;

import java.util.stream.IntStream;

import org.bukkit.Material;

public enum Reason {
	
	/**
	 * Flow Universal
	 */
	
	UNFAIR_ADVANTAGE_UNIVERSAL(
			
			"Unfair Advantage: Flow Rejected Modifications",
			"Cheating",
			null,
			Material.BARRIER,
			ConvictionType.BAN,
			new EvidenceType[] { EvidenceType.RECORDING_REFERENCE },
			NumUtils.MONTH_TO_MILLIS,
			"This player has an unfair advantage"),
	
	CHAT_ABUSE_UNIVERSAL(
			
			"Chat Abuse: Flow Rejected Chat",
			"Disrespect/Abuse",
			null,
			Material.BARRIER,
			ConvictionType.MUTE,
			new EvidenceType[] { EvidenceType.RECORDING_REFERENCE },
			NumUtils.MONTH_TO_MILLIS * 3,
			"This player is using chat inappropriately"),
	
	OTHER_UNIVERSAL(
			
			"Other: Flow Rejected Activity",
			"Teaming/Targeting/Glitching",
			null,
			Material.BARRIER,
			ConvictionType.MUTE,
			new EvidenceType[] { EvidenceType.RECORDING_REFERENCE },
			NumUtils.MONTH_TO_MILLIS * 3,
			"This player is breaking another flow rule"),
	
	/**
	 *  Unfair Advantage
	 */
	
	HACKING(
			
			"Unfair Advantage: Cheating",
			"Hacking",
			null,
			Material.ANVIL,
			ConvictionType.BAN,
			new EvidenceType[] { EvidenceType.YOUTUBE_LINK, EvidenceType.RECORDING_REFERENCE },
			NumUtils.MONTH_TO_MILLIS,
			"This player is using a hacked client"),
	
	MODIFICATIONS(
			
			"Unfair Advantage: Cheating",
			"Illegal Modifications",
			null,
			Material.REDSTONE,
			ConvictionType.BAN,
			new EvidenceType[] { EvidenceType.YOUTUBE_LINK, EvidenceType.RECORDING_REFERENCE },
			NumUtils.MONTH_TO_MILLIS,
			"This player is using prohibited",
			"client modifications"),
	
	GLITCHING(
			
			"Unfair Advantage: Cheating",
			"Glitching",
			null,
			Material.STONE_PICKAXE,
			ConvictionType.KICK,
			new EvidenceType[] { EvidenceType.YOUTUBE_LINK, EvidenceType.RECORDING_REFERENCE },
			-1L,
			"This player is glitching or intentionally",
			"abusing in-game mechanics"),
	
	TEAMING(
			
			"Unfair Advantage: Abusive Behaviour",
			"Teaming in FFA",
			null,
			Material.TRIPWIRE_HOOK,
			ConvictionType.KICK,
			new EvidenceType[] { EvidenceType.YOUTUBE_LINK, EvidenceType.RECORDING_REFERENCE },
			-1L,
			"This player is teaming with",
			"others/another player"),
	
	/*
	 *  Ingame Abuse
	 */
	
	TARGETING(
			
			"Physical Abuse: Abusive Behaviour",
			"Excessive Targeting",
			null,
			VersionUtils.material("YELLOW_FLOWER", "DANDELION"),
			ConvictionType.KICK,
			new EvidenceType[] { EvidenceType.YOUTUBE_LINK, EvidenceType.RECORDING_REFERENCE },
			-1L,
			"This player is excessively targeting/stalking/stream",
			"sniping me and/or other players"),
	
	/**
	 *  Chat Abuse
	 */
	
	NETWORK_THREATS(
			
			"Chat Abuse: Malicious Activity",
			"Malicious Threats",
			"\"%name%: im abouta fry this proxy\"",
			Material.FLINT_AND_STEEL,
			ConvictionType.BAN,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.CHAT_LOG },
			NumUtils.MONTH_TO_MILLIS,
			"This player is sending malicious threats targeted",
			"towards the network or an individual server, such as",
			"Network/Proxy denial of service attacks (DoS), etc."),
	
	PLAYER_THREATS(
			
			"Chat Abuse: Malicious Activity",
			"Malicious Threats",
			"\"%name%: FUCK OFF ILL DOX YA CUNT\"",
			Material.POISONOUS_POTATO,
			ConvictionType.MUTE,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.CHAT_LOG },
			NumUtils.MONTH_TO_MILLIS,
			"This player is sending malicious threats targeted",
			"towards me and/or other players, such as but not limited",
			"to player distributed denial of service attacks (DDoS), leaks",
			"of private information and/or personal documents, etc."),
	
	MALICIOUS_LINKS(
			
			"Chat Abuse: Malicious Activity",
			"Malicious Links",
			"\"%name%: I got a free minecon cape from this link! http://freeCape.scam\"",
			VersionUtils.material("BOOK_AND_QUILL", "WRITABLE_BOOK"),
			ConvictionType.MUTE,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.CHAT_LOG },
			NumUtils.YEAR_TO_MILLIS,
			"This player is sending malicious links",
			"to me and/or other players"),
    
	FILTER_BYPASS(
			
			"Chat Abuse: Malicious Activity",
			"Filter Bypass",
			"\"%name%: wot r u a f@ggot? ky5 kid\"",
			Material.PAPER,
			ConvictionType.MUTE,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.CHAT_LOG },
			NumUtils.MONTH_TO_MILLIS,
			"This player is bypassing the on",
			"board chat filter system"),
	
	NETWORK_DISRESPECT(
			
			"Chat Abuse: Disrepsect",
			"Network Disrespect",
			"\"%name%: the developers hard work aint shit lmao\"",
			Material.TNT,
			ConvictionType.MUTE,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.CHAT_LOG },
			NumUtils.DAY_TO_MILLIS * 16,
			"This player is being disrespectful towards overall",
			"network development and/or administration"),
	
	PLAYER_DISRESPECT(
			
			"Chat Abuse: Disrepsect",
			"Player Disrespect",
			"\"%name%: LOL I SAW A PICTURE OF YO UGLY ASS FACE\"",
			VersionUtils.material("WEB", "COBWEB"),
			ConvictionType.MUTE,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.CHAT_LOG },
			NumUtils.DAY_TO_MILLIS * 16,
			"This player is being disrespectful",
			"towards me and/or other players"),
	INTENT_TO_DEGRADE(
			
			"Chat Abuse: Disrepsect",
			"Degrading Others",
			"\"%name%: stfu cunt you're asian\"",
			Material.DRAGON_EGG,
			ConvictionType.MUTE,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.CHAT_LOG },
			NumUtils.MONTH_TO_MILLIS,
			"This player is being abusive with",
			"intent to degrade race or religion"), // Nice spelling Caleb
	
	INTENT_TO_DISCRIMINATE(
			
			"Chat Abuse: Disrepsect",
			"Discrimination",
			"\"%name%: leave the lobby no gays allowed\"",
			VersionUtils.material("IRON_FENCE", "IRON_BARS"),
			ConvictionType.MUTE,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.CHAT_LOG },
			NumUtils.MONTH_TO_MILLIS,
			"This player is discriminating against race,",
			"gender, religion or sexual preference"),
	
    TRASH_TALK(
    		
    		"Chat Abuse: Disrepsect",
    		"Trash Talk",
    		"\"%name%: ur so shit at mineman lol\"",
    		Material.NAME_TAG,
			ConvictionType.MUTE,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.CHAT_LOG },
			NumUtils.DAY_TO_MILLIS * 16,
    		"This player is trash talking and/or being a bad",
    		"sport towards me and/or other players"),
	
	INAPPROPRIATE_LINKS(
			
			"Chat Abuse: Inappropriate",
			"Inappropriate Links",
			null,
			Material.BOOK,
			ConvictionType.MUTE,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.CHAT_LOG },
			NumUtils.DAY_TO_MILLIS * 16,
			"This player is sending inappropriate",
			"links to me and/or other players"),
	
	ADVERTISEMENT_OTHER(
			
			"Chat Abuse: Advertisement",
			"Service/Product Advertisement",
			"\"%name%: Selling my account. Send me offers!\"",
			VersionUtils.material("SIGN", "OAK_SIGN"),
			ConvictionType.MUTE,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.CHAT_LOG },
			NumUtils.DAY_TO_MILLIS * 16,
			"This player is using the network as a platform",
			"to advertise their products/services"),
	
	ADVERTISEMENT_SERVER(
			
			"Chat Abuse: Advertisement",
			"Server Advertisement",
			"\"%name%: Come join my server! ad.notAscam.com\"",
			VersionUtils.material("SKULL_ITEM", "PLAYER_HEAD"),
			ConvictionType.MUTE,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.CHAT_LOG },
			NumUtils.YEAR_TO_MILLIS,
			"This player is advertising server",
			"domains and/or ip addresses"),
    
	/**
	 *  Third Party Abuse
	 */
	
	INAPPROPRIATE_NAME(
			
			"Other: Inappropriate",
			"Inappropriate Username",
			null,
			Material.NAME_TAG,
			ConvictionType.BAN,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK },
			NumUtils.YEAR_TO_MILLIS,
			"This player has a username which is designed",
			"to discriminate, or shows disrespect"),
	
	INAPPROPRIATE_SKIN(
			
			"Other: Inappropriate",
			"Inappropriate Skin",
			null,
			Material.COOKIE,
			ConvictionType.BAN,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK },
			NumUtils.YEAR_TO_MILLIS,
			"This player has a skin that contains inappropriate",
			"content, or depicts inappropriate characters"),
	
	BOTTING(
			
			"Other: Spoof/Bootleg",
			"Bot/VPN Use",
			"Using a VPN to bypass a previous ban or mute",
			VersionUtils.material("FIREWORK_CHARGE", "FIRE_CHARGE"),
			ConvictionType.BAN,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.YOUTUBE_LINK, EvidenceType.RECORDING_REFERENCE },
			NumUtils.YEAR_TO_MILLIS,
			"This player is botting and/or using a",
			"VPN to bypass the networks security"),
	
	BOOT_LEG(
			
			"Other: Spoof/Bootleg",
			"Network Bootleg Creation",
			"Recreation of a copyrighted concept",
			Material.COAL,
			ConvictionType.BAN,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.YOUTUBE_LINK, EvidenceType.RECORDING_REFERENCE },
			NumUtils.YEAR_TO_MILLIS,
			"This player is involved in the development of",
			"a bootlegged/copyrighted product and/or service"),
    
	/**
	 *  Other?
	 */
	
	OTHER(
			
			"Other",
			"Something Else",
			null,
			Material.FEATHER,
			null,
			new EvidenceType[] { EvidenceType.IMGUR_LINK, EvidenceType.GYAZO_LINK, EvidenceType.LIGHTSHOT_LINK, EvidenceType.YOUTUBE_LINK, EvidenceType.CHAT_LOG, EvidenceType.RECORDING_REFERENCE },
			NumUtils.DAY_TO_MILLIS * 16L,
			"This player is breaking",
			"another network rule");
	
	private String category, display, example;
	
	private Material material;
	
	private ConvictionType conviction;
	
	private EvidenceType[] evidence;
	
	private long duration;
	
	private String[] description;
	
	private Reason(String category, String display, @Nullable String example, Material material, @Nullable ConvictionType conviction, EvidenceType[] evidence, long duration, String... description) {
		
		this.category = category;
		this.display = display;
		this.example = example;
		
		this.conviction = conviction;
		
		this.material = material;
		
		this.evidence = evidence;
		
		this.duration = duration;
		
		this.description = description;
		
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public String getExample() {
		return this.example;
	}
	
	public String getDescription() {
		StringBuilder builder = new StringBuilder();
		IntStream.range(0, this.description.length).forEach(i -> builder.append((builder.length() != 0 ? " " : "") + this.description[i]));
		return builder.toString();
	}
	
	public String[] getDescriptionAsLore() {
		return this.description;
	}
	
	public Material getMaterial() {
		return this.material;
	}
	
	public ConvictionType getConvictionType() {
		return this.conviction;
	}
	
	public EvidenceType[] getRequiredEvidence() {
		return ArrayUtils.contains(this.evidence, EvidenceType.NONE) ? new EvidenceType[0] : this.evidence;
	}
	
	public long getDuration() {
		return this.duration;
	}
	
	public boolean isEvidenceRequired() {
		return this.getRequiredEvidence().length > 0;
	}
	
	public boolean isFlowVerdict() {
		return this == Reason.UNFAIR_ADVANTAGE_UNIVERSAL || this == Reason.CHAT_ABUSE_UNIVERSAL || this == Reason.OTHER_UNIVERSAL;
	}
	
	@Override
	public String toString() {
		return this.display;
	}
	
}
