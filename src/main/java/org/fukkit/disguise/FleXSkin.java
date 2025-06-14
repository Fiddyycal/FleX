package org.fukkit.disguise;

import org.fukkit.Fukkit;

import io.flex.commons.cache.Cacheable;

public interface FleXSkin extends Cacheable {
	
	FleXSkin DEFAULT = Fukkit.getImplementation().getSystemSkin(FleXSkinType.DEFAULT);
	
    public String getName();
    
    public String getPropertyName();
    
    public String getValue();
    
    public String getSignature();
    
}
