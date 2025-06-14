package io.flex.commons.cache;

import java.util.LinkedHashSet;
import java.util.function.BiPredicate;

public class LinkedCache<E extends Cacheable, U> extends AbstractCache<E, U> {
	
	private static final long serialVersionUID = -6489724712554431014L;

	public LinkedCache(BiPredicate<E, U> predacate) {
		super(predacate);
		this.set = new LinkedHashSet<E>(super.set);
	}

	@Override
	public void onAdd(E e) {
		//
	}

	@Override
	public void onRemove(E e) {
		//
	}

	@Override
	public void onClear() {
		//
	}
	
}