package io.flex.commons.cache.cell;

import java.io.Serializable;

import io.flex.commons.cache.Cacheable;

public interface MultiCell<A extends Cell<?>, B extends Cell<?>> extends Cacheable, Serializable {
	
	A cellA();
	B cellB();
	
	public static <A extends Cell<?>, B extends Cell<?>> MultiCell<A, B> of(A a, B b) {
		return new MultiCell<A, B>() {
			
			private static final long serialVersionUID = 1748874906363723785L;

			@Override
			public A cellA() {
				return a;
			}
			
			@Override
			public B cellB() {
				return b;
			}
			
		};
	}
	
}
