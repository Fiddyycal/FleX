package io.flex.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
	
	String test_annotation_interface_field = "s";
	
	String test_annotation_field();
	
	String test_annotation_default_field() default "s";

}
