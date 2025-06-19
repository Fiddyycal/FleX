package org.fukkit.consequence;

import org.fukkit.Fukkit;

public enum EvidenceType {
	
	IMGUR_LINK("Imgur Link", "imgur.com/", "i.imgur.com/"),
	GYAZO_LINK("Gyazo Link", "gyazo.com/", "i.gyazo.com/"),
	LIGHTSHOT_LINK("Lightshot Link", "prnt.sc/", "i.prnt.sc/"),
	
	YOUTUBE_LINK("Youtube Link", "youtube.com/"),
	
	RECORDING_REFERENCE("Flow Record Reference (Automatic)", "flow-", ".rec"),
	
	CHAT_LOG("Flow Chat Log (Automatic)", "flow-", ".chat"),
	
	NONE("None");
	
	public static final String NOT_APPLICABLE = "Not-applicable (Approved by Administration)", PROCESSING = "Processing", REDUCED = PROCESSING + " (Duration reduced until finalized)";
	
	private String name;
	
	private String[] links;
	
	private EvidenceType(String name, String... links) {
		this.name = name;
		this.links = links;
	}
	
	public String[] getLinks() {
		return this.links;
	}
	
	public boolean isVideo() {
		return (this == EvidenceType.YOUTUBE_LINK);
	}
	
	public boolean isImage() {
		return (this == EvidenceType.IMGUR_LINK) || (this == EvidenceType.GYAZO_LINK) || (this == LIGHTSHOT_LINK);
	}
	
	public boolean isChatType() {
		return (this.isImage()) || (this == CHAT_LOG);
	}
	
	public boolean isPhysicalType() {
		return (this.isVideo()) || (this == EvidenceType.RECORDING_REFERENCE);
	}
	
	public boolean isAutomatic() {
		return (this == CHAT_LOG) || (Fukkit.getFlowLineEnforcementHandler().isFlowEnabled() && this == EvidenceType.RECORDING_REFERENCE);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public static EvidenceType parse(String link) {
		for (EvidenceType evidence : EvidenceType.values()) {
			for (String l : evidence.links) {
				
				link = link.toLowerCase();
				
				boolean webLink = evidence.isImage() || evidence.isVideo();
				boolean website = webLink && (link.startsWith("http://" + l) || link.startsWith("https://" + l));
				boolean worldwideweb = webLink && (link.startsWith("www." + l) || link.startsWith("http://www." + l) || link.startsWith("https://www." + l));
				
				if (link.startsWith(l) || website || worldwideweb)
					return evidence;
				
			}
		}
		return null;
	}

}
