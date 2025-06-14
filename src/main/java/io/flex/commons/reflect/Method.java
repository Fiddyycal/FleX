package io.flex.commons.reflect;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class Method<T> {

	private T instance;
	
	private Class<T> cls;
	
	private String method;
	
	public Method(Class<T> cls, String method) {
		this.cls = requireNonNull(cls, "cls cannot be null");
		this.method = method;
	}
	
	public Method(T instance, String method) {
		this.instance = requireNonNull(instance, "instance cannot be null");
		this.method = method;
	}
	
	@SuppressWarnings("unchecked")
	public T init() {
		try {
			
			boolean cls = this.cls != null;
			
			if (this.method.equals("size"))
				return (T) String.valueOf(Collection.class.cast(cls ? this.cls : this.instance).size());
			
			else return (T) (cls ? this.cls : this.instance.getClass()).getMethod(this.method).invoke((cls ? this.cls : this.instance.getClass()).newInstance());
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
				NoSuchMethodException | SecurityException | InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Class<T> getClazz() {
		return this.cls;
	}
	
	public T getInstance() {
		return this.instance;
	}
	
}
