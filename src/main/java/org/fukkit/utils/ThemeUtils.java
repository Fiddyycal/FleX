package org.fukkit.utils;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.fukkit.entity.FleXPlayer;
import org.fukkit.theme.Theme;

import io.flex.commons.file.Variable;

public class ThemeUtils {
	
	@SuppressWarnings("deprecation")
	public static Variable<?>[] getNameVariables(FleXPlayer player, Theme theme) {
		return new Variable<?>[] {

			new Variable<String>("%name%", player.getDisplayName()),
			new Variable<String>("%player%", player.getName()),
			new Variable<String>("%display%", player.getDisplayName(theme)),
			new Variable<String>("%rank%", player.getRank().getDisplay(theme, true)),
			
		};
	}
	
	public static Comparator<? super Entry<String, List<String>>> compare() {
		
		return new Comparator<Entry<String, List<String>>>() {

			@Override
			public int compare(Entry<String, List<String>> e1, Entry<String, List<String>> e2) {
				
				TagType t1 = TagType.parseTag(e1.getKey());
				TagType t2 = TagType.parseTag(e2.getKey());
				
				return t1 == null || t2 == null ? 0 : t1.ordinal() > t2.ordinal() ? 1 : -1;
				
			}
			
		};
		
	}
	
	private enum TagType {
		
		PREFIX,
		SUFFIX,
		COLOR("colour"),
		VALUE("valuing"),
		PUNCTUATION,
		RESULT("success", "failure", "severe", "reset"),
		TITLE("subtitle", "display", "lore", "description"),
		INTERACTABLE("clickable", "hoverable");
		
		private String[] tags;
		
		private TagType(String... others) {
			this.tags = others;
		}
		
		public static TagType parseTag(String tag) {
			for (TagType tags : TagType.values()) {
				
				if (tags.toString().toLowerCase().contains(tag.toLowerCase()))
					return tags;
				
				for (String t : tags.tags) {
					
					if (tags.toString().toLowerCase().contains(tag.toLowerCase())
							|| t.toLowerCase().contains(tag.toLowerCase()))
						return tags;
					
				}
				
			}
			return null;
		}
		
	}

}
