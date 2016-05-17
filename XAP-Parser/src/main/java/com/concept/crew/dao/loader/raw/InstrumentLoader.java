package com.concept.crew.dao.loader.raw;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.concept.crew.dao.loaderUtil.AbstractDataLoader;
import com.concept.crew.info.jaxb.Instrument;
import com.concept.crew.info.raw.InstrumentRaw;
import com.concept.crew.util.StatementHelper;



public final class InstrumentLoader extends AbstractDataLoader 
{

	//private static final String INSERT = QueryReaderUtil.getTextFileContents("INSTRUMENT");
	private static final String INSERT = " INSERT INTO CORE_REF_DATA.INSTRUMENT_RAW "
												+ "("
												+ " PKEY, "
												+ " INSTRUMENTID, "
												+ " DATASETID,   "
												+ " VERSION,  "
												+ " NEWISSUE"
												+ ") "
												+ "VALUES (?, ?, ?, ?, ?)";
	@Override
	public String getWriteQuery() 
	{
		return INSERT;
	}

	
	/**
	 * 
	 */
	public int prepare(InstrumentRaw rawBond, 
					   PreparedStatement statement) 
							   				throws SQLException 
	{
		Instrument bond = rawBond.getInstrument();
		
		int index = 1;
		StatementHelper.setLong(statement, index++, rawBond.getPkeyId());
		StatementHelper.setLong(statement, index++, bond.getInstrumentId());
		StatementHelper.setLong(statement, index++, bond.getDatasetId());
		StatementHelper.setLong(statement, index++, bond.getVersion());
		StatementHelper.setString(statement, index++, bond.getNewIssue());

		statement.addBatch();

		return 1;
	}

}
