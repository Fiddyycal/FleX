package io.flex.test.annotation;

import java.lang.annotation.Annotation;

import io.flex.commons.Nullable;

@SuppressWarnings("unused")
@Test(test_annotation_field = "s", test_annotation_default_field = "s") 
public class AnnotationTestClass implements AnnotationTest {
	
	@Test(test_annotation_field = "s")
	private String test_private_field = "s";
	
	@Test(test_annotation_field = "s")
	public String test_public_field = "s";
	
	@Test(test_annotation_field = "s")
	private static String test_private_static_field = "s";
	
	@Test(test_annotation_field = "s")
	public static String test_public_static_field = "s";
	
	@Test(test_annotation_field = "s")
	public static final String test_public_static_final_field = "s";
	
	@Test(test_annotation_field = "s")
	private static final String test_private_static_final_field = "s";
	
	@Test(test_annotation_field = "s")
	public AnnotationTestClass(@Test(test_annotation_field = "s") Test test_public_constructor_with_annotation) {
		
		@Test(test_annotation_field = "s")
		String test_local_variable_s = test_public_static_final_field;
		
	}
	
	@Test(test_annotation_field = "s")
	private AnnotationTestClass(@Test(test_annotation_field = "s") Object test_private_constructor) {
		
		@Test(test_annotation_field = "s")
		String test_local_variable_s = test_private_static_final_field;
		
	}
	
	@Test(test_annotation_field = "s")
	public String test_public_getter_method() {
		return this.test_public_field;
	}
	
	@Test(test_annotation_field = "s")
	public String test_public_getter_method_with_parameter(Object test_parameter) {
		return test_parameter != null ? this.test_public_field : this.test_private_field;
	}
	
	@Test(test_annotation_field = "s")
	private String test_private_getter_method() {
		return this.test_private_field;
	}
	
	@Test(test_annotation_field = "s")
	public static String test_public_static_getter_method() {
		return test_public_static_field;
	}
	
	@Test(test_annotation_field = "s")
	private static String test_private_static_getter_method() {
		return test_private_static_field;
	}
	
	@Override
	@Test(test_annotation_field = "s")
	public String test_overridden_method() {
		return test_interface_field;
	}
	
	private void test_initialize_constructor_null() {
		new AnnotationTestClass(new Test() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return null;
			}
			
			@Override
			public String test_annotation_field() {
				return Test.test_annotation_interface_field;
			}
			
			@Override
			public String test_annotation_default_field() {
				return Test.test_annotation_interface_field;
			}
			
		});
	}
	
	private void test_initialize_constructer_call_to_method_with_parameter() {
		new AnnotationTestClass(new Object()).test_public_getter_method_with_parameter(new Object());
	}
	
}
