package org.fukkit.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	
	public String name();
	
	public String description() default "No description";

	public String[] usage() default { "/<command>" };
	
	public String[] aliases() default {};
	
}
