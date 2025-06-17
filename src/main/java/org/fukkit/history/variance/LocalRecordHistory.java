package org.fukkit.history.variance;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.fukkit.api.helper.ConfigHelper;
import org.fukkit.consequence.EvidenceType;
import org.fukkit.entity.FleXHumanEntity;
import org.fukkit.history.History;

import io.flex.commons.utils.FileUtils;

public class LocalRecordHistory extends History<String> {

	public LocalRecordHistory(FleXHumanEntity player) {
		
		super(player);
		
		File data = new File(ConfigHelper.flow_path + File.separator + player.getUniqueId());
		
		if (!data.exists())
			return;

		String flow = EvidenceType.RECORDING_REFERENCE.getLinks()[0];
		String rec = EvidenceType.RECORDING_REFERENCE.getLinks()[1];
		
		this.log = Arrays.stream(data.listFiles()).filter(f -> {
			
			return f.getName().startsWith(flow) && f.getName().endsWith(rec);
			
		}).collect(Collectors.toMap(f -> FileUtils.getMillis(f.getName().substring(flow.length(), f.getName().length() - rec.length())), f -> f.getName()));
		
	}

}
