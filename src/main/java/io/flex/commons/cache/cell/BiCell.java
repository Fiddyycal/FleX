package io.flex.commons.cache.cell;

public interface BiCell<A, B> extends Cell<A> {
	
	B b();
	
	public static <A, B> BiCell<A, B> of(A a, B b) {
		return new BiCell<A, B>() {
			
			private static final long serialVersionUID = -3824156236884831764L;

			@Override
			public A a() {
				return a;
			}

			@Override
			public B b() {
				return b;
			}
		};
	}
	
}
