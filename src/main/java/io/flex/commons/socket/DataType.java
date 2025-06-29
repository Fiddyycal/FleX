package io.flex.commons.socket;

public enum DataType {

	/**
	 * For sending critical data updates that where guaranteed delivery is paramount.
	 */
	TCP,
	
	/**
	 * For sending lightweight announcements or fire-and-forget messages.
	 */
	UDP;
	
}
