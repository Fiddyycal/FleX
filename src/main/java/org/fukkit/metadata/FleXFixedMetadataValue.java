package org.fukkit.metadata;

/**
 * A FixedMetadataValue is a special case metadata item that contains the same
 * value forever after initialization. Invalidating a FixedMetadataValue has
 * no effect.
 * <p>
 * This class extends LazyMetadataValue for historical reasons, even though it
 * overrides all the implementation methods. it is possible that in the future
 * that the inheritance hierarchy may change.
 */
public class FleXFixedMetadataValue extends FleXMetadata {

    /**
     * Store the internal value that is represented by this fixed value.
     */
    private final Object internalValue;

    /**
     * Initializes a FixedMetadataValue with an Object
     *
     * @param owningPlugin the {@link Plugin} that created this metadata value
     * @param value the value assigned to this metadata value
     */
    public FleXFixedMetadataValue(final Object value) {
        this.internalValue = value;
    }

    @Override
    public void invalidate() {

    }

    @Override
    public Object value() {
        return internalValue;
    }

}
