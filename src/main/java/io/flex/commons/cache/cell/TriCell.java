package io.flex.commons.cache.cell;

public interface TriCell<A, B, C> extends BiCell<A, B> {
	
	C c();
	
	public static <A, B, C> TriCell<A, B, C> of(A a, B b, C c) {
		return new TriCell<A, B, C>() {
			
			private static final long serialVersionUID = 1641058717578383435L;

			@Override
			public A a() {
				return a;
			}

			@Override
			public B b() {
				return b;
			}

			@Override
			public C c() {
				return c;
			}
		};
	}
	
}
