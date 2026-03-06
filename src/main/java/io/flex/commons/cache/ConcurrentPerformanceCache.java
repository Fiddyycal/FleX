package io.flex.commons.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ConcurrentPerformanceCache<E extends Cacheable, U> extends AbstractPerformanceCache<E, U> {
	
	private static final long serialVersionUID = 1648336747398474737L;

	public ConcurrentPerformanceCache(Function<E, U> function) {
		super(new ConcurrentHashMap<U, E>(), function);
	}

	@Override
	public void onAdd(E e) {
		// Do nothing unless overriden.
	}

	@Override
	public void onRemove(E e) {
		// Do nothing unless overriden.
	}

	@Override
	public void onClear() {
		// Do nothing unless overriden.
	}

}
