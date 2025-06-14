package io.flex.commons;

public enum Tier {
	
	CUSTOM, TIER_ONE, TIER_TWO, TIER_THREE;
	
	public boolean isTier(int tier) {
		return (this.ordinal() == tier);
	}
	
	public boolean canParseAs(int tier) {
		return (this.ordinal() >= tier);
	}
	
	public boolean canParseAs(Tier tier) {
		return (this.ordinal() >= tier.ordinal());
	}

}
