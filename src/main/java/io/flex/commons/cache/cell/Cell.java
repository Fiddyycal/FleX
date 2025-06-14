package io.flex.commons.cache.cell;

import java.io.Serializable;

import io.flex.commons.cache.Cacheable;

public interface Cell<A> extends Cacheable, Serializable {
	A a();
}
