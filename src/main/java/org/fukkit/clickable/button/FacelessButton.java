package org.fukkit.clickable.button;

import org.bukkit.Material;

public class FacelessButton extends PointlessButton {

	private static final long serialVersionUID = 1837802054576106416L;

	public FacelessButton(Material material) {
		super(material, "", 1);
	}
	
	public FacelessButton(Material material, short damage) {
		super(material, "", 1, damage);
	}

}
