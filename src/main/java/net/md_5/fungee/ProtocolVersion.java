package net.md_5.fungee;

import java.util.Arrays;

import io.flex.commons.utils.ArrayUtils;

public enum ProtocolVersion {
	
	/**
	 * THE ORDER OF THESE MATTER!!!
	 */
	UNSPECIFIED("Unknown", -1),
	
	//TODO Could make these more accurate (i.e like 1.17-1.19 (1.8 & 1.7 also done)).
	
	v1_7("1.7", 3/*1.7 & 1.7.1*/, 4/*1.7.2-1.7.4*/, 5/*1.7.6-pre1-1.7.10*/),
	v1_8("1.8", 47/*1.8-1.8.9*/, 44/*1.8-pre1*/, 45/*1.8-pre2*/, 46/*1.8-pre3*/),
	v1_9("1.9", 110, 103, 104, 105, 106, 107, 108, 109, 110),
	v1_10("1.10", 210, 204, 205),
	v1_11("1.11", 316, 314, 315),
	v1_12("1.12", 340, 328, 3299, 330, 331, 332, 333, 334, 335, 337, 338, 339),
	v1_13("1.13", 404, 383, 384, 385, 386, 387, 388, 389, 390, 391, 392, 393, 399, 400, 401, 402, 403),
	v1_14("1.14", 498, 472, 473, 474, 475, 476, 477, 478, 479, 480, 481, 482, 483, 484, 485, 486, 487, 488, 489, 490, 491, 492, 493, 494, 495, 496, 497),
	v1_15("1.15", 578, 565, 566, 567, 568, 569, 570, 571, 572, 573, 574, 575, 576, 577),
	v1_16("1.16", 754, 721, 722, 725, 726, 727, 729, 730, 732, 733, 734, 735, 736, 744, 746, 748, 749, 750, 751, 752, 753),
	v1_17("1.17", 755),
	v1_17_1("1.17.1", 756),
	v1_18("1.18", 757/*/*1.18 & 1.18.1*/),
	v1_18_2("1.18.2", 758),
	v1_19("1.19", 759, 760/*1.19.1 & 1.19.2*/),
	v1_19_3("1.19.3", 761),
	v1_19_4("1.19.4", 762),
	v1_20("1.20/.1", 763/*1.20 & 1.20.1*/),
	v1_20_2("1.20.2", 764),
	v1_20_3("1.20.3/4", 765/*1.20.3 & 1.20.4*/),
	v1_20_6("1.20.5/6", 766/*1.20.5 & 1.20.6*/),
	v1_21("1.21", 767),
	v1_21_1("1.21.1", 768),
	
	LATEST(v1_20_6.toString(), v1_20_6.toRecommendedProtocol());
	
	private String display;
	
	private Integer[] protocols;
	
	private ProtocolVersion(String display, Integer protocol, Integer... revisions) {
		
		this.display = display;
		
		Integer[] protocols = new Integer[] { protocol };
		
		if (revisions != null && revisions.length > 0)
			protocols = ArrayUtils.add(protocols, revisions);
		
		this.protocols = protocols;
		
	}
	
	public boolean isAtLeast(ProtocolVersion version) {
		
		int prot = this.protocols[0];
		int check = version.protocols[0];
		
		return version != UNSPECIFIED && (prot >= check || (version.protocols.length > 1 && prot >= version.toLowestProtocol()));
		
	}
	
	public boolean isSupported() {
		return (this != UNSPECIFIED);
	}
	
	public Integer[] asSnapshots() {
		return this.protocols;
	}
	
	public int toRecommendedProtocol() {
		return this.protocols[0];
	}
	
	public Integer toLowestProtocol() {
		
		Integer[] protocols = this.protocols.clone();
		
		Arrays.sort(protocols);
		
		return protocols[0];
		
	}
	
	public String toString() {
		return this.display;
	}
	
	public static ProtocolVersion fromProtocol(int protocol) {
		return Arrays.stream(values()).filter(v -> ArrayUtils.contains(v.protocols, protocol)).findFirst().orElse(null);
	}
	
	public static ProtocolVersion fromString(String display) {
		
		ProtocolVersion version = Arrays.stream(values()).filter(v -> v.display.equals(display)).findFirst().orElse(null);
		
		return version != null ? version : valueOf(display);
		
	}
	
}
