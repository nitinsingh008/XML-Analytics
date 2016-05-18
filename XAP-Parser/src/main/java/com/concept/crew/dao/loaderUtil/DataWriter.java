package com.concept.crew.dao.loaderUtil;

import java.util.Collection;

import com.concept.crew.dao.loaderUtil.raw.DataLoader;
import com.concept.crew.dao.loaderUtil.raw.DataLoader.DbWriter;
import com.concept.crew.info.raw.ParentInfoWrapper;

public class DataWriter {

	public DataWriter(){
		
	}
	public Collection<ParentInfoWrapper> write(Collection<ParentInfoWrapper> raw) {
		DbWriter writer = DataLoader.createWriter();
		writer.writeData(raw);
		return raw;
	}
}
