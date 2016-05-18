package com.concept.crew.dao.loaderUtil;

import java.util.Collection;

import com.concept.crew.dao.loaderUtil.raw.DataLoader;
import com.concept.crew.dao.loaderUtil.raw.DataLoader.BondWriter;
import com.concept.crew.info.raw.ParentInfoWrapper;

public class DataWriter {

	public DataWriter(){
		
	}
	public Collection<ParentInfoWrapper> write(Collection<ParentInfoWrapper> raw) {
		BondWriter writer = DataLoader.createWriter();
		writer.writeData(raw);
		return raw;
	}
}
