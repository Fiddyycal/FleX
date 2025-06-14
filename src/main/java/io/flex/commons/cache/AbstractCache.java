package io.flex.commons.cache;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import io.flex.commons.utils.NumUtils;

public abstract class AbstractCache<E extends Cacheable, U> implements Cache<E, U> {
	
	private static final long serialVersionUID = -2463893855111872122L;
	
	// Delegation
	protected Set<E> set = new HashSet<E>();
	private BiPredicate<E, U> predacate;
	
	public AbstractCache(BiPredicate<E, U> predacate) {
		this.predacate = Objects.requireNonNull(predacate, "predacate must not be null");
	}
	
	public E getRandom() {
		return this.stream().collect(Collectors.toList()).get(NumUtils.getRng().getInt(0, this.size()-1));
	}
	
	@Override
	public E get(U arg0) {
		return this.set.stream().filter(e -> this.test(e, arg0)).findFirst().orElse(null);
	}
	
	@Override
	public E getOrDefault(U arg0, E def) {
		return this.set.stream().filter(e -> this.test(e, arg0)).findFirst().orElse(def);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean add(E... args) {
		return Arrays.stream(args).anyMatch(e -> {
			this.onAdd(e);
			return this.set.add(e);
		});
	}
	
	@Override
	public boolean replace(E arg0, E arg1) {
		boolean replaced = false;
		this.onRemove(arg0);
		replaced = this.set.remove(arg0);
		this.onAdd(arg1);
		return replaced && this.set.add(arg1);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean remove(E... args) {
		return Arrays.stream(args).anyMatch(e -> {
			this.onRemove(e);
			return this.set.remove(e);
		});
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
        boolean mod = false;
        for (E e : c) {
			this.onAdd(e);
			if (this.set.add(e))
            	mod = true;
        }
        return mod;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean removeAll(Collection<? extends E> c) {
        boolean mod = false;
        Iterator<?> it = this.set.iterator();
        while (it.hasNext()) {
			E e = (E) it.next();
            if (c.contains(e)) {
            	this.onRemove(e);
                it.remove();
                mod = true;
            }
        }
        return mod;
	}

	@Override
	public boolean retainAll(Collection<? extends E> c) {
		return this.set.retainAll(c);
	}

	@Override
	public boolean contains(E o) {
		return this.set.contains(o);
	}

	@Override
	public boolean containsAll(Collection<? extends E> c) {
		return this.set.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return this.set.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return this.set.iterator();
	}

	@Override
	public void clear() {
		this.onClear();
		this.set.clear();
	}

	@Override
	public int size() {
		return this.set.size();
	}

	@Override
	@SuppressWarnings("unchecked")
	public E[] toArray() {
		return (E[]) this.set.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.set.toArray(a);
	}
	
	protected boolean test(E e, U u) {
		return this.predacate.test(e, u);
	}
	
	public boolean load() throws Exception {
		return true;
	}
	
	public abstract void onAdd(E e);
	
	public abstract void onRemove(E e);
	
	public abstract void onClear();

}
