package org.fukkit.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CooldownCommand {
	
	Map<UUID, Long> cooldowns_cache = new HashMap<UUID, Long>();
	
	long delay();
	
	TimeUnit timeUnit();

}
