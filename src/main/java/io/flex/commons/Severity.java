package io.flex.commons;

import io.flex.commons.utils.StringUtils;

public enum Severity {
	
	INFO, NOTICE, WARNING, ALERT, ERROR, CRITICAL, EMERG;
	
	@Override
	public String toString() {
		return StringUtils.capitalize(this.name().toLowerCase());
	}
	
}
