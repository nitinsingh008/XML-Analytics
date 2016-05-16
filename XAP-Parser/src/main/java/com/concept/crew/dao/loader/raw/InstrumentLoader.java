package com.concept.crew.dao.loader.raw;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.concept.crew.dao.loaderUtil.AbstractDataLoader;
import com.concept.crew.info.jaxb.Instrument;
import com.concept.crew.info.raw.InstrumentRaw;
import com.concept.crew.util.Constants;
import com.concept.crew.util.Constants.BondCopy;
import com.concept.crew.util.Constants.QueryIDType;
import com.concept.crew.util.Pair;
import com.concept.crew.util.ResultSetHelper;
import com.concept.crew.util.StatementHelper;



public final class InstrumentLoader extends AbstractDataLoader 
{

	private static final String RAW_INSERT = " INSERT INTO CORE_REF_DATA.INSTRUMENT_RAW "
												+ "("
												+ " PKEY, "
												+ " INSTRUMENTID, "
												+ " DATASETID,   "
												+ " VERSION,  "
												+ " NEWISSUE"
												+ ") "
												+ "VALUES (CORE_REF_DATA.INSTRUMENT_SEQ.NEXTVAL, ?, ?, ?, ?)";
	
	private static final String RAW_SELECT_MARKITPKEY = " SELECT * FROM INSTRUMENT_RAW WHERE MARKIT_PKEY "
													  + " IN ( ? )";
	
	@Override
	public Map<Pair<BondCopy, QueryIDType>, String> getReadQueryMap() 
	{
		Map<Pair<BondCopy, QueryIDType>, String> map = new HashMap<Pair<BondCopy, QueryIDType>, String>();

		map.put(new Pair<BondCopy, QueryIDType>(BondCopy.RAW, Constants.QueryIDType.MARKIT_PKEY), RAW_SELECT_MARKITPKEY);


		return map;
	}

	@Override
	public Map<BondCopy, String> getWriteQueryMap() 
	{
		Map<BondCopy, String> map = new HashMap<BondCopy, String>();
		
		map.put(BondCopy.RAW, RAW_INSERT);

		
		return map;
	}

	//@Override
	/*
	 * Reading from Database
	 * @see com.markit.refdata.core.mapper.db.Populate#populate(java.lang.Object, java.lang.Object)
	 */
	public void populate(InstrumentRaw rawBond, 
						 ResultSet 		rs) 
												throws SQLException 
	{
		//bond.setBondCopy(getBondCopy());
		rawBond.setPkeyId(ResultSetHelper.toLong(rs, "BOND_ID"));
		
		
		Instrument bond = new Instrument();
/*		rawBond.setMarkitBond(bond);
		
		bond.setPkey(ResultSetHelper.toString(rs, "PKEY"));
		bond.setPkey(ResultSetHelper.toString(rs, "TITLEOFNOTES"));
		bond.setPkey(ResultSetHelper.toString(rs, "MARKITISSUERNAME"));*/
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
		
		int index = 1;
		
		StatementHelper.setLong(statement, index++, bond.getInstrumentId());
		StatementHelper.setLong(statement, index++, bond.getDatasetId());
		StatementHelper.setLong(statement, index++, bond.getVersion());
		StatementHelper.setString(statement, index++, bond.getNewIssue());

		statement.addBatch();

		return 1;
	}

}
