package io.flex.commons.cache;

import java.io.Serializable;
import java.util.Collection;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Cache<E, U> extends Iterable<E>, Serializable {
	
	E get(U u);
	
	E getOrDefault(U arg0, E def);
	
	@SuppressWarnings("unchecked")
	boolean add(E... e);
	
	boolean replace(E e, E o);
	
	@SuppressWarnings("unchecked")
	boolean remove(E... o);
	
	boolean addAll(Collection<? extends E> c);
	
	boolean removeAll(Collection<? extends E> c);
	
	boolean retainAll(Collection<? extends E> c);
	
	boolean contains(E o);
	
	boolean containsAll(Collection<? extends E> c);
	
	boolean isEmpty();
	
	void clear();
	
	int size();
	
	E[] toArray();

	<T> T[] toArray(T[] a);
	
    default Spliterator<E> spliterator() {
        return Spliterators.spliteratorUnknownSize(this.iterator(), 0);
    }
    
    default Stream<E> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }
    
    default Stream<E> parallelStream() {
        return StreamSupport.stream(this.spliterator(), true);
    }

}
