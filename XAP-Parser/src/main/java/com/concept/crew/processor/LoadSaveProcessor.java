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


public class LoadSaveProcessor 
{
	private static final Logger log = Logger.getLogger(LoadSaveProcessor.class);
	private static long  pkCounter  = 0;
	 /**
     * Iterate over ROOT bonds, transform them and persist all Bonds in RAW tables
     */
	public static List<String> saveData(StartingTag  startingTag)  throws Exception 
											
	{
		log.info("Start Processing");
		List<String> pkey = new ArrayList<String>();
		
		
		if (startingTag==null) 
			return pkey;
		
		List<Instrument> instruments = startingTag.getInstrument(); // TODO : Dyanmic
		
		Collection<ParentInfoWrapper> rawDataList = new ArrayList<ParentInfoWrapper>();
		
		
		for(Instrument instrument: instruments)
		{
			ParentInfoWrapper raw = new ParentInfoWrapper();
			raw.setInstrument(instrument);
			
			// Generate unique Key using sequence and assign to Bond
			Long pkeyId = createUniquekey();
			raw.setPkeyId(pkeyId);	

			rawDataList.add(raw);
		}
		
		if (CollectionsUtil.isEmpty(rawDataList)) 
		{
			return pkey;
		}
		DataWriter writer = new DataWriter();
		writer.write(rawDataList);
		
		return pkey;
	}

	private static Long createUniquekey()
	{
		pkCounter++;	
		return pkCounter;
	}
	
}
