package com.concept.crew.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.concept.crew.dao.loaderUtil.AbstractDataWriter;
import com.concept.crew.dao.loaderUtil.raw.DataLoader;
import com.concept.crew.dao.loaderUtil.raw.DataLoader.BondReader;
import com.concept.crew.info.jaxb.Instrument;
import com.concept.crew.info.jaxb.StartingTag;
import com.concept.crew.info.raw.InstrumentRaw;
import com.concept.crew.util.CollectionsUtil;
import com.concept.crew.util.Constants;
import com.concept.crew.util.Constants.BondCopy;
import com.concept.crew.util.Group;
import com.concept.crew.util.GroupBy;


public class LoadSaveProcessor 
{
	private static final Logger log = Logger.getLogger(LoadSaveProcessor.class);

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
		
		Collection<InstrumentRaw> rawDataList = new ArrayList<InstrumentRaw>();
		
		long tempCount = 0;
		for(Instrument instrument: instruments)
		{
			InstrumentRaw raw = new InstrumentRaw();
			raw.setMarkitBond(instrument);
			
			// Generate unique Key using sequence and assign to Bond
			Long bondId = ++tempCount;
			raw.setBondId(bondId);		
			String markitPkey = createUniquekey();
			raw.setMarkitPkey(markitPkey);
			
			pkey.add(markitPkey);
			
			rawDataList.add(raw);
		}
		
		if (CollectionsUtil.isEmpty(rawDataList)) 
		{
			return pkey;
		}

		Map<BondCopy, Collection<InstrumentRaw>> groupedByBondCopy = Group.group(rawDataList, new GroupByBondCopy());
		Set<BondCopy> bondCopies = groupedByBondCopy.keySet();
		
		Collection<AbstractDataWriter> instrumentWriters 	= AbstractDataWriter.create(bondCopies);
		Collection<InstrumentRaw> 	 written 			= new ArrayList<InstrumentRaw>();
		
		for (AbstractDataWriter instrumentWriter : instrumentWriters) 
		{
			BondCopy 				  bondCopy 	= instrumentWriter.getBondCopy();
			Collection<InstrumentRaw> bondGroup = groupedByBondCopy.get(bondCopy);

			Collection<InstrumentRaw> wrote = instrumentWriter.write(bondGroup);
			written.addAll(wrote);
		}
		
		return pkey;
	}
	
	public static Map<String, List<InstrumentRaw>> loadBonds(List<String> searchKeys)
	{		
		Map<String, List<InstrumentRaw>> allMarkitBondRawMap  = new HashMap<String, List<InstrumentRaw>>();
		
		 Map<Constants.QueryIDType, Set<String>> rawQueryIds = new HashMap<Constants.QueryIDType, Set<String>>();
		 Set<String> rawMarkitPkeySet = new HashSet<String>(searchKeys);
		 rawQueryIds.put(Constants.QueryIDType.MARKIT_PKEY, rawMarkitPkeySet);
		 
		 BondReader reader = DataLoader.createReader(rawQueryIds, "Source", BondCopy.RAW);
		 Map<BondCopy, Map<Long, InstrumentRaw>> bondsReturned = reader.readBonds();
		 
		 return allMarkitBondRawMap;
	}
	

	private static String createUniquekey()
	{
		return "UniqueKey";
	}
	
	public static final class GroupByBondCopy implements GroupBy<InstrumentRaw, BondCopy> {

		@Override
		public BondCopy groupBy(InstrumentRaw bond) 
		{
			return BondCopy.RAW;
		}
	}
}
