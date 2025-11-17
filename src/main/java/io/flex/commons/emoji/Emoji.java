package io.flex.commons.emoji;

public enum Emoji {
	
	// NOT MIENCRAFT COMPATIBLE
	BOW("🏹"),
	
	// COMPATIBLE EVERYWHERE
	HEART("❤"),
	BULLET("•"),
	MARK_REGISTERED_TRADE("©"),
	MARK_TRADE("®"),
	MARK_SERVICE("™"),
	BLACK_CHESS_QUEEN("♛"),
	BOX_DRAWINGS_LIGHT_UP_AND_RIGHT("└"),
	BOX_DRAWINGS_LIGHT_VERTICAL_AND_RIGHT("├"),
	BOX_DRAWINGS_LIGHT_HORIZONTAL("─"),
	CROSSED_SWORDS("⚔️"),
	BLACK_SUN_WITH_RAYS("☀"),
	FOUR_POINTED_WHITE_STAR("✧"),
	SIX_POINTED_BLACK_STAR("✶"),
	HEAVY_CHECK_MARK("✔"),
	HEAVY_CROSS_MARK("✖"),
	DOUBLE_LEFT_POINTING_ARROW("«"),
	DOUBLE_RIGHT_POINTING_ARROW("»"),
	CIRCLED_HEAVY_WHITE_RIGHTWARDS_ARROW("➲"),
	LEFT_HALF_BLOCK("▌"),
	LEFT_ONE_QUATER_BLOCK("▎"),
	LEFT_THREE_EIGHTHS_BLOCK("▍"),
	LEFT_FACING_CRESENT("☽"),
	RIGHT_FACING_CRESENT("☾"),
	RIGHT_FACING_STAR_AND_CRESENT("☪"),
	BLACK_LARGE_SQUARE("⬛"),
	WHITE_LARGE_SQUARE("⬜"),
	BLACK_PARALLELOGRAM("▰"),
	WHITE_PARALLELOGRAM("▱"),
	HIGH_VOLTAGE("⚡");
	
	private String unicode;
	
	private Emoji(String unicode) {
		this.unicode = unicode;
	}
	
	public boolean isMinecraftCompatible() {
		return this != BOW;
	}
	
	@Override
	public String toString() {
		return this.unicode;
	}

}
