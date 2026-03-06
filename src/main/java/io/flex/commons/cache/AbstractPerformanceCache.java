package io.flex.commons.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import io.flex.commons.utils.NumUtils;

public abstract class AbstractPerformanceCache<E extends Cacheable, U> implements Cache<E, U> {
	
	private static final long serialVersionUID = 4488362019971420136L;

	// Delegation
	protected Map<U, E> map;
	
	private Function<E, U> function;
	
	public AbstractPerformanceCache(Map<U, E> delegation, Function<E, U> function) {
		this.map = Objects.requireNonNull(delegation, "delegation must not be null");
		this.function = Objects.requireNonNull(function, "function must not be null");
	}
	
	public E getRandom() {
		
	    @SuppressWarnings("unchecked")
		E[] values = (E[]) this.map.values().toArray();
	    
	    if (values.length == 0)
	    	return null;
	    
	    return values[NumUtils.getRng().getInt(0, values.length - 1)];
	    
	}
	
	@Override
	public E get(U arg0) {
		
		if (arg0 == null)
			return null;
		
		return this.map.get(arg0);
		
	}
	
	@Override
	public E getOrDefault(U arg0, E def) {
		
		if (arg0 == null)
			return def;
		
		return this.map.getOrDefault(arg0, def);
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean add(E... args) {
		
		if (args == null)
			return false;
		
		for (E e : args) {
	        U key = this.function.apply(e);
			Objects.requireNonNull(key, "key cannot be null");
	        this.onAdd(e);
	        this.map.put(key, e);
	    }
		
		return true;
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean remove(E... args) {
		
		if (args == null)
			return false;
		
		boolean modified = false;
		
	    for (E e : args) {
	        U key = this.function.apply(e);
			Objects.requireNonNull(key, "key cannot be null");
	    	this.onRemove(e);
	        this.map.remove(key);
	        modified = true;
	    }
	    
	    return modified;
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public E replace(U key, E value) {
		
		if (key == null) {
			this.add(value);
			return value;
		}
		
	    return this.map.compute(key, (k, o) -> {
	    	
	        if (o != null)
	            this.onRemove(o);
	        
	        this.onAdd(value);
	        return value;
	        
	    });
	    
	}
	
	public boolean removeIf(Predicate<E> filter) {
		return this.map.entrySet().removeIf(e -> {
			
			 if (filter.test(e.getValue())) {
				 this.onRemove(e.getValue());
				 return true;
			 }
			 
			 return false;
			 
		});
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		
        boolean modified = false;
        
        for (E e : c) {
	        U key = this.function.apply(e);
			Objects.requireNonNull(key, "key cannot be null");
			this.onAdd(e);
			this.map.put(key, e);
			modified = true;
        }
        
        return modified;
        
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean removeAll(Collection<? extends E> c) {
		
        boolean modified = false;
        
        Set<E> lookup = new HashSet<E>(c);
        Iterator<?> it = this.map.values().iterator();
        
        while (it.hasNext()) {
			E e = (E) it.next();
            if (lookup.contains(e)) {
            	this.onRemove(e);
                it.remove();
                modified = true;
            }
        }
        
        return modified;
        
	}
	
	@Override
	public boolean retainAll(Collection<? extends E> c) {
		
		if (c == null)
			return false;
		
	    boolean modified = false;
	    
	    Set<E> retainSet = new HashSet<E>(c);
	    Iterator<E> it = this.map.values().iterator();
	    
	    while (it.hasNext()) {
	        E e = it.next();
	        if (!retainSet.contains(e)) {
	            this.onRemove(e);
	            it.remove();
	            modified = true;
	        }
	    }
	    
	    return modified;
	    
	}
	
	@Override
	public boolean contains(E o) {
		return this.map.values().contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<? extends E> c) {
		return this.map.values().containsAll(c);
	}
	
	@Override
	public boolean isEmpty() {
		return this.map.isEmpty();
	}
	
	@Override
	public Iterator<E> iterator() {
		return this.map.values().iterator();
	}
	
	@Override
	public void clear() {
		this.onClear();
		this.map.clear();
	}
	
	@Override
	public int size() {
		return this.map.size();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public E[] toArray() {
		return (E[]) this.map.values().toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return this.map.values().toArray(a);
	}
	
	public boolean load() throws Exception {
		return true;
	}
	
	public abstract void onAdd(E e);
	
	public abstract void onRemove(E e);
	
	public abstract void onClear();

}
