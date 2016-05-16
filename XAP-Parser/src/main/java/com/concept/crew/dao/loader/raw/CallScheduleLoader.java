package com.concept.crew.dao.loader.raw;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.concept.crew.dao.loaderUtil.AbstractDataLoader;
import com.concept.crew.info.jaxb.CallSchedule;
import com.concept.crew.info.jaxb.Instrument;
import com.concept.crew.info.raw.InstrumentRaw;
import com.concept.crew.util.CollectionsUtil;
import com.concept.crew.util.Constants.BondCopy;
import com.concept.crew.util.Constants.QueryIDType;
import com.concept.crew.util.GeneralUtil;
import com.concept.crew.util.Pair;
import com.concept.crew.util.StatementHelper;

public final class CallScheduleLoader extends AbstractDataLoader 
{

	private static final String GOLDEN_INSERT = "  INSERT INTO CALLSCHEDULE_RAW ("
												+ "CALLSCHEDULESOURCE, ISCALLDEFEASED) "
												+ "VALUES (?, ?)";
	
	@Override
	public Map<Pair<BondCopy, QueryIDType>, String> getReadQueryMap() 
	{
		Map<Pair<BondCopy, QueryIDType>, String> map = new HashMap<Pair<BondCopy, QueryIDType>, String>();

		//map.put(new Pair<BondCopy, QueryIDType>(BondCopy.GOLDEN, QueryIDType.INST_ID), GOLDEN_INST_ID);


		return map;
	}

	@Override
	public Map<BondCopy, String> getWriteQueryMap() 
	{
		Map<BondCopy, String> map = new HashMap<BondCopy, String>();
		
		map.put(BondCopy.RAW, GOLDEN_INSERT);

		
		return map;
	}

	//@Override
	public void populate(InstrumentRaw bond, ResultSet rs) 
				throws SQLException {
		//bond.setBondCopy(getBondCopy());
		//bond.setSource(ResultSetHelper.toString(rs, "SOURCE"));

	}



	//@Override
	/*
	 * For inserting into Database
	 */
	public int prepare(InstrumentRaw rawBond, 
					   PreparedStatement statement) 
			throws SQLException 
	{
		Instrument bond = rawBond.getInstrument();

		int total = 0;

		List<CallSchedule> instrumentHolidayCodes = bond.getCallSchedule();
		
		for (int i = 0; CollectionsUtil.isNotEmpty(instrumentHolidayCodes) && i < instrumentHolidayCodes.size(); i++) 
		{
			CallSchedule callSchedule = instrumentHolidayCodes.get(i);

			if (GeneralUtil.isNotNull(callSchedule)) 
			{
				total++;
				int index = 1;
				//StatementHelper.setLong(statement, index++, rawBond.getPkeyId());
				StatementHelper.setString(statement,index++, callSchedule.getCallScheduleSource());
				StatementHelper.setBoolean(statement,index++, callSchedule.isIsCallDefeased());
				
				statement.addBatch();
				
			}
		}

		return total;	
	}
	
	
}
