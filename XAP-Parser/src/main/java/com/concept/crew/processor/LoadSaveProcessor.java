package com.concept.crew.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.concept.crew.dao.loaderUtil.DataWriter;
import com.concept.crew.info.jaxb.Instrument;
import com.concept.crew.info.jaxb.StartingTag;
import com.concept.crew.info.raw.ParentInfoWrapper;
import com.concept.crew.util.CollectionsUtil;
import com.concept.crew.util.QueryReaderUtil;


public class LoadSaveProcessor 
{
	private static final Logger log = Logger.getLogger(LoadSaveProcessor.class);
	private static List<Integer> uniqueKeys = new ArrayList<Integer>();
	/**
	 * Iterate over ROOT bonds, transform them and persist all Bonds in RAW
	 * tables
	 */
	public static List<String> saveData(StartingTag startingTag) throws Exception {
		log.info("Start Processing");
		List<String> pkey = new ArrayList<String>();

		if (startingTag == null)
			return pkey;

		List<Instrument> instruments = startingTag.getInstrument(); 

		Collection<ParentInfoWrapper> rawDataList = new ArrayList<ParentInfoWrapper>();

		for (Instrument instrument : instruments) {
			ParentInfoWrapper raw = new ParentInfoWrapper();
			raw.setInstrument(instrument);

			// Generate unique Key using sequence and assign to Bond
			raw.setPkeyId(fetchPrimarykey(rawDataList.size()).longValue());

			rawDataList.add(raw);
		}

		if (CollectionsUtil.isEmpty(rawDataList)) {
			return pkey;
		}
		DataWriter writer = new DataWriter();
		writer.write(rawDataList);

		return pkey;
	}

	private static Integer fetchPrimarykey(int size) {		
		try {
			if (uniqueKeys.size() <= 1) {
				uniqueKeys.addAll(QueryReaderUtil.loadNewPKeys(size));
			}

		} catch (Exception e) {
			throw new RuntimeException(	"Failed while fetching keys from Sequence" ,  e);
		}
		return uniqueKeys.remove(0);
	}
	
}
