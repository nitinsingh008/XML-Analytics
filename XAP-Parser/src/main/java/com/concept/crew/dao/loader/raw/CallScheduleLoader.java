package com.concept.crew.dao.loader.raw;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.concept.crew.dao.loaderUtil.AbstractDataLoader;
import com.concept.crew.info.jaxb.CallSchedule;
import com.concept.crew.info.jaxb.Instrument;
import com.concept.crew.info.raw.InstrumentRaw;
import com.concept.crew.util.CollectionsUtil;
import com.concept.crew.util.GeneralUtil;
import com.concept.crew.util.StatementHelper;

public final class CallScheduleLoader extends AbstractDataLoader 
{

	private static final String INSERT = "  INSERT INTO CALLSCHEDULE_RAW ("
												+ "PARENT_KEY, CALLSCHEDULESOURCE, ISCALLDEFEASED) "
												+ "VALUES (?, ?, ?)";
	

	@Override
	public String getWriteQuery() 
	{
		return INSERT;
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

		List<CallSchedule> scheduleList = bond.getCallSchedule();
		
		for (int i = 0; CollectionsUtil.isNotEmpty(scheduleList) && i < scheduleList.size(); i++) 
		{
			CallSchedule schedule = scheduleList.get(i);

			if (GeneralUtil.isNotNull(schedule)) 
			{
				total++;
				int index = 1;
				StatementHelper.setLong(statement, index++, rawBond.getPkeyId());
				StatementHelper.setString(statement,index++, schedule.getCallScheduleSource());
				StatementHelper.setBoolean(statement,index++, schedule.isIsCallDefeased());
				
				statement.addBatch();
				
			}
		}

		return total;	
	}

	
}
