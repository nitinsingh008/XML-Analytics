package com.concept.crew.dao.loaderUtil;

import java.util.Collection;

import com.concept.crew.dao.loaderUtil.raw.DataLoader;
import com.concept.crew.dao.loaderUtil.raw.DataLoader.BondWriter;
import com.concept.crew.info.raw.InstrumentRaw;

public class DataWriter {

	public DataWriter(){
		
	}
	public Collection<InstrumentRaw> write(Collection<InstrumentRaw> raw) {
		BondWriter writer = DataLoader.createWriter();
		writer.writeData(raw);
		return raw;
	}
}
