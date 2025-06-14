package io.flex.commons.cache.cell;

import io.flex.commons.cache.Cacheable;

public interface MultiCell<A extends Cell<?>, B extends Cell<?>> extends Cacheable {
	A cellA();
	B cellB();
}
